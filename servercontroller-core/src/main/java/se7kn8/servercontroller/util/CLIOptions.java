package se7kn8.servercontroller.util;

import se7kn8.servercontroller.ServerController;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;

/**
 * Created by se7kn8 on 17.06.2017.
 * A util class for the cli arguments
 */
public class CLIOptions {

	private static Logger log = LogManager.getLogger();

	public static void loadOptions(String[] args) {

		Options options = new Options();
		options.addOption("d","debug", false, "enable debug mode");
		options.addOption("l","lang", true, "change the default language");

		try {
			CommandLineParser parser = new DefaultParser();
			CommandLine cmd = parser.parse(options, args);

			ServerController.DEBUG = cmd.hasOption("debug");
			if(ServerController.DEBUG){
				log.info("Debug-mode enabled");
			}

			String lang = cmd.getOptionValue("lang");
			if(lang != null){
				for (Locale locale : Locale.getAvailableLocales()) {
					if (locale.getLanguage().equalsIgnoreCase(lang)) {
						log.info("Set language to {}", locale);
						Locale.setDefault(locale);
						break;
					}
				}
			}


		} catch (ParseException e) {
			log.error("Couldn't load cli-arguments", e);
		}


	}

}
