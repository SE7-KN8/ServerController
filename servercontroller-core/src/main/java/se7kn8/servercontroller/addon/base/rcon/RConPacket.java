package se7kn8.servercontroller.addon.base.rcon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

// Source: https://developer.valvesoftware.com/wiki/Source_RCON_Protocol
public class RConPacket {
	private static Logger log = LogManager.getLogger();

	private int size;
	private int id;
	private RConType type;
	private byte[] payload;

	public RConPacket(int id, RConType type, byte[] payload) {
		this.size = Integer.BYTES + Integer.BYTES + payload.length + Byte.BYTES + Byte.BYTES;
		this.id = id;
		this.type = type;
		this.payload = payload;
		log.debug("Created rcon-package with size: {} ,id: {}, type: {} and payload: {}", size, id, type.name(), Arrays.toString(payload));
	}

	public RConPacket(int id, RConType type, String payload) {
		this(id, type, payload.getBytes(StandardCharsets.US_ASCII));
	}

	public RConPacket(ByteBuffer buffer) {
		buffer.position(0);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		this.size = buffer.getInt();
		this.id = buffer.getInt();
		this.type = RConType.getByType(buffer.getInt());
		int payloadLength = size - Integer.BYTES - Integer.BYTES - Byte.BYTES - Byte.BYTES;
		this.payload = new byte[payloadLength];
		buffer.get(payload);
		log.debug("Created rcon-package with size: {} ,id: {}, type: {} and payload: {}", size, id, type.name(), Arrays.toString(payload));
	}

	public int getSize() {
		return size;
	}

	public RConType getType() {
		return type;
	}

	public byte[] getPayload() {
		return payload;
	}

	public String getPayloadAsString() {
		return new String(payload, StandardCharsets.US_ASCII);
	}

	public ByteBuffer toByteBuffer() {
		ByteBuffer buffer = ByteBuffer.allocate(size + Integer.BYTES);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putInt(size);
		buffer.putInt(id);
		buffer.putInt(type.getType());
		buffer.put(payload);
		buffer.put((byte) 0);
		buffer.put((byte) 0);
		buffer.position(0);
		return buffer;
	}

}
