package se7kn8.servercontroller.api.addon;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;

/**
 * Created by Sebe3012 on 28.02.2017.
 * All information about an addon
 */
@SuppressWarnings("unused")
public class AddonInfo {

	public static class AddonVersion implements Comparable<AddonVersion> {

		private int major;
		private int minor;
		private int patch;
		private int build;

		public AddonVersion(int major, int minor, int patch, int build){
			this.major = major;
			this.minor = minor;
			this.patch = patch;
			this.build = build;
		}

		@Override
		public String toString() {
			return major + "." + minor + "." + patch + "." + build;
		}

		@Override
		public int compareTo(@NotNull AddonVersion o) {
			if (compareVersions(this.major, o.major) == 0) {
				if (compareVersions(this.minor, o.minor) == 0) {
					if (compareVersions(this.patch, o.patch) == 0) {
						return compareVersions(this.build, o.build);
					} else {
						return compareVersions(this.patch, o.patch);
					}
				} else {
					return compareVersions(this.minor, o.minor);
				}
			} else {
				return compareVersions(this.major, o.major);
			}
		}

		private int compareVersions(int a, int b) {
			if (a > b) {
				return 1;
			} else if (a == b) {
				return 0;
			} else if (a < b) {
				return -1;
			}

			return 0;
		}
	}

	//This values will change by json-deserializer
	private String id;
	private String name;
	private AddonVersion version;
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

	public AddonVersion getVersion() {
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
