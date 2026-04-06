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
public class ExecuteShellUnstableReturnSlicer extends AbstractBuildUnstableReturnSlicer<Shell> {

    public ExecuteShellUnstableReturnSlicer() {
        super(new ExecuteShellUnstableReturnSliceSpec());
    }

    public static class ExecuteShellUnstableReturnSliceSpec extends AbstractBuildUnstableReturnSliceSpec<Shell> {

        public String getName() {
            return "Execute shell unstable code slicer (beta)";
        }

        public String getUrl() {
            return "executeshellunstableueturnslice";
        }

        @Override
        public Shell createBuilder(String value, List<Shell> existingBuilders, Shell oldBuilder) {
            String command = "# new shell";
            if (oldBuilder != null) {
                command = oldBuilder.getCommand();
            }
            Shell newBuilder = new Shell(command);
            try {
                int intValue = Integer.parseInt(value);
                if (intValue > 0) {
                    newBuilder.setUnstableReturn(intValue);
                }
            } catch (NumberFormatException e) {
            }
            if (oldBuilder != null) {
                newBuilder.setConfiguredLocalRules(oldBuilder.getConfiguredLocalRules());
            }

            return newBuilder;
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
        public String getUnstableReturn(Shell builder) {
            if (builder != null) {
                Integer retval = builder.getUnstableReturn();
                if (retval != null) {
                    return retval.toString();
                } else {
                    return NOTHING;
                }
            } else {
                return NOTHING;
            }
        }

        @Override
        public void setUnstableReturn(Shell builder, int value) {
            builder.setUnstableReturn(value);
        }

        @Override
        public List<Shell> getConcreteBuildersList(DescribableList<Builder, Descriptor<Builder>> buildersList) {
            return buildersList.getAll(Shell.class);
        }
    }
}
