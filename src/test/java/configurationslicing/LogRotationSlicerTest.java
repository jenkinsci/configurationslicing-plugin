package configurationslicing;

import static org.junit.jupiter.api.Assertions.*;

import configurationslicing.logrotator.LogRotationSlicer;
import configurationslicing.logrotator.LogRotationSlicer.LogRotationBuildsSliceSpec;
import configurationslicing.logrotator.LogRotationSlicer.LogRotationDaysSliceSpec;
import hudson.model.AbstractProject;
import hudson.tasks.LogRotator;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class LogRotationSlicerTest {

    private JenkinsRule r;

    @BeforeEach
    void setUp(JenkinsRule rule) {
        r = rule;
    }

    @Test
    void testSetValues() throws Exception {
        AbstractProject item = r.createFreeStyleProject();

        int daysToKeep = 111;
        int numToKeep = 222;
        int artifactDaysToKeep = 333;
        int artifactNumToKeep = 444;

        LogRotator lr = new LogRotator(daysToKeep, numToKeep, artifactDaysToKeep, artifactNumToKeep);
        equalsLogRotator(lr, daysToKeep, numToKeep, artifactDaysToKeep, artifactNumToKeep);

        item.setLogRotator(lr);
        equalsLogRotator(item.getLogRotator(), daysToKeep, numToKeep, artifactDaysToKeep, artifactNumToKeep);

        numToKeep = 12345;
        List<String> set = new ArrayList<>();
        set.add(String.valueOf(numToKeep));

        LogRotationBuildsSliceSpec buildsSpec = new LogRotationBuildsSliceSpec();
        buildsSpec.setValues(item, set);
        equalsLogRotator(item.getLogRotator(), daysToKeep, numToKeep, artifactDaysToKeep, artifactNumToKeep);

        daysToKeep = 54321;
        set = new ArrayList<>();
        set.add(String.valueOf(daysToKeep));

        LogRotationDaysSliceSpec daysSpec = new LogRotationDaysSliceSpec();
        daysSpec.setValues(item, set);
        equalsLogRotator(item.getLogRotator(), daysToKeep, numToKeep, artifactDaysToKeep, artifactNumToKeep);
    }

    private void equalsLogRotator(
            LogRotator lr, int daysToKeep, int numToKeep, int artifactDaysToKeep, int artifactNumToKeep) {
        assertEquals(daysToKeep, lr.getDaysToKeep());
        assertEquals(numToKeep, lr.getNumToKeep());
        assertEquals(artifactDaysToKeep, lr.getArtifactDaysToKeep());
        assertEquals(artifactNumToKeep, lr.getArtifactNumToKeep());
    }

    @Test
    void testLogRotatorEquals() {
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
