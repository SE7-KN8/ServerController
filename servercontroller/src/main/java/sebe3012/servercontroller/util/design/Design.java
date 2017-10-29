package sebe3012.servercontroller.util.design;

import sebe3012.servercontroller.util.I18N;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
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

	public Design(String baseFolder, String id) {
		this.stylesheets = new ArrayList<>();
		this.id = id;

		try {
			File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());

			if (jarFile.isFile()) {  // Run with jar file
				JarFile jar = new JarFile(jarFile);
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
				System.out.println(url);
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
		return I18N.translate("design_" + id);
	}

	@Override
	public String toString() {
		return getLocalizedName();
	}
}
