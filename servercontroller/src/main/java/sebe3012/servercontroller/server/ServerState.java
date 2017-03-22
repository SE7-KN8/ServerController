package sebe3012.servercontroller.server;

import javafx.scene.paint.Color;

/**
 * Created by Sebe3012 on 28.01.2017.
 * All states that a {@link BasicServer} can have
 */
public enum ServerState {
	STARTING,
	RUNNING,
	STOPPING,
	STOPPED;

	public static Color getColor(ServerState state){
		switch (state){
			case STARTING:
				return Color.YELLOW;
			case RUNNING:
				return Color.LIGHTGREEN;
			case STOPPING:
				return Color.RED;
			case STOPPED:
				return Color.GREY;
			default:
				return Color.WHITE;
		}
	}

}
