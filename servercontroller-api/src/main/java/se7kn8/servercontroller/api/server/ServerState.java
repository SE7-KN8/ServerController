package se7kn8.servercontroller.api.server;

import org.jetbrains.annotations.NotNull;

import javafx.scene.paint.Color;

/**
 * Created by Sebe3012 on 28.01.2017.
 * All states that a {@link BasicServer} can have
 */
public enum ServerState {
	STARTING(Color.YELLOW),
	RUNNING(Color.LIGHTGREEN),
	STOPPING(Color.RED),
	STOPPED(Color.GRAY);

	private final Color color;

	ServerState(@NotNull Color color) {
		this.color = color;
	}

	@NotNull
	public Color getColor() {
		return color;
	}

}
