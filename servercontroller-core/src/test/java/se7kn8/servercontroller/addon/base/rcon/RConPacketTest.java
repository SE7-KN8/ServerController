package se7kn8.servercontroller.addon.base.rcon;

import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RConPacketTest {

	private RConPacket testPacket;
	private RConPacket testPacket2;
	private RConPacket testPacket3;

	@Before
	public void setUp() throws Exception {
		 testPacket = new RConPacket(1234, RConType.LOGIN, "test");
		 testPacket2 = new RConPacket(4321, RConType.COMMAND, "set test true");

		ByteBuffer buffer = ByteBuffer.allocate(25);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putInt(19);
		buffer.putInt(3333);
		buffer.putInt(RConType.COMMAND.getType());
		buffer.put("test test".getBytes(StandardCharsets.US_ASCII));
		buffer.put((byte)0);
		buffer.put((byte)0);
		testPacket3 = new RConPacket(buffer);
	}

	@Test
	public void getSize() {
		assertTrue(testPacket.getSize() == 14);
		assertTrue(testPacket2.getSize() == 23);
		assertTrue(testPacket3.getSize() == 19);
	}

	@Test
	public void getType() {
		assertTrue(testPacket.getType() == RConType.LOGIN);
		assertTrue(testPacket2.getType() == RConType.COMMAND);
		assertTrue(testPacket3.getType() == RConType.COMMAND);
	}

	@Test
	public void getPayload() {
		assertTrue(testPacket.getPayload().length == 4);
		assertTrue(testPacket2.getPayload().length == 13);
		assertTrue(testPacket3.getPayload().length == 9);
	}

	@Test
	public void getPayloadAsString() {
		assertEquals("test", testPacket.getPayloadAsString());
		assertEquals("set test true", testPacket2.getPayloadAsString());
		assertEquals("test test", testPacket3.getPayloadAsString());
	}

	@Test
	public void toByteBuffer() {

		ByteBuffer buffer = testPacket.toByteBuffer();
		assertTrue(buffer.getInt() == 14);
		assertTrue(buffer.getInt() == 1234);
		assertTrue(buffer.getInt() == RConType.LOGIN.getType());

		buffer = testPacket2.toByteBuffer();
		assertTrue(buffer.getInt() == 23);
		assertTrue(buffer.getInt() == 4321);
		assertTrue(buffer.getInt() == RConType.COMMAND.getType());

		buffer = testPacket3.toByteBuffer();
		assertTrue(buffer.getInt() == 19);
		assertTrue(buffer.getInt() == 3333);
		assertTrue(buffer.getInt() == RConType.COMMAND.getType());
	}
}