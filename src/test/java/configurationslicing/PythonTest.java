package configurationslicing;

import configurationslicing.executeshell.ExecutePythonSlicer;
import hudson.plugins.python.Python;
import junit.framework.TestCase;

public class PythonTest extends TestCase {

    public void testCreatePython() throws Exception {
        // this is failing at runtime for a method not found problem with "newInstance"
        Python p = new ExecutePythonSlicer.ExecutePythonSliceSpec().createBuilder("hello", null, null);
        assertNotNull(p);
    }
}
