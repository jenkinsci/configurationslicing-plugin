package configurationslicing.executeshell;

import hudson.model.Descriptor;
import hudson.tasks.Builder;
import hudson.util.DescribableList;

import java.util.List;

import net.sf.json.JSONObject;

import org.jvnet.hudson.plugins.Jython;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Slicer for the jython builder
 *
 * @author Jacob Robertson
 */
public class ExecuteJythonSlicer extends AbstractBuildCommandSlicer<Jython> {

    public ExecuteJythonSlicer() {
        super(new ExecuteJythonSliceSpec());
    }
	@Override
	public void loadPluginDependencyClass() {
		// this is just to demonstrate that the Jython plugin is loaded
		Jython.class.getClass();
	}
    private static final Jython.DescriptorImpl JYTHON_DESCRIPTOR = new Jython.DescriptorImpl();
    
    public static class ExecuteJythonSliceSpec extends AbstractBuildCommandSliceSpec<Jython> {

        public String getName() {
            return "Execute Jython script";
        }

        public String getUrl() {
            return "executejythonslice";
        }
		@Override
        public Jython createBuilder(String command, List<Jython> existingBuilders, Jython oldBuilder) {
        	// this is an unfortunate workaround that is necessary due to the Jython constructor being private
        	StaplerRequest req = null;
        	JSONObject formData = new JSONObject();
        	formData.put("jython", command);
			return (Jython) JYTHON_DESCRIPTOR.newInstance(req, formData);
        }
        @Override
        public Jython[] createBuilderArray(int len) {
        	return new Jython[len];
        }
        @Override
        public String getCommand(Jython builder) {
        	return builder.getCommand();
        }
        @Override
        public List<Jython> getConcreteBuildersList(
        		DescribableList<Builder, Descriptor<Builder>> buildersList) {
            return buildersList.getAll(Jython.class);
        }

    }
}

