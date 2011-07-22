package configurationslicing;

import configurationslicing.jdk.JdkSlicer;
import hudson.model.JDK;
import junit.framework.TestCase;

public class JdkSlicerTest extends TestCase {

	public void testJdkEquals() {
		doTestJdkEquals("h1", "h2", "n1", "n2", false);
		doTestJdkEquals("h1", "h1", "n1", "n2", false);
		doTestJdkEquals("h1", "h2", "n1", "n1", false);
		doTestJdkEquals("h1", "h1", "n1", "n1", true);

		doTestJdkEquals(null, null, null, null, true);
		doTestJdkEquals("h1", null, null, null, false);
		doTestJdkEquals(null, null, "n1", null, false);
	}
	private void doTestJdkEquals(String h1, String h2, String n1, String n2, boolean expect) {
		JDK j1 = new JDK(n1, h1);
		JDK j2 = new JDK(n2, h2);
		assertEquals(expect, JdkSlicer.JdkSlicerSpec.equals(j1, j2));
		
		assertTrue(JdkSlicer.JdkSlicerSpec.equals(j1, j1));
		assertTrue(JdkSlicer.JdkSlicerSpec.equals(j2, j2));
		assertTrue(JdkSlicer.JdkSlicerSpec.equals(null, null));

		assertFalse(JdkSlicer.JdkSlicerSpec.equals(j1, null));
		assertFalse(JdkSlicer.JdkSlicerSpec.equals(j2, null));
		assertFalse(JdkSlicer.JdkSlicerSpec.equals(null, j1));
		assertFalse(JdkSlicer.JdkSlicerSpec.equals(null, j2));
	}
	
}
