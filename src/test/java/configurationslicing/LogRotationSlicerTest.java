package configurationslicing;

import java.util.HashSet;
import java.util.Set;

import org.jvnet.hudson.test.HudsonTestCase;

import hudson.model.AbstractProject;
import hudson.tasks.LogRotator;
import configurationslicing.logrotator.LogRotationSlicer;
import configurationslicing.logrotator.LogRotationSlicer.LogRotationBuildsSliceSpec;
import configurationslicing.logrotator.LogRotationSlicer.LogRotationDaysSliceSpec;

public class LogRotationSlicerTest extends HudsonTestCase {

	@SuppressWarnings("unchecked")
	public void testSetValues() throws Exception {
		AbstractProject item = createFreeStyleProject();
		
	    int daysToKeep = 111;
	    int numToKeep = 222;
	    int artifactDaysToKeep = 333;
	    int artifactNumToKeep = 444;
	    
		LogRotator lr = new LogRotator(daysToKeep, numToKeep, artifactDaysToKeep, artifactNumToKeep);
		assertEquals(lr, daysToKeep, numToKeep, artifactDaysToKeep, artifactNumToKeep);
		
		item.setLogRotator(lr);
		assertEquals(item.getLogRotator(), daysToKeep, numToKeep, artifactDaysToKeep, artifactNumToKeep);
		
		numToKeep = 12345;
		Set<String> set = new HashSet<String>();
		set.add(String.valueOf(numToKeep));
		
		LogRotationBuildsSliceSpec buildsSpec = new LogRotationBuildsSliceSpec();
		buildsSpec.setValues(item, set);
		assertEquals(item.getLogRotator(), daysToKeep, numToKeep, artifactDaysToKeep, artifactNumToKeep);

		
		daysToKeep = 54321;
		set = new HashSet<String>();
		set.add(String.valueOf(daysToKeep));
		
		LogRotationDaysSliceSpec daysSpec = new LogRotationDaysSliceSpec();
		daysSpec.setValues(item, set);
		assertEquals(item.getLogRotator(), daysToKeep, numToKeep, artifactDaysToKeep, artifactNumToKeep);
	}
	
	private void assertEquals(LogRotator lr, int daysToKeep, int numToKeep, int artifactDaysToKeep, int artifactNumToKeep) {
		assertEquals(daysToKeep, lr.getDaysToKeep());
		assertEquals(numToKeep, lr.getNumToKeep());
		assertEquals(artifactDaysToKeep, lr.getArtifactDaysToKeep());
		assertEquals(artifactNumToKeep, lr.getArtifactNumToKeep());
	}
	
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
		LogRotator r1 = new LogRotator(d1, n1, 0, 0);
		LogRotator r2 = new LogRotator(d2, n2, 0, 0);
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
