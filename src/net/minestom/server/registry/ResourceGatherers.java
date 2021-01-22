package net.minestom.server.registry;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.jar.JarFile;

import net.minestom.server.utils.NamespaceID;

/**
 * Responsible for making sure Minestom has the necessary files to run (notably registry files)
 */
public class ResourceGatherers {
	public static JarFile file;
	public ResourceGatherers(File f) {
		try {
			file=new JarFile(f);
		} catch (Exception e) {
		}
	}
	
	public static Reader read(NamespaceID name, String path) {
		try {
			return new InputStreamReader(file.getInputStream(file.getEntry("data/" + name.getDomain() + "/"+path+"/" + name.getPath() + ".json")));
		} catch (Exception e) {
			return null;
		}
	}
	
	public static void close() {
		try {
			file.close();
		} catch (Exception e) {
		}
	}
}
