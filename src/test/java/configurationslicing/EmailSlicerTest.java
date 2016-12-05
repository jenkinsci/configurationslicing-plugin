package configurationslicing;


import hudson.maven.MavenReporter;
import hudson.maven.MavenModuleSet;
import hudson.maven.reporters.MavenMailer;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.FreeStyleProject;
import hudson.plugins.emailext.EmailType;
import hudson.plugins.emailext.ExtendedEmailPublisher;
import hudson.plugins.emailext.plugins.trigger.FailureTrigger;
import hudson.tasks.Publisher;
import hudson.tasks.Mailer;
import hudson.util.DescribableList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jenkins.model.Jenkins;

import org.jvnet.hudson.test.HudsonTestCase;

import configurationslicing.email.AbstractEmailSliceSpec;
import configurationslicing.email.CoreEmailSlicer.CoreEmailSliceSpec;
import configurationslicing.email.ExtEmailSlicer;
import configurationslicing.email.ExtEmailSlicer.ExtEmailSliceSpec;

public class EmailSlicerTest extends HudsonTestCase {
	
	/**
	 * Given a project with a core mailer with mailer.sendToIndividuals == true, setting recipients with a slicer must 
	 * <ul>
	 * 	<li>return null when the recipient list is set to empty (null)</li>
	 *  <li>return "(Disabled)" when the recipient list is set to (disabled) (in any case, even "(dISABLED)")</li>
	 *  <li>return the set list of recipients (like "john@doe.com sue@gov.com") otherwise</li>
	 * @throws Exception
	 */
	public void testSendToIndividuals() throws Exception {
		assertEquals("Setting empty recipients with sendToIndividuals enabled", null, setAndGetCoreValues(createMavenProjectWithSendToIndividualsAndEmptyRecipients(), null));
		assertEquals("Setting >(disabled)< with sendToIndividuals enabled", "(Disabled)", setAndGetCoreValues(createMavenProjectWithSendToIndividualsAndEmptyRecipients(), "(disabled)"));
		assertEquals("Setting >(DISABLED)< with sendToIndividuals enabled", "(Disabled)", setAndGetCoreValues(createMavenProjectWithSendToIndividualsAndEmptyRecipients(), "(DISABLED)"));
		assertEquals("Setting recipients with sendToIndividuals enabled", "john@doe.com sue@gov.com", setAndGetCoreValues(createMavenProjectWithSendToIndividualsAndEmptyRecipients(), "john@doe.com sue@gov.com"));

		assertEquals("Setting empty recipients with sendToIndividuals enabled", null, setAndGetCoreValues(createFreestyleProjectWithSendToIndividualsAndEmptyRecipients(), null));
		assertEquals("Setting >(disabled)< with sendToIndividuals enabled", "(Disabled)", setAndGetCoreValues(createFreestyleProjectWithSendToIndividualsAndEmptyRecipients(), "(disabled)"));
		assertEquals("Setting >(DISABLED)< with sendToIndividuals enabled", "(Disabled)", setAndGetCoreValues(createFreestyleProjectWithSendToIndividualsAndEmptyRecipients(), "(DISABLED)"));
		assertEquals("Setting recipients with sendToIndividuals enabled", "john@doe.com sue@gov.com", setAndGetCoreValues(createFreestyleProjectWithSendToIndividualsAndEmptyRecipients(), "john@doe.com sue@gov.com"));
		
		assertEquals("Setting empty recipients with ext mailer", null, setAndGetExtValues(createFreestyleProjectWithExtMailer(), null));
		assertEquals("Setting >(disabled)< with ext mailer", "(Disabled)", setAndGetExtValues(createFreestyleProjectWithExtMailer(), "(disabled)"));
	}

