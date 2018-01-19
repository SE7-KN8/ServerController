package se7kn8.servercontroller.addon.base.rcon;

// Source: https://developer.valvesoftware.com/wiki/Source_RCON_Protocol
public enum RConType {

	LOGIN(3),
	LOGIN_RESPONSE(2),
	COMMAND(2),
	COMMAND_RESPONSE(0),
	INVALID(-1);

	private int type;

	RConType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public static RConType getByType(int type) {
		switch (type) {
			case 3:
				return LOGIN;
			case 2:
				return COMMAND;
			case 0:
				return COMMAND_RESPONSE;
			default:
				return INVALID;
		}
	}

}
