package sebe3012.servercontroller.addon.api;

import java.nio.file.Path;
import java.util.List;

/**
 * Created by Sebe3012 on 28.02.2017.
 * All information about an addon
 */
@SuppressWarnings("unused")
public class AddonInfo {

	//This values will change by json-deserializer
	private String id;
	private String name;
	private String version;
	private String mainClass;
	private List<String> authors;
	private List<String> dependencies;

	//This will not change by json-deserializer
	private transient Path jarPath;

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public String getMainClass() {
		return mainClass;
	}

	public List<String> getAuthors() {
		return authors;
	}

	public List<String> getDependencies() {
		return dependencies;
	}

	public Path getJarPath() {
		return jarPath;
	}

	public void setJarPath(Path jarPath) {
		this.jarPath = jarPath;
	}

	@Override
	public String toString() {
		return "AddonInfo{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", version='" + version + '\'' +
				", jarPath='" + jarPath + '\'' +
				", mainClass='" + mainClass + '\'' +
				", authors=" + authors +
				", dependencies=" + dependencies +
				'}';
	}
}
