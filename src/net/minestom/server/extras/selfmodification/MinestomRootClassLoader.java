package net.minestom.server.extras.selfmodification;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import com.sun.istack.internal.NotNull;

/**
 * Class Loader that can modify class bytecode when they are loaded
 */
public class MinestomRootClassLoader extends HierarchyClassLoader {

    private static MinestomRootClassLoader INSTANCE;

    /**
     * Used to let ASM find out common super types, without actually commiting to loading them
     * Otherwise ASM would accidentally load classes we might want to modify
     */
    private final URLClassLoader asmClassLoader;

    // TODO: replace by tree to optimize lookup times. We can use the fact that package names are split with '.' to allow for fast lookup
    // TODO: eg. Node("java", Node("lang"), Node("io")). Loading "java.nio.Channel" would apply modifiers from "java", but not "java.io" or "java.lang".
    // TODO: that's an example, please don't modify standard library classes. And this classloader should not let you do it because it first asks the platform classloader

    // TODO: priorities?
    private final List<CodeModifier> modifiers = new LinkedList<>();

    private MinestomRootClassLoader(ClassLoader parent) {
        super(extractURLsFromClasspath(), parent);
        asmClassLoader = newChild(new URL[0]);
    }

    public static MinestomRootClassLoader getInstance() {
        if (INSTANCE == null) {
            synchronized (MinestomRootClassLoader.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MinestomRootClassLoader(MinestomRootClassLoader.class.getClassLoader());
                }
            }
        }
        return INSTANCE;
    }

    private static URL[] extractURLsFromClasspath() {
        String classpath = System.getProperty("java.class.path");
        String[] parts = classpath.split(";");
        URL[] urls = new URL[parts.length];
        for (int i = 0; i < urls.length; i++) {
            try {
                String part = parts[i];
                String protocol;
                if (part.contains("!")) {
                    protocol = "jar://";
                } else {
                    protocol = "file://";
                }
                urls[i] = new URL(protocol + part);
            } catch (MalformedURLException e) {
                throw new Error(e);
            }
        }
        return urls;
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> loadedClass = findLoadedClass(name);
        if (loadedClass != null)
            return loadedClass;

        try {
            // we do not load system classes by ourselves
            Class<?> systemClass = ClassLoader.getSystemClassLoader().loadClass(name);
            return systemClass;
        } catch (ClassNotFoundException e) {
            try {
                return define(name, resolve);
            } catch (Exception ex) {
                // fail to load class, let parent load
                // this forbids code modification, but at least it will load
                return super.loadClass(name, resolve);
            }
        }
    }

    private Class<?> define(String name, boolean resolve) throws IOException, ClassNotFoundException {
        try {
            byte[] bytes = loadBytes(name, true);
            Class<?> defined = defineClass(name, bytes, 0, bytes.length);
            if (resolve) {
                resolveClass(defined);
            }
            return defined;
        } catch (ClassNotFoundException e) {
            // could not load inside this classloader, attempt with children
            Class<?> defined = null;
            for (MinestomExtensionClassLoader subloader : children) {
                try {
                    defined = subloader.loadClassAsChild(name, resolve);
                    return defined;
                } catch (ClassNotFoundException e1) {
                    // not found inside this child, move on to next
                }
            }
            throw e;
        }
    }

    /**
     * Loads and possibly transforms class bytecode corresponding to the given binary name.
     *
     * @param name
     * @param transform
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public byte[] loadBytes(String name, boolean transform) throws IOException, ClassNotFoundException {
        if (name == null)
            throw new ClassNotFoundException();
        String path = name.replace(".", "/") + ".class";
        InputStream input = getResourceAsStream(path);
        if (input == null) {
            throw new ClassNotFoundException("Could not find resource " + path);
        }
        byte[] originalBytes = readAllBytes(input);
        if (transform) {
            return transformBytes(originalBytes, name);
        }
        return originalBytes;
    }

    public byte[] loadBytesWithChildren(String name, boolean transform) throws IOException, ClassNotFoundException {
        if (name == null)
            throw new ClassNotFoundException();
        String path = name.replace(".", "/") + ".class";
        InputStream input = getResourceAsStreamWithChildren(path);
        if (input == null) {
            throw new ClassNotFoundException("Could not find resource " + path);
        }
        
        byte[] originalBytes = readAllBytes(input);
        if (transform) {
            return transformBytes(originalBytes, name);
        }
        return originalBytes;
    }

    byte[] transformBytes(byte[] classBytecode, String name) {
        ClassReader reader = new ClassReader(classBytecode);
        ClassNode node = new ClassNode();
        reader.accept(node, 0);
        boolean modified = false;
        synchronized (modifiers) {
            for (CodeModifier modifier : modifiers) {
                boolean shouldModify = modifier.getNamespace() == null || name.startsWith(modifier.getNamespace());
                if (shouldModify) {
                    modified |= modifier.transform(node);
                }
            }
        }
        if (modified) {
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES) {
                @Override
                protected ClassLoader getClassLoader() {
                    return asmClassLoader;
                }
            };
            node.accept(writer);
            classBytecode = writer.toByteArray();
        }
        return classBytecode;
    }

    // overriden to increase access (from protected to public)
    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }

    @NotNull
    public URLClassLoader newChild(URL[] urls) {
        return URLClassLoader.newInstance(urls, this);
    }

    public void loadModifier(File[] originFiles, String codeModifierClass) {
        URL[] urls = new URL[originFiles.length];
        try {
            for (int i = 0; i < originFiles.length; i++) {
                urls[i] = originFiles[i].toURI().toURL();
            }
            URLClassLoader loader = newChild(urls);
            Class<?> modifierClass = loader.loadClass(codeModifierClass);
            if (CodeModifier.class.isAssignableFrom(modifierClass)) {
                CodeModifier modifier = (CodeModifier) modifierClass.getDeclaredConstructor().newInstance();
                synchronized (modifiers) {
                    addCodeModifier(modifier);
                }
            }
        } catch (MalformedURLException | ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void addCodeModifier(CodeModifier modifier) {
        synchronized (modifiers) {
            modifiers.add(modifier);
        }
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }

    public List<CodeModifier> getModifiers() {
        return modifiers;
    }
    
    public static byte[] readAllBytes(InputStream inputStream) throws IOException {
        final int bufLen = 1024;
        byte[] buf = new byte[bufLen];
        int readLen;
        IOException exception = null;

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            while ((readLen = inputStream.read(buf, 0, bufLen)) != -1)
                outputStream.write(buf, 0, readLen);

            return outputStream.toByteArray();
        } catch (IOException e) {
            exception = e;
            throw e;
        } finally {
            if (exception == null) inputStream.close();
            else try {
                inputStream.close();
            } catch (IOException e) {
                exception.addSuppressed(e);
            }
        }
    }
}
