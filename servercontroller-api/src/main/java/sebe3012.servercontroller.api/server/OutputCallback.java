package sebe3012.servercontroller.api.server;

import java.util.function.Consumer;

/**
 * Created by Sebe3012 on 18.02.2017.
 * This class in used in {@link BasicServer} to execute a {@link Consumer} if a line matches the given regex
 */
public class OutputCallback {

	private String regex;
	private Consumer<String> callback;
	private BasicServer server;

	OutputCallback(String regex, Consumer<String> callback, BasicServer server) {
		this.regex = regex;
		this.callback = callback;
		this.server = server;
	}

	public boolean testLine(String line) {

		boolean isSuccessful= line.matches(regex);

		if (isSuccessful) {
			callback.accept(line);
		}

		return isSuccessful;
	}

	public String getRegex() {
		return regex;
	}

	public Consumer<String> getCallback() {
		return callback;
	}
}
