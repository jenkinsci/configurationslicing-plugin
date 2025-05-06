package configurationslicing;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import configurationslicing.executeshell.ExecutePythonSlicer;
import hudson.plugins.python.Python;
import org.junit.jupiter.api.Test;

class PythonTest {

    @Test
    void testCreatePython() {
        // this is failing at runtime for a method not found problem with "newInstance"
        Python p = new ExecutePythonSlicer.ExecutePythonSliceSpec().createBuilder("hello", null, null);
        assertNotNull(p);
    }
}
