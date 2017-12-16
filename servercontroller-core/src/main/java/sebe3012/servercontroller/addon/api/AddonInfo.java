package sebe3012.servercontroller.addon.api;

import org.jetbrains.annotations.NotNull;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sebe3012 on 28.02.2017.
 * All information about an addon
 */
@SuppressWarnings("unused")
public class AddonInfo {

	public static class AddonVersionTypeAdapter extends TypeAdapter<AddonVersion>{
		@Override
		public AddonVersion read(JsonReader in) throws IOException {
			String value = in.nextString();

			Pattern pattern = Pattern.compile("([0-9]*)[._]([0-9]*)[._]([0-9]*)[._]([0-9]*)");
			Matcher matcher = pattern.matcher(value);
			matcher.matches();

			return new AddonVersion(Integer.valueOf(matcher.group(1)), Integer.valueOf(matcher.group(2)), Integer.valueOf(matcher.group(3)), Integer.valueOf(matcher.group(4)));
		}

		@Override
		public void write(JsonWriter out, AddonVersion value) throws IOException {
			throw new UnsupportedOperationException();
		}
	}

	public static class AddonVersion implements Comparable<AddonVersion> {

		private int major = 0;
		private int minor = 0;
		private int patch = 0;
		private int build = 0;

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
