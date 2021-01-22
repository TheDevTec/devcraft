package net.minestom.server.extras.selfmodification;

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.List;

/**
 * Classloader part of a hierarchy of classloader
 */
public abstract class HierarchyClassLoader extends URLClassLoader {
    protected final List<MinestomExtensionClassLoader> children = new LinkedList<>();

    public HierarchyClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public void addChild(MinestomExtensionClassLoader loader) {
        children.add(loader);
    }

    public InputStream getResourceAsStreamWithChildren(String name) {
        InputStream in = getResourceAsStream(name);
        if(in != null) return in;

        for(MinestomExtensionClassLoader child : children) {
            InputStream childInput = child.getResourceAsStreamWithChildren(name);
            if(childInput != null)
                return childInput;
        }
        return null;
    }

    public void removeChildInHierarchy(MinestomExtensionClassLoader child) {
        children.remove(child);
        children.forEach(c -> c.removeChildInHierarchy(child));
    }
}
