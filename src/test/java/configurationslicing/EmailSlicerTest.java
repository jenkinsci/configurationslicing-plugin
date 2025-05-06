package configurationslicing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import configurationslicing.email.AbstractEmailSliceSpec;
import configurationslicing.email.CoreEmailSlicer.CoreEmailSliceSpec;
import configurationslicing.email.ExtEmailSlicer;
import configurationslicing.email.ExtEmailSlicer.ExtEmailSliceSpec;
import hudson.maven.MavenModuleSet;
import hudson.maven.MavenReporter;
import hudson.maven.reporters.MavenMailer;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.FreeStyleProject;
import hudson.plugins.emailext.EmailType;
import hudson.plugins.emailext.ExtendedEmailPublisher;
import hudson.plugins.emailext.plugins.trigger.FailureTrigger;
import hudson.tasks.Mailer;
import hudson.tasks.Publisher;
import hudson.util.DescribableList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jenkins.model.Jenkins;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class EmailSlicerTest {

    private JenkinsRule r;

    private static int projectNameCounter = 0;

    @BeforeEach
    void setUp(JenkinsRule rule) {
        r = rule;
    }

    /**
     * Given a project with a core mailer with mailer.sendToIndividuals == true, setting recipients with a slicer must
     * <ul>
     * 	<li>return null when the recipient list is set to empty (null)</li>
     *  <li>return "(Disabled)" when the recipient list is set to (disabled) (in any case, even "(dISABLED)")</li>
     *  <li>return the set list of recipients (like "john@doe.com sue@gov.com") otherwise</li>
     * @throws Exception
     */
    @Test
    void testSendToIndividuals() throws Exception {
        assertNull(
                setAndGetCoreValues(createMavenProjectWithSendToIndividualsAndEmptyRecipients(), null),
                "Setting empty recipients with sendToIndividuals enabled");
        assertEquals(
                "(Disabled)",
                setAndGetCoreValues(createMavenProjectWithSendToIndividualsAndEmptyRecipients(), "(disabled)"),
                "Setting >(disabled)< with sendToIndividuals enabled");
        assertEquals(
                "(Disabled)",
                setAndGetCoreValues(createMavenProjectWithSendToIndividualsAndEmptyRecipients(), "(DISABLED)"),
                "Setting >(DISABLED)< with sendToIndividuals enabled");
        assertEquals(
                "john@doe.com sue@gov.com",
                setAndGetCoreValues(
                        createMavenProjectWithSendToIndividualsAndEmptyRecipients(), "john@doe.com sue@gov.com"),
                "Setting recipients with sendToIndividuals enabled");

        assertNull(
                setAndGetCoreValues(createFreestyleProjectWithSendToIndividualsAndEmptyRecipients(), null),
                "Setting empty recipients with sendToIndividuals enabled");
        assertEquals(
                "(Disabled)",
                setAndGetCoreValues(createFreestyleProjectWithSendToIndividualsAndEmptyRecipients(), "(disabled)"),
                "Setting >(disabled)< with sendToIndividuals enabled");
        assertEquals(
                "(Disabled)",
                setAndGetCoreValues(createFreestyleProjectWithSendToIndividualsAndEmptyRecipients(), "(DISABLED)"),
                "Setting >(DISABLED)< with sendToIndividuals enabled");
        assertEquals(
                "john@doe.com sue@gov.com",
                setAndGetCoreValues(
                        createFreestyleProjectWithSendToIndividualsAndEmptyRecipients(), "john@doe.com sue@gov.com"),
                "Setting recipients with sendToIndividuals enabled");

        assertNull(
                setAndGetExtValues(createFreestyleProjectWithExtMailer(), null),
                "Setting empty recipients with ext mailer");
        assertEquals(
                "(Disabled)",
                setAndGetExtValues(createFreestyleProjectWithExtMailer(), "(disabled)"),
                "Setting >(disabled)< with ext mailer");
    }

    @Test
    void testNormalize() {
        doTestNormalize("email@gov.com", "email@gov.com");
        doTestNormalize("email@gov.com, CAPS@gov", "caps@gov email@gov.com");
        doTestNormalize(" ", null);
        doTestNormalize(null, null);
    }

    private void doTestNormalize(String email, String expect) {
        String normalized = new CoreEmailSliceSpec().normalize(email, " ");
        assertEquals(expect, normalized);
    }

    @Test
    void testSetValues() throws Exception {
        doTestSetValues("(Disabled)", "(Disabled)");
        doTestSetValues("caps@gov email@gov.com", "email@gov.com, CAPS@gov");
    }

    private void doTestSetValues(String expected, String valuesString) throws Exception {
        doTestSetValues(expected, valuesString, false, true);
        doTestSetValues(expected, valuesString, true, true);
        doTestSetValues(expected, valuesString, false, false);
        doTestSetValues(expected, valuesString, true, false);
    }

    private void doTestSetValues(String expected, String valuesString, boolean maven, boolean core) throws Exception {

        if (!core) {
            expected = expected.replaceAll(" ", ",");
        }

        AbstractProject project;
        if (maven) {
            String name = createUniqueProjectName();
            MavenModuleSet mavenModuleSet = Jenkins.get().createProject(MavenModuleSet.class, name);
            mavenModuleSet.setRunHeadless(true);

            project = mavenModuleSet;
        } else {
            project = r.createFreeStyleProject();
        }
        AbstractEmailSliceSpec spec;
        if (core) {
            spec = new CoreEmailSliceSpec();
        } else {
            spec = new ExtEmailSliceSpec();
        }

        List<String> values = new ArrayList<>();
        values.add(valuesString);
        spec.setValues(project, values);

        List<String> gotList = spec.getValues(project);
        String got = spec.join(gotList);

        assertEquals(expected, got);
    }

    @Test
    void testCommonValues() {
        UnorderedStringSlice<AbstractProject> slice =
                new UnorderedStringSlice<>(new ExtEmailSlicer.ExtEmailSliceSpec());
        List<String> values = slice.getConfiguredValues();
        assertEquals(3, values.size());
        assertTrue(values.contains(""));
        assertTrue(values.contains(ExtEmailSlicer.ExtEmailSliceSpec.DISABLED));
        assertTrue(values.contains("$DEFAULT_RECIPIENTS"));

        slice.add("test", Collections.singleton("value"));
        values = slice.getConfiguredValues();
        assertEquals(4, values.size());
    }

    private String setAndGetCoreValues(AbstractProject project, String valuesString) {
        CoreEmailSliceSpec spec = new CoreEmailSliceSpec();

        List<String> values = new ArrayList<>();
        values.add(valuesString);
        spec.setValues(project, values);

        List<String> gotList = spec.getValues(project);
        String got = spec.join(gotList);
        return got;
    }

    private String setAndGetExtValues(AbstractProject project, String valuesString) {
        ExtEmailSliceSpec spec = new ExtEmailSliceSpec();

        List<String> values = new ArrayList<>();
        values.add(valuesString);
        spec.setValues(project, values);

        List<String> gotList = spec.getValues(project);
        String got = spec.join(gotList);
        return got;
    }

    private MavenModuleSet createMavenProjectWithSendToIndividualsAndEmptyRecipients() throws IOException {
        String name = createUniqueProjectName();
        MavenModuleSet mavenModuleSet = Jenkins.get().createProject(MavenModuleSet.class, name);
        mavenModuleSet.setRunHeadless(true);

        MavenModuleSet mavenProject = mavenModuleSet;
        MavenMailer mailer = new MavenMailer();
        mailer.sendToIndividuals = true;
        DescribableList<MavenReporter, Descriptor<MavenReporter>> reporters = mavenProject.getReporters();
        reporters.add(mailer);
        return mavenProject;
    }

    private AbstractProject createFreestyleProjectWithSendToIndividualsAndEmptyRecipients() throws IOException {
        FreeStyleProject project = r.createFreeStyleProject();
        Mailer mailer = new Mailer();
        mailer.sendToIndividuals = true;
        DescribableList<Publisher, Descriptor<Publisher>> publishers = project.getPublishersList();
        publishers.add(mailer);
        return project;
    }

    private AbstractProject createFreestyleProjectWithExtMailer() throws IOException {
        FreeStyleProject project = r.createFreeStyleProject();
        DescribableList<Publisher, Descriptor<Publisher>> publishers = project.getPublishersList();
        ExtendedEmailPublisher publisher = new ExtendedEmailPublisher();
        FailureTrigger trigger = FailureTrigger.createDefault();
        EmailType email = new EmailType();
        email.setSendToDevelopers(true);
        email.setSendToRecipientList(true);
        trigger.setEmail(email);
        publisher.getConfiguredTriggers().add(trigger);

        // there is no way to get this text from the plugin itself
        publisher.defaultContent = "$DEFAULT_CONTENT";
        publisher.defaultSubject = "$DEFAULT_SUBJECT";

        publishers.add(publisher);
        return project;
    }

    private String createUniqueProjectName() {
        return "somestring-" + projectNameCounter++;
    }
}
