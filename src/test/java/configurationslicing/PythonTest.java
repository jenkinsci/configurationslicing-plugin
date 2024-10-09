package configurationslicing;

import static org.junit.Assert.assertNotNull;

import configurationslicing.executeshell.ExecutePythonSlicer;
import hudson.plugins.python.Python;
import org.junit.Test;

public class PythonTest {

    @Test
    public void testCreatePython() throws Exception {
        // this is failing at runtime for a method not found problem with "newInstance"
        Python p = new ExecutePythonSlicer.ExecutePythonSliceSpec().createBuilder("hello", null, null);
        assertNotNull(p);
    }
}
