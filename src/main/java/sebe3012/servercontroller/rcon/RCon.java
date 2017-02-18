package sebe3012.servercontroller.rcon;

import sebe3012.servercontroller.ServerController;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class RCon implements Closeable {

	private InputStream input;
	private OutputStream output;
	private Socket socket;
	private char[] password;
	private int requestID;
	private int port;
	private String host;

	public static final int LOGIN = 3;
	public static final int COMMAND = 2;
	public static final int COMMAND_RESPONSE = 0;

	public RCon(String host, int port, char[] password) {
		this.host = host;
		this.port = port;
		this.password = password;
	}

	public void loadConnection() throws IOException {
		System.out.println("Try connect to " + host + ":" + port);
		this.socket = new Socket(host, port);
		this.output = socket.getOutputStream();
		this.input = socket.getInputStream();

		byte[] password = new byte[this.password.length];
		for (int i = 0; i < this.password.length; i++) {
			password[i] = (byte) this.password[i];
		}

		send(LOGIN, password);

	}

	private byte[] send(int type, byte[] payload) throws IOException {

		byte[] receivedPayload;

		int sendLength = 4 + 4 + payload.length + 2;
		byte[] sendBytes = new byte[4 + sendLength];

		ByteBuffer sendBuffer = ByteBuffer.wrap(sendBytes);
		sendBuffer.order(ByteOrder.LITTLE_ENDIAN);
		sendBuffer.putInt(sendLength);
		sendBuffer.putInt(requestID);
		sendBuffer.putInt(type);
		sendBuffer.put(payload);
		sendBuffer.put((byte) 0);
		sendBuffer.put((byte) 0);

		output.write(sendBytes);
		output.flush();

		byte[] receivedBytes = new byte[2048 * 2];
		int receivedBytesLength = input.read(receivedBytes);
		ByteBuffer receivedBuffer = ByteBuffer.wrap(receivedBytes, 0, receivedBytesLength);

		receivedBuffer.order(ByteOrder.LITTLE_ENDIAN);

		int receivedLength = receivedBuffer.getInt();
		int receivedRequestID = receivedBuffer.getInt();
		int receivedType = receivedBuffer.getInt();

		if (ServerController.DEBUG) {
			System.out.println(host + "-RCon-Connection received package: Length: " + receivedLength + " RequestID: "
					+ receivedRequestID + " Type: " + receivedType);
		}

		receivedPayload = new byte[receivedLength - 4 - 4 - 2];
		receivedBuffer.get(receivedPayload);
		receivedBuffer.get(new byte[2]);

		return receivedPayload;
	}

	private String send(int type, String payload) throws IOException {
		return new String(send(type, payload.getBytes(StandardCharsets.US_ASCII)), StandardCharsets.US_ASCII);
	}

	public String sendCommand(String command) throws IOException {
		return send(COMMAND, command);
	}

	@Override
	public void close() throws IOException {

		socket.close();
		input.close();
		output.close();

	}

}
