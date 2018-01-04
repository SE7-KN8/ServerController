package sebe3012.servercontroller.addon.vanilla.dialog.ops;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OpsHandler {

	private String path;
	private List<Operator> operators = new ArrayList<>();
	private Logger log = LogManager.getLogger();
	private static final Gson gson = new Gson();

	public OpsHandler(String path) {
		this.path = path;
	}

	public List<Operator> getAllValues() {
		return operators;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void readOps() {
		try (Reader reader = Files.newBufferedReader(Paths.get(path))) {
			operators.clear();
			Operator[] ops = gson.fromJson(reader, Operator[].class);
			Collections.addAll(operators, ops);
			log.debug(operators);
		} catch (IOException e) {
			log.error("Error while reading operators", e);
		}

	}

}
