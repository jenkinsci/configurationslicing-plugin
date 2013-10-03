package configurationslicing;

import configurationslicing.logfilesizechecker.LogfilesizecheckerSlicer;
import hudson.plugins.logfilesizechecker.LogfilesizecheckerWrapper;
import junit.framework.TestCase;

public class LogfilesizecheckerSlicerTest extends TestCase {

	int maxLogSize = 3; 
	boolean failBuild = true;
	boolean setOwn = true;
	
	public void testNewLogfilesizecheckerWrapper() {
	    LogfilesizecheckerWrapper wrapper = LogfilesizecheckerSlicer.LogfilesizeSliceSpec.newLogfilesizecheckerWrapper(maxLogSize, failBuild, setOwn);
		assertEquals(maxLogSize, wrapper.maxLogSize);
		assertEquals(failBuild, wrapper.failBuild);
        assertEquals(setOwn, wrapper.setOwn);
	}
	public void testLogfilesizecheckerWrapperConstructor() {
	    LogfilesizecheckerWrapper wrapper = new LogfilesizecheckerWrapper(maxLogSize, failBuild, setOwn);
		assertEquals(maxLogSize, wrapper.maxLogSize);
		assertEquals(failBuild, wrapper.failBuild);
        assertEquals(setOwn, wrapper.setOwn);
	}
}