package se7kn8.servercontroller.api.addon;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AddonVersionTest {

	private AddonInfo.AddonVersion addonVersion;

	@Before
	public void before(){
		addonVersion = new AddonInfo.AddonVersion(1,4,5,6);
	}


	@Test
	public void compareTo() throws Exception {
		assertEquals(0,addonVersion.compareTo(new AddonInfo.AddonVersion(1,4,5,6)));
		assertEquals(-1, addonVersion.compareTo(new AddonInfo.AddonVersion(1,4,5,10)));
		assertEquals(1, addonVersion.compareTo(new AddonInfo.AddonVersion(1, 3,5,10)));
		assertEquals(-1, addonVersion.compareTo(new AddonInfo.AddonVersion(1,5,2,7)));
	}

}