package se7kn8.servercontroller.api.util.design;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by Sebe3012 on 18.02.2017.
 * A design for the gui
 */
public class Design {
	private Logger log = LogManager.getLogger();

	private List<String> stylesheets;
	private String id;
	private ResourceBundle bundle;

	public Design(String baseFolder, String id, ResourceBundle bundleToTranslate) {
		log.info("Loading design {}, in folder: '{}'", id, baseFolder);
		this.stylesheets = new ArrayList<>();
		this.id = id;
		this.bundle = bundleToTranslate;

		try {
			Path jarFile = Paths.get(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());

			if (!Files.isDirectory(jarFile)) {  // Run with jar file
				JarFile jar = new JarFile(jarFile.toFile());
				Enumeration<JarEntry> entries = jar.entries();
				while (entries.hasMoreElements()) {
					String name = entries.nextElement().getName();
					if (name.startsWith(baseFolder + "/")) {
						this.stylesheets.add(name);
					}
				}
				jar.close();
			} else { // Run with IDE
				URL url = ClassLoader.getSystemResource(baseFolder);
				if (url != null) {
					File apps = new File(url.toURI());
					for (File app : apps.listFiles()) {
						this.stylesheets.add(app.toURI().toURL().toExternalForm());
					}

				}
			}
		} catch (Exception e) {
			log.error("Unable to load design", e);
		}

	}

	List<String> getStylesheets() {
		return stylesheets;
	}

	public String getId() {
		return id;
	}

	public String getLocalizedName() {
		return bundle.getString("design_" + id);
	}

	@Override
	public String toString() {
		return getLocalizedName();
	}
}
