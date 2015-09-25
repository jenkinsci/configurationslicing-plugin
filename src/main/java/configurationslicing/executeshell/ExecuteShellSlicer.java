package configurationslicing.executeshell;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.tasks.Builder;
import hudson.tasks.Shell;
import hudson.util.DescribableList;

import java.util.List;

/**
 * Slicer for the shell builder
 */
@Extension
public class ExecuteShellSlicer extends AbstractBuildCommandSlicer<Shell> {

    public ExecuteShellSlicer() {
        super(new ExecuteShellSliceSpec());
    }

    public static class ExecuteShellSliceSpec extends AbstractBuildCommandSliceSpec<Shell> {

        public String getName() {
            return "Execute shell slicer";
        }

        public String getUrl() {
            return "executeshellslicestring";
        }
        @Override
        public Shell createBuilder(String command, List<Shell> existingBuilders, Shell oldBuilder) {
        	return new Shell(command);
        }
        @Override
        public Shell[] createBuilderArray(int len) {
        	return new Shell[len];
        }
        @Override
        public String getCommand(Shell builder) {
        	return builder.getCommand();
        }
        @Override
        public List<Shell> getConcreteBuildersList(
        		DescribableList<Builder, Descriptor<Builder>> buildersList) {
            return buildersList.getAll(Shell.class);
        }

    }
}

