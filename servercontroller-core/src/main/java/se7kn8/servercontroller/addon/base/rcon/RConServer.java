package se7kn8.servercontroller.addon.base.rcon;

import se7kn8.servercontroller.api.server.NamedServer;
import se7kn8.servercontroller.api.server.ServerState;
import se7kn8.servercontroller.api.util.ErrorCode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class RConServer extends NamedServer {

	private static final int LOGIN = 3;
	private static final int COMMAND = 2;

	private static Logger log = LogManager.getLogger();

	private InputStream inputStream;
	private OutputStream outputStream;
	private Socket socket;

	private String ip;
	private String port;
	private String password;

	@Override
	public void initialize(@NotNull Map<String, String> properties) {
		super.initialize(properties);

		ip = properties.get("rconIP");
		port = properties.get("rconPort");
		password = properties.get("rconPassword");
	}

	@NotNull
	public String getIp() {
		return ip;
	}

	@NotNull
	public String getPort() {
		return port;
	}

	@NotNull
	public String getPassword() {
		return password;
	}

	@NotNull
	@Override
	public ErrorCode start() {
		try {
			setState(ServerState.STARTING);
			log.info("[{}] Try to connect to: {}:{}", getName(), getIp(), getPort());
			this.socket = new Socket(getIp(), Integer.valueOf(getPort()));
			this.outputStream = socket.getOutputStream();
			this.inputStream = socket.getInputStream();
			send(LOGIN, getPassword().getBytes(StandardCharsets.US_ASCII));
			setState(ServerState.RUNNING);
			return ErrorCode.SUCCESSFUL;
		} catch (Exception e) {
			onError(e);
			stop();
		}
		return ErrorCode.ERROR;
	}

	private byte[] send(int type, byte[] payload) throws IOException {
		int length = 4/*length*/ + 4/*request id*/ + 4/*type*/ + payload.length/*payload*/ + 2/*null-bytes*/;

		byte[] bytesToSend = new byte[length];
		ByteBuffer buffer = ByteBuffer.wrap(bytesToSend);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putInt(length);
		buffer.putInt(0);
		buffer.putInt(type);
		buffer.put(payload);
		buffer.put((byte) 0);
		buffer.put((byte) 0);

		outputStream.write(bytesToSend);
		outputStream.flush();

		byte[] responseBytes = new byte[4096];
		int receivedLength = inputStream.read(responseBytes);

		if (receivedLength == -1) {
			return new byte[]{0};
		}

		System.out.println(receivedLength);
		ByteBuffer responseBuffer = ByteBuffer.wrap(responseBytes, 0, receivedLength);
		responseBuffer.order(ByteOrder.LITTLE_ENDIAN);
		int responseLength = responseBuffer.getInt(); //length
		responseBuffer.getInt(); //id
		responseBuffer.getInt(); //type

		System.out.println(responseLength);
		byte[] message = new byte[responseLength - 4/*length*/ - 4/*type*/ - 4 /*request id*/ - 2 /*null-bytes*/];
		responseBuffer.get(message);
		return message;
	}

	@NotNull
	@Override
	public ErrorCode stop() {
		try {
			setState(ServerState.STOPPING);
			inputStream.close();
			outputStream.close();
			socket.close();
			return ErrorCode.SUCCESSFUL;
		} catch (Exception e) {
			log.error("Can't stop server: ", e);
		} finally {
			getStopListeners().forEach(listener -> listener.onStop(0));
			setState(ServerState.STOPPED);
		}
		return ErrorCode.ERROR;
	}

	@Override
	public void sendCommand(@NotNull String command) {
		getMessageListeners().forEach(listener -> {
			try {
				listener.onMessage(new String(send(COMMAND, command.getBytes(StandardCharsets.US_ASCII)), StandardCharsets.US_ASCII));
			} catch (Exception e) {
				onError(e);
			}
		});
	}

	@Override
	public int getSaveVersion() {
		return 0;
	}
}
