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
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

// Source: https://developer.valvesoftware.com/wiki/Source_RCON_Protocol
public class RConServer extends NamedServer {
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
			send(RConType.LOGIN, getPassword().getBytes(StandardCharsets.US_ASCII));
			setState(ServerState.RUNNING);
			return ErrorCode.SUCCESSFUL;
		} catch (Exception e) {
			onError(e);
			stop();
		}
		return ErrorCode.ERROR;
	}

	private byte[] send(RConType type, byte[] payload) throws IOException {
		System.out.println(Arrays.toString(payload));
		outputStream.write(new RConPacket(1234, type, payload).toByteBuffer().array());
		outputStream.flush();

		byte[] responseBytes = new byte[4096];
		int receivedLength = inputStream.read(responseBytes);
		byte[] dataBytes = Arrays.copyOf(responseBytes, receivedLength);
		System.out.println(Arrays.toString(dataBytes));
		ByteBuffer responseBuffer = ByteBuffer.wrap(dataBytes);
		return new RConPacket(responseBuffer).getPayload();
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
			sendStop(0);
			setState(ServerState.STOPPED);
		}
		return ErrorCode.ERROR;
	}

	@Override
	public void sendCommand(@NotNull String command) {
		try {
			sendLine(new String(send(RConType.COMMAND, command.getBytes(StandardCharsets.US_ASCII)), StandardCharsets.US_ASCII));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getSaveVersion() {
		return 0;
	}
}
