package net.minestom.server.plugins;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;

import me.devtec.server.configs.Data;
import me.devtec.server.utils.StreamUtils;
import me.devtec.server.utils.StringUtils;

public class Description {
	private final String main, name, web, author;
	private final File file;
	private final Collection<String> d, sd, b;
	private final Version ver;
	public Description(File file, InputStream stream) throws Exception {
		Data data=new Data();
		data.reload(StreamUtils.fromStream(stream));
		main=data.getString("main");
		if(main==null)throw new Exception("Main class of plugin cannot be null");
		name=data.getString("name");
		if(name==null)throw new Exception("Name of plugin cannot be null");
		web=data.getString("website");
		String author = data.getString("author");
		if(author==null) {
			author=data.getStringList("authors").toString();
		}
		if(author==null)throw new Exception("Author of plugin cannot be null");
		this.author=author;
		this.file=file;
		d=data.getStringList("depend");
		sd=data.getStringList("softdepend");
		b=data.getStringList("loadbefore");
		ver=new Version(data.getString("version"));
		if(ver==null)throw new Exception("Version of plugin cannot be null");
	}
	
	public String getName() {
		return name;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public Version getVersion() {
		return ver;
	}
	
	public Collection<String> getDepend() {
		return d;
	}
	
	public Collection<String> getSoftDepend() {
		return sd;
	}
	
	public Collection<String> getLoadBefore() {
		return b;
	}
	
	public String getWebsite() {
		return web;
	}
	
	public File getFile() {
		return file;
	}
	
	public String getMain() {
		return main;
	}
	
	public static class Version {
		private final String text;
		public Version(String string) {
			text=string;
		}
		
		public String toString() {
			return text;
		}
		
		public String getName() {
			return text;
		}
		
		public String getVersion() {
			return text;
		}
		
		public String asString() {
			return text;
		}
		
		public boolean isNewerThan(Version version) {
			int d = 0;
			String[] s = text.split("\\.");
			for (String f : version.asString().split("\\.")) {
				int id = StringUtils.getInt(f), bi = StringUtils.getInt(s[d++]);
				if (id < bi)return true;
			}
			return false;
		}
	}
}
