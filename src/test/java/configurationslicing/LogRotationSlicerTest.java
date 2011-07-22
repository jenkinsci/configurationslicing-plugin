package configurationslicing;

import configurationslicing.logrotator.LogRotationSlicer;
import hudson.tasks.LogRotator;
import junit.framework.TestCase;

public class LogRotationSlicerTest extends TestCase {

	public void testLogRotatorEquals() {
		doTestLogRotatorEquals(0, 0, 0, 0, true);
		doTestLogRotatorEquals(-1, -1, -1, -1, true);

		doTestLogRotatorEquals(1, 1, 1, 1, true);
		doTestLogRotatorEquals(1, 1, 2, 2, true);
		
		doTestLogRotatorEquals(1, -1, -1, -1, false);
		doTestLogRotatorEquals(-1, 1, -1, -1, false);
		doTestLogRotatorEquals(-1, -1, 1, -1, false);
		doTestLogRotatorEquals(-1, -1, -1, 1, false);
		
		doTestLogRotatorEquals(1, 1, 2, 3, false);
		doTestLogRotatorEquals(2, 3, 1, 1, false);
	}
	private void doTestLogRotatorEquals(int d1, int d2, int n1, int n2, boolean expect) {
		LogRotator r1 = new LogRotator(d1, n1);
		LogRotator r2 = new LogRotator(d2, n2);
		boolean equals = LogRotationSlicer.equals(r1, r2);
		assertEquals(expect, equals);
		assertFalse(LogRotationSlicer.equals(r1, null));
		assertFalse(LogRotationSlicer.equals(null, r1));
		assertFalse(LogRotationSlicer.equals(r2, null));
		assertFalse(LogRotationSlicer.equals(null, r2));
		
		assertTrue(LogRotationSlicer.equals(null, null));
		assertTrue(LogRotationSlicer.equals(r1, r1));
		assertTrue(LogRotationSlicer.equals(r2, r2));
	}
	
}
