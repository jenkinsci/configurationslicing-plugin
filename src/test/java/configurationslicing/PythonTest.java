package configurationslicing;

import hudson.plugins.python.Python;
import junit.framework.TestCase;
import configurationslicing.executeshell.ExecutePythonSlicer;

public class PythonTest extends TestCase {

	public void testCreatePython() throws Exception {
		// this is failing at runtime for a method not found problem with "newInstance"
		Python p = new ExecutePythonSlicer.ExecutePythonSliceSpec().createBuilder("hello", null, null);
		assertNotNull(p);
	}
}
