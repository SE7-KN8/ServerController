package sebe3012.servercontroller.util.design;

import sebe3012.servercontroller.util.I18N;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sebe3012 on 18.02.2017.
 * A design for the gui
 */
public class Design {
	private Logger log = LogManager.getLogger();

	private List<String> stylesheets;
	private String id;

	public Design(URL baseFolder, String id) {
		this.stylesheets = new ArrayList<>();
		this.id = id;

		try {

			Path basePath = Paths.get(baseFolder.toURI());

			DirectoryStream<Path> folderStream = Files.newDirectoryStream(basePath);

			folderStream.forEach(path ->{
				try{
					stylesheets.add(path.toUri().toURL().toExternalForm());
				}catch (Exception e){
					throw new RuntimeException(e);
				}
		});
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
