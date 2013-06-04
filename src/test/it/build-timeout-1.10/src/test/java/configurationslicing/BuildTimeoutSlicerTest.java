package configurationslicing;

import configurationslicing.buildtimeout.BuildTimeoutSlicer;
import hudson.plugins.build_timeout.BuildTimeoutWrapper;
import junit.framework.TestCase;

public class BuildTimeoutSlicerTest extends TestCase {

	int timeoutMinutes = 123; 
	boolean failBuild = true;
	
	public void testNewBuildTimeoutWrapper() {
		BuildTimeoutWrapper wrapper = BuildTimeoutSlicer.BuildTimeoutSliceSpec.newBuildTimeoutWrapper(timeoutMinutes, failBuild, "absolute");
		assertEquals(timeoutMinutes, wrapper.timeoutMinutes);
		assertEquals(failBuild, wrapper.failBuild);
	}
	public void testBuildTimeoutWrapperConstructor() {
		BuildTimeoutWrapper wrapper = new BuildTimeoutWrapper(timeoutMinutes, failBuild, false, 1, 1, "");
		assertEquals(timeoutMinutes, wrapper.timeoutMinutes);
		assertEquals(failBuild, wrapper.failBuild);
	}
	
}
