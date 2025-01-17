package configurationslicing.executeshell;

import hudson.model.Descriptor;
import hudson.model.Descriptor.FormException;
import hudson.plugins.python.Python;
import hudson.tasks.Builder;
import hudson.util.DescribableList;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest2;

/**
 * Slicer for the python builder
 *
 * @author Jacob Robertson
 */
public class ExecutePythonSlicer extends AbstractBuildCommandSlicer<Python> {

    public ExecutePythonSlicer() {
        super(new ExecutePythonSliceSpec());
    }

    @Override
    public void loadPluginDependencyClass() {
        // this is just to demonstrate that the Python plugin is loaded
        Python.class.getClass();
    }

    private static final Python.DescriptorImpl PYTHON_DESCRIPTOR = new Python.DescriptorImpl();

    public static class ExecutePythonSliceSpec extends AbstractBuildCommandSliceSpec<Python> {

        public String getName() {
            return "Execute Python script";
        }

        public String getUrl() {
            return "executepythonslice";
        }

        @SuppressWarnings({"rawtypes"})
        @Override
        public Python createBuilder(String command, List<Python> existingBuilders, Python oldBuilder) {
            Python python = null;
            Constructor[] cons = Python.class.getConstructors();
            if (cons.length > 0) {
                try {
                    if (!Modifier.isPublic(cons[0].getModifiers())) {
                        cons[0].setAccessible(true);
                    }
                    python = (Python) cons[0].newInstance(command);
                } catch (Exception e) {
                    // we'll try another way to get it
                    python = null;
                }
            }
            if (python == null) {
                // this is an unfortunate workaround that is necessary due to the Python constructor being private in
                // certain versions
                StaplerRequest2 req = null;
                JSONObject formData = new JSONObject();
                formData.put("python", command);
                try {
                    python = (Python) PYTHON_DESCRIPTOR.newInstance(req, formData);
                } catch (FormException e) {
                    python = null;
                }
            }
            return python;
        }

        @Override
        public Python[] createBuilderArray(int len) {
            return new Python[len];
        }

        @Override
        public String getCommand(Python builder) {
            return builder.getCommand();
        }

        @Override
        public List<Python> getConcreteBuildersList(DescribableList<Builder, Descriptor<Builder>> buildersList) {
            return buildersList.getAll(Python.class);
        }
    }
}
