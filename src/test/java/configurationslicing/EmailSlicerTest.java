package configurationslicing;

import hudson.model.AbstractProject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jvnet.hudson.test.HudsonTestCase;

import configurationslicing.email.CoreEmailSlicer;

public class EmailSlicerTest extends HudsonTestCase {

	public void testNormalize() {
		doTestNormalize("email@gov.com", "email@gov.com");
		doTestNormalize("email@gov.com, CAPS@gov", "caps@gov email@gov.com");
		doTestNormalize(" ", null);
		doTestNormalize(null, null);
	}
	private void doTestNormalize(String email, String expect) {
		String normalized = new CoreEmailSlicer.CoreEmailSliceSpec().normalize(email, " ");
		assertEquals(expect, normalized);
	}

	public void testSetValues() throws Exception {
		doTestSetValues("(Disabled)", "");
		doTestSetValues("(Disabled)", " \b\t ");
		doTestSetValues("(Disabled)", "(Disabled)");
		doTestSetValues("caps@gov email@gov.com", "email@gov.com, CAPS@gov");
	}

	private void doTestSetValues(String expected, String valuesString) throws Exception {
		doTestSetValues(expected, valuesString, false);
		doTestSetValues(expected, valuesString, true);
	}
	@SuppressWarnings("unchecked")
	private void doTestSetValues(String expected, String valuesString, boolean maven) throws Exception {
		
		AbstractProject project;
		if (maven) {
			project = createMavenProject();
		} else {
			project = createFreeStyleProject();
		}
		CoreEmailSlicer.CoreEmailSliceSpec spec = new CoreEmailSlicer.CoreEmailSliceSpec();
		
		Set<String> values = new HashSet<String>();
		values.add(valuesString);
		spec.setValues(project, values);
		
		List<String> gotList = spec.getValues(project);
		String got = spec.join(gotList);
		
		assertEquals(expected, got);
	}
	
}