	public void testNormalize() {
		doTestNormalize("email@gov.com", "email@gov.com");
		doTestNormalize("email@gov.com, CAPS@gov", "caps@gov email@gov.com");
		doTestNormalize(" ", null);
		doTestNormalize(null, null);
	}
	private void doTestNormalize(String email, String expect) {
		String normalized = new CoreEmailSliceSpec().normalize(email, " ");
		assertEquals(expect, normalized);
	}

	public void testSetValues() throws Exception {		
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
		
		AbstractProject<?,?> project;
		if (maven) {
	        String name = createUniqueProjectName();
	        MavenModuleSet mavenModuleSet = Jenkins.getInstance().createProject(MavenModuleSet.class,name);
	        mavenModuleSet.setRunHeadless( true );

			project = mavenModuleSet;
		} else {
			project = createFreeStyleProject();
		}
		AbstractEmailSliceSpec spec;
		if (core) {
			spec = new CoreEmailSliceSpec();
		} else {
			spec = new ExtEmailSliceSpec();
		}
		
		List<String> values = new ArrayList<String>();
		values.add(valuesString);
		spec.setValues(project, values);
		
		List<String> gotList = spec.getValues(project);
		String got = spec.join(gotList);
		
		assertEquals(expected, got);
	}

	public void testCommonValues() {
		UnorderedStringSlice<AbstractProject<?,?>> slice = new UnorderedStringSlice<AbstractProject<?,?>>(new ExtEmailSlicer.ExtEmailSliceSpec());
		List<String> values = slice.getConfiguredValues();
		assertEquals(3, values.size());
		assertTrue(values.contains(""));
		assertTrue(values.contains(ExtEmailSlicer.ExtEmailSliceSpec.DISABLED));
		assertTrue(values.contains("$DEFAULT_RECIPIENTS"));
		
		slice.add("test", Collections.singleton("value"));
		values = slice.getConfiguredValues();
		assertEquals(4, values.size());
	}

	private String setAndGetCoreValues(AbstractProject<?,?> project, String valuesString) {
		CoreEmailSliceSpec spec = new CoreEmailSliceSpec();
		
		List<String> values = new ArrayList<String>();
		values.add(valuesString);
		spec.setValues(project, values);
		
		List<String> gotList = spec.getValues(project);
		String got = spec.join(gotList);
		return got;
	}

	private String setAndGetExtValues(AbstractProject<?,?> project, String valuesString) {
		ExtEmailSliceSpec spec = new ExtEmailSliceSpec();
		
		List<String> values = new ArrayList<String>();
		values.add(valuesString);
		spec.setValues(project, values);
		
		List<String> gotList = spec.getValues(project);
		String got = spec.join(gotList);
		return got;
	}
	
	private MavenModuleSet createMavenProjectWithSendToIndividualsAndEmptyRecipients()
			throws IOException {
        String name = createUniqueProjectName();
        MavenModuleSet mavenModuleSet = Jenkins.getInstance().createProject(MavenModuleSet.class,name);
        mavenModuleSet.setRunHeadless( true );

		MavenModuleSet mavenProject = mavenModuleSet;
		MavenMailer mailer = new MavenMailer();
		mailer.sendToIndividuals = true;
		DescribableList<MavenReporter,Descriptor<MavenReporter>> reporters = mavenProject.getReporters();
		reporters.add(mailer);		
		return mavenProject;
	}
	
	private AbstractProject<?,?> createFreestyleProjectWithSendToIndividualsAndEmptyRecipients()
			throws IOException {
		FreeStyleProject project = createFreeStyleProject();
		Mailer mailer = new Mailer();
		mailer.sendToIndividuals = true;
		DescribableList<Publisher,Descriptor<Publisher>> publishers = project.getPublishersList();
		publishers.add(mailer);		
		return project;
	}
	
	private AbstractProject<?,?> createFreestyleProjectWithExtMailer() throws IOException {
		FreeStyleProject project = createFreeStyleProject();
		DescribableList<Publisher,Descriptor<Publisher>> publishers = project.getPublishersList();
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
}
