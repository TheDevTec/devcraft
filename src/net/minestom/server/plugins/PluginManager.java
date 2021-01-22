package net.minestom.server.plugins;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;

public class PluginManager {
	private final Map<String, Plugin> plugins = new HashMap<>();

	public void loadPlugins(File directory) {
		if(directory!=null && directory.exists())
        try {
		if(!directory.isDirectory())throw new NoSuchFileException("File must be directory");
        Map<String, Description> plugins = new HashMap<String, Description>();
        Set<String> loadedPlugins = new HashSet<String>();
        Map<String, Collection<String>> dependencies = new HashMap<String, Collection<String>>();
        Map<String, Collection<String>> softDependencies = new HashMap<String, Collection<String>>();

        // This is where it figures out all possible plugins
        for (File file : directory.listFiles()) {
        	Description description = preparePlugin(file);
        	plugins.put(description.getName(), description);

            Collection<String> softDependencySet = description.getSoftDepend();
            if (softDependencySet != null && !softDependencySet.isEmpty()) {
                if (softDependencies.containsKey(description.getName())) {
                    // Duplicates do not matter, they will be removed together if applicable
                    softDependencies.get(description.getName()).addAll(softDependencySet);
                } else {
                    softDependencies.put(description.getName(), new ArrayList<String>(softDependencySet));
                }
            }

            Collection<String> dependencySet = description.getDepend();
            if (dependencySet != null && !dependencySet.isEmpty()) {
                dependencies.put(description.getName(), new ArrayList<String>(dependencySet));
            }

            Collection<String> loadBeforeSet = description.getLoadBefore();
            if (loadBeforeSet != null && !loadBeforeSet.isEmpty()) {
                for (String loadBeforeTarget : loadBeforeSet) {
                    if (softDependencies.containsKey(loadBeforeTarget)) {
                        softDependencies.get(loadBeforeTarget).add(description.getName());
                    } else {
                        // softDependencies is never iterated, so 'ghost' plugins aren't an issue
                        Collection<String> shortSoftDependency = new ArrayList<String>();
                        shortSoftDependency.add(description.getName());
                        softDependencies.put(loadBeforeTarget, shortSoftDependency);
                    }
                }
            }
        }

        while (!plugins.isEmpty()) {
            boolean missingDependency = true;
            Iterator<String> pluginIterator = plugins.keySet().iterator();

            while (pluginIterator.hasNext()) {
                String plugin = pluginIterator.next();

                if (dependencies.containsKey(plugin)) {
                    Iterator<String> dependencyIterator = dependencies.get(plugin).iterator();

                    while (dependencyIterator.hasNext()) {
                        String dependency = dependencyIterator.next();

                        // Dependency loaded
                        if (loadedPlugins.contains(dependency)) {
                            dependencyIterator.remove();

                        // We have a dependency not found
                        } else if (!plugins.containsKey(dependency)) {
                            missingDependency = false;
                            pluginIterator.remove();
                            plugins.remove(plugin);
                            softDependencies.remove(plugin);
                            dependencies.remove(plugin);
                            break;
                        }
                    }

                    if (dependencies.containsKey(plugin) && dependencies.get(plugin).isEmpty()) {
                        dependencies.remove(plugin);
                    }
                }
                if (softDependencies.containsKey(plugin)) {
                    Iterator<String> softDependencyIterator = softDependencies.get(plugin).iterator();

                    while (softDependencyIterator.hasNext()) {
                        String softDependency = softDependencyIterator.next();

                        // Soft depend is no longer around
                        if (!plugins.containsKey(softDependency)) {
                            softDependencyIterator.remove();
                        }
                    }

                    if (softDependencies.get(plugin).isEmpty()) {
                        softDependencies.remove(plugin);
                    }
                }
                if (!(dependencies.containsKey(plugin) || softDependencies.containsKey(plugin)) && plugins.containsKey(plugin)) {
                    // We're clear to load, no more soft or hard dependencies left
                    Description file = plugins.get(plugin);
                    pluginIterator.remove();
                    plugins.remove(plugin);
                    missingDependency = false;

                    try {
                        load(file);
                        loadedPlugins.add(plugin);
                        continue;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

            if (missingDependency) {
                // We now iterate over plugins until something loads
                // This loop will ignore soft dependencies
                pluginIterator = plugins.keySet().iterator();

                while (pluginIterator.hasNext()) {
                    String plugin = pluginIterator.next();

                    if (!dependencies.containsKey(plugin)) {
                        softDependencies.remove(plugin);
                        missingDependency = false;
                        Description file = plugins.get(plugin);
                        plugins.remove(plugin);

                        try {
                            load(file);
                            loadedPlugins.add(plugin);
                            break;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                // We have no plugins left without a depend
                if (missingDependency) {
                    softDependencies.clear();
                    dependencies.clear();
                    plugins.clear();
                }
            }
        }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
	}
	
	private final void load(Description d) throws Exception {
		ClassLoader loader = URLClassLoader.newInstance(new URL[] { d.getFile().toURI().toURL() }, ClassLoader.getSystemClassLoader());
        Class<?> clazz = Class.forName(d.getMain(), true, loader);
        for (Class<?> subclazz : clazz.getClasses()) {
            Class.forName(subclazz.getName(), true, loader);
        }
        Class<? extends Plugin> typeClass = clazz.asSubclass(Plugin.class);
        Plugin type = typeClass.newInstance();
        type.desctiption=d;
        type.enabled=true;
        System.out.println("Loading plugin "+d.getName()+" "+(d.getVersion().asString().toLowerCase().startsWith("v")?d.getVersion().asString():"v"+d.getVersion().asString()));
		type.onLoad();
		System.out.println("Enabling plugin "+d.getName()+" "+(d.getVersion().asString().toLowerCase().startsWith("v")?d.getVersion().asString():"v"+d.getVersion().asString()));
		type.onEnable();
        plugins.put(d.getName(), type);
	}
	
	private final Description preparePlugin(File file) {
        try {
        	if(file.getName().endsWith(".jar")) {
	            JarFile jarFile = new JarFile(file);
	            Description desc = new Description(file, jarFile.getInputStream(jarFile.getJarEntry("plugin.yml")));
	            jarFile.close();
	            return desc;
        	}
        } catch (Exception ex) {
            ex.printStackTrace();
        }
		return null;
	}

	public void load(File file) {
		try {
			load(preparePlugin(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void unload(Plugin plugin) {
		if(plugin!=null) {
			System.out.println("Unloading plugin "+plugin.getName()+" "+(plugin.getDesctiption().getVersion().asString().toLowerCase().startsWith("v")?plugin.getDesctiption().getVersion().asString():"v"+plugin.getDesctiption().getVersion().asString()));
			disable(plugin);
			plugins.remove(plugin.getName());
		}
	}

	public void unloadPlugins() {
		plugins.values().forEach(e -> unload(e));
	}

	public void enable(Plugin plugin) {
		if(plugin!=null && !plugin.isEnabled()) {
			System.out.println("Enabling plugin "+plugin.getName()+" "+(plugin.getDesctiption().getVersion().asString().toLowerCase().startsWith("v")?plugin.getDesctiption().getVersion().asString():"v"+plugin.getDesctiption().getVersion().asString()));
			plugin.enabled=true;
			plugin.onEnable();
		}
	}

	public void disable(Plugin plugin) {
		if(plugin!=null && plugin.isEnabled()) {
			System.out.println("Disabling plugin "+plugin.getName()+" "+(plugin.getDesctiption().getVersion().asString().toLowerCase().startsWith("v")?plugin.getDesctiption().getVersion().asString():"v"+plugin.getDesctiption().getVersion().asString()));
			plugin.enabled=false;
			plugin.onDisable();
		}
	}
	
	public boolean isEnabled(String name) {
		return name!=null && getPlugin(name)!=null  && getPlugin(name).isEnabled();
	}

	public Plugin getPlugin(String name) {
		return plugins.get(name);
	}
}