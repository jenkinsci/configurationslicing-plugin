package configurationslicing;


import hudson.model.AbstractProject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jvnet.hudson.test.HudsonTestCase;

import configurationslicing.email.AbstractEmailSliceSpec;
import configurationslicing.email.CoreEmailSlicer.CoreEmailSliceSpec;
import configurationslicing.email.ExtEmailSlicer.ExtEmailSliceSpec;

public class EmailSlicerTest extends HudsonTestCase {

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
		doTestSetValues("(Disabled)", "");
		doTestSetValues("(Disabled)", " \b\t ");
		doTestSetValues("(Disabled)", "(Disabled)");
		doTestSetValues("caps@gov email@gov.com", "email@gov.com, CAPS@gov");
	}

	private void doTestSetValues(String expected, String valuesString) throws Exception {
		doTestSetValues(expected, valuesString, false, true);
		doTestSetValues(expected, valuesString, true, true);
		doTestSetValues(expected, valuesString, false, false);
		doTestSetValues(expected, valuesString, true, false);
	}
	@SuppressWarnings("unchecked")
	private void doTestSetValues(String expected, String valuesString, boolean maven, boolean core) throws Exception {
		
		if (!core) {
			expected = expected.replaceAll(" ", ",");
		}
		
		AbstractProject project;
		if (maven) {
			project = createMavenProject();
		} else {
			project = createFreeStyleProject();
		}
		AbstractEmailSliceSpec spec;
		if (core) {
			spec = new CoreEmailSliceSpec();
		} else {
			spec = new ExtEmailSliceSpec();
		}
		
		Set<String> values = new HashSet<String>();
		values.add(valuesString);
		spec.setValues(project, values);
		
		List<String> gotList = spec.getValues(project);
		String got = spec.join(gotList);
		
		assertEquals(expected, got);
	}
	
}
