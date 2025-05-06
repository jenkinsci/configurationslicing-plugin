package configurationslicing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import configurationslicing.logfilesizechecker.LogfilesizecheckerSlicer;
import hudson.plugins.logfilesizechecker.LogfilesizecheckerWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class LogfilesizecheckerSlicerTest {

    private JenkinsRule r;

    int maxLogSize = 3;
    boolean failBuild = true;
    boolean setOwn = true;

    @BeforeEach
    void setUp(JenkinsRule rule) {
        r = rule;
    }

    @Test
    void testNewLogfilesizecheckerWrapper() {
        LogfilesizecheckerWrapper wrapper = LogfilesizecheckerSlicer.LogfilesizeSliceSpec.newLogfilesizecheckerWrapper(
                maxLogSize, failBuild, setOwn);
        assertEquals(maxLogSize, wrapper.maxLogSize);
        assertEquals(failBuild, wrapper.failBuild);
        assertEquals(setOwn, wrapper.setOwn);
    }

    @Test
    void testLogfilesizecheckerWrapperConstructor() {
        LogfilesizecheckerWrapper wrapper = new LogfilesizecheckerWrapper(maxLogSize, failBuild, setOwn);
        assertEquals(maxLogSize, wrapper.maxLogSize);
        assertEquals(failBuild, wrapper.failBuild);
        assertEquals(setOwn, wrapper.setOwn);
    }
}
