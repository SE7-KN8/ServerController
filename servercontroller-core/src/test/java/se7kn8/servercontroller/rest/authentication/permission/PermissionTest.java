package se7kn8.servercontroller.rest.authentication.permission;

import se7kn8.servercontroller.rest.authentication.permission.node.GlobNode;
import se7kn8.servercontroller.rest.authentication.permission.node.NamedPermissionNode;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PermissionTest {

	private Permission pem1;
	private Permission pem2;
	private Permission pem3;

	private Gson gson;

	@Before
	public void setUp() {
		pem1 = new Permission();
		pem2 = new Permission(new NamedPermissionNode("servercontroller"), new NamedPermissionNode("test"), new NamedPermissionNode("permission"), new GlobNode());
		pem3 = new Permission(new NamedPermissionNode("servercontroller"), new NamedPermissionNode("permission"), new NamedPermissionNode("set"));
		gson = new Gson();
	}

	@Test
	public void getPermission() {
		assertEquals("", pem1.getPermission());
		assertEquals("servercontroller.test.permission.*", pem2.getPermission());
		assertEquals("servercontroller.permission.set", pem3.getPermission());
	}

	@Test
	public void getNodes() {
		assertEquals(0, pem1.getNodes().size());
		assertEquals(4, pem2.getNodes().size());
		assertEquals(3, pem3.getNodes().size());
	}

	@Test
	public void isPermissionValid() {
		assertFalse(pem1.isPermissionValid(""));
		assertFalse(pem1.isPermissionValid("servercontroller.test"));

		assertTrue(pem2.isPermissionValid("servercontroller.test.permission.test"));
		assertTrue(pem2.isPermissionValid("servercontroller.test.permission.test.test.test"));
		assertFalse(pem2.isPermissionValid("servercontroller.test.permission"));
		assertFalse(pem2.isPermissionValid("servercontroller.test.test"));

		assertTrue(pem3.isPermissionValid("servercontroller.permission.set"));
		assertFalse(pem3.isPermissionValid("servercontroller.permission.get"));
	}

	@Test
	public void toJson() {
		assertEquals("{\"nodes\":[]}", Permission.toJson(pem1).toString());
		assertEquals("{\"nodes\":[{\"type\":\"NAMED\",\"path\":\"servercontroller\"},{\"type\":\"NAMED\",\"path\":\"test\"},{\"type\":\"NAMED\",\"path\":\"permission\"},{\"type\":\"GLOB\",\"path\":\"*\"}]}", Permission.toJson(pem2).toString());
		assertEquals("{\"nodes\":[{\"type\":\"NAMED\",\"path\":\"servercontroller\"},{\"type\":\"NAMED\",\"path\":\"permission\"},{\"type\":\"NAMED\",\"path\":\"set\"}]}", Permission.toJson(pem3).toString());
	}

	@Test
	public void fromJson() {
		assertEquals(pem1.getPermission(), Permission.fromJson(Permission.toJson(pem1)).getPermission());
		assertEquals(pem2.getPermission(), Permission.fromJson(Permission.toJson(pem2)).getPermission());
		assertEquals(pem3.getPermission(), Permission.fromJson(Permission.toJson(pem3)).getPermission());
	}
}