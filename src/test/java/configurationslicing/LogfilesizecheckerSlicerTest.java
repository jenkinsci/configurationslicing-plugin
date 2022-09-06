package configurationslicing;

import configurationslicing.logfilesizechecker.LogfilesizecheckerSlicer;
import hudson.plugins.logfilesizechecker.LogfilesizecheckerWrapper;

import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.junit.Assert.*;

public class LogfilesizecheckerSlicerTest {

        @Rule public final JenkinsRule r = new JenkinsRule();

        int maxLogSize = 3;
	boolean failBuild = true;
	boolean setOwn = true;

        @Test
	public void testNewLogfilesizecheckerWrapper() {
	    LogfilesizecheckerWrapper wrapper = LogfilesizecheckerSlicer.LogfilesizeSliceSpec.newLogfilesizecheckerWrapper(maxLogSize, failBuild, setOwn);
		assertEquals(maxLogSize, wrapper.maxLogSize);
		assertEquals(failBuild, wrapper.failBuild);
        assertEquals(setOwn, wrapper.setOwn);
	}

        @Test
	public void testLogfilesizecheckerWrapperConstructor() {
	    LogfilesizecheckerWrapper wrapper = new LogfilesizecheckerWrapper(maxLogSize, failBuild, setOwn);
		assertEquals(maxLogSize, wrapper.maxLogSize);
		assertEquals(failBuild, wrapper.failBuild);
        assertEquals(setOwn, wrapper.setOwn);
	}
}
