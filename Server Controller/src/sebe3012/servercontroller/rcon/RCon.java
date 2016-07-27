package sebe3012.servercontroller.rcon;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

import sebe3012.servercontroller.ServerController;

public class RCon {

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

	public void loadConnection() {

		try {

			this.socket = new Socket(host, port);
			this.output = socket.getOutputStream();
			this.input = socket.getInputStream();

			byte[] password = new byte[this.password.length];
			for (int i = 0; i < this.password.length; i++) {
				password[i] = (byte) this.password[i];
			}

			send(LOGIN, password);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private byte[] send(int type, byte[] payload) throws IOException {

		int lenght = 18 + payload.length;
		byte[] sendBytes = new byte[4 + lenght];

		ByteBuffer sendBuffer = ByteBuffer.wrap(sendBytes);
		sendBuffer.order(ByteOrder.LITTLE_ENDIAN);
		sendBuffer.putInt(lenght);
		sendBuffer.putInt(requestID);
		sendBuffer.putInt(type);
		sendBuffer.put(payload);
		sendBuffer.put((byte) 0);
		sendBuffer.put((byte) 0);

		output.write(sendBytes);
		output.flush();

		byte[] receivedBytes = new byte[4096];
		int receivedBytesLenght = input.read(receivedBytes);
		ByteBuffer receivedBuffer = ByteBuffer.wrap(receivedBytes, 0, receivedBytesLenght);
		receivedBuffer.order(ByteOrder.LITTLE_ENDIAN);

		int receivedLenght = receivedBuffer.getInt();
		int receivedRequestID = receivedBuffer.getInt();
		int receivedType = receivedBuffer.getInt();
		byte[] receivedPayload = new byte[receivedBytesLenght - 10];
		receivedBuffer.get(receivedPayload);
		receivedBuffer.get(new byte[2]);

		if (ServerController.DEBUG) {
			System.out.println(host + "-RCon-Connection received package: Lenght: " + receivedLenght + " RequestID: "
					+ receivedRequestID + " Type: " + receivedType);
		}

		return receivedPayload;
	}

	private String send(int type, String payload) throws IOException {
		return new String(send(type, payload.getBytes(StandardCharsets.US_ASCII)), StandardCharsets.US_ASCII);
	}

	public String sendCommand(String command) {
		try {
			return send(COMMAND, command);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
