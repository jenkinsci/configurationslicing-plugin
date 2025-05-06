package configurationslicing.claim;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import configurationslicing.claim.ClaimSlicer.ClaimSpec;
import hudson.maven.MavenModuleSet;
import hudson.model.AbstractProject;
import jenkins.model.Jenkins;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class ClaimSlicerTest {

    private JenkinsRule r;

    private static int projectNameCounter = 0;

    @BeforeEach
    void setUp(JenkinsRule rule) {
        r = rule;
    }

    /*
     * Test that we can interrogate and set values using the claim slicer on free style projects
     */
    @Test
    void testFreeStyleValues() throws Exception {
        AbstractProject item = r.createFreeStyleProject();
        doTestValues(item);
    }

    /*
     * Test that we can interrogate and set values using the claim slicer on maven projects
     */

    @Test
    void testMavenValues() throws Exception {
        String name = createUniqueProjectName();
        MavenModuleSet mavenModuleSet = Jenkins.get().createProject(MavenModuleSet.class, name);
        mavenModuleSet.setRunHeadless(true);

        AbstractProject item = mavenModuleSet;
        doTestValues(item);
    }

    /*
     * Test that the is loaded method returns true if the claim plug-in is loaded.
     * The test that this method returns false otherwise is problematic to test meaningfully in the unit test environment
     */
    @Test
    void testIsLoaded() {
        ClaimSlicer slicer = new ClaimSlicer();
        boolean isLoaded = slicer.isLoaded();
        assertTrue(isLoaded, "Expect claim slicer to be loaded when we have the claim plugin");
    }

    private void doTestValues(AbstractProject item) {
        ClaimSpec spec = new ClaimSpec();
        boolean claimsEnabled = spec.getValue(item);
        assertFalse(claimsEnabled, "Claims should be disabled on a new project");

        boolean valueSet = spec.setValue(item, false);
        assertTrue(valueSet, "disabling a value when it is already disabled should work");

        valueSet = spec.setValue(item, true);
        assertTrue(valueSet, "setting a value when it is disabled should work");

        claimsEnabled = spec.getValue(item);
        assertTrue(claimsEnabled, "Claims should be enabled after they have been set");

        valueSet = spec.setValue(item, true);
        assertTrue(valueSet, "setting a value when it is already enabled should work");

        valueSet = spec.setValue(item, false);
        assertTrue(valueSet, "removing the publisher when it is enabled should work");

        claimsEnabled = spec.getValue(item);
        assertFalse(claimsEnabled, "Claims should be disabled after they have been unset");
    }

    private String createUniqueProjectName() {
        return "somestring-" + projectNameCounter++;
    }
}
