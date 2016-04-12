package configurationslicing;

import configurationslicing.maven.MavenSnapshotBuildTrigger.MavenSnapshotBuildTriggerSlicerSpec;
import hudson.maven.MavenModuleSet;
import org.jvnet.hudson.test.HudsonTestCase;

/**
 * Created by jbischoff on 4/12/16.
 */
public class MavenSnapshotBuildTriggerTest extends HudsonTestCase {

    public void testEnableSnapshotBuildTrigger() {
        // Create the class under test
        MavenSnapshotBuildTriggerSlicerSpec slicerSpec = new MavenSnapshotBuildTriggerSlicerSpec();
        // Create a mock for the MavenModuleSet a.k.a. the project(s) being modified
        // The version of EasyMock in use in this project doesn't seem to support mocking of concrete classes -- upgrade desirable
        //MavenModuleSet mavenModuleSet = EasyMock.createMock(MavenModuleSet.class);
        MavenModuleSet mavenModuleSet = new MavenModuleSet("mock");
        // Set the Snapshot builds trigger setting to 'true'
        slicerSpec.setValue(mavenModuleSet, true);
        // Check that the property was set correctly
        assertTrue(slicerSpec.getValue(mavenModuleSet));
        // Check that the underlying MavenModuleSet has the correct value
        // In this case, setting the build trigger to 'true' means setting the ignoreUpstremChanges flag to 'false'
        assertFalse(mavenModuleSet.ignoreUpstremChanges());
    }

    public void testDisableSnapshotBuildTrigger() {
        // Create the class under test
        MavenSnapshotBuildTriggerSlicerSpec slicerSpec = new MavenSnapshotBuildTriggerSlicerSpec();
        // Create a mock for the MavenModuleSet a.k.a. the project(s) being modified
        // The version of EasyMock in use in this project doesn't seem to support mocking of concrete classes -- upgrade desirable
        //MavenModuleSet mavenModuleSet = EasyMock.createMock(MavenModuleSet.class);
        MavenModuleSet mavenModuleSet = new MavenModuleSet("mock");
        // Set the Snapshot builds trigger setting to 'false'
        slicerSpec.setValue(mavenModuleSet, false);
        // Check that the property was set correctly
        assertFalse(slicerSpec.getValue(mavenModuleSet));
        // Check that the underlying MavenModuleSet has the correct value
        // In this case, setting the build trigger to 'false' means setting the ignoreUpstremChanges flag to 'true'
        assertTrue(mavenModuleSet.ignoreUpstremChanges());
    }

}
