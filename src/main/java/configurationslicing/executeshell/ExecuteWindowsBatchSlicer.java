package configurationslicing.executeshell;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.tasks.BatchFile;
import hudson.tasks.Builder;
import hudson.util.DescribableList;
import java.util.List;

/**
 * Slicer for the windows batch builder
 *
 * @author Jacob Robertson
 */
@Extension
public class ExecuteWindowsBatchSlicer extends AbstractBuildCommandSlicer<BatchFile> {

    public ExecuteWindowsBatchSlicer() {
        super(new ExecuteWindowsBatchSliceSpec());
    }

    public static class ExecuteWindowsBatchSliceSpec extends AbstractBuildCommandSliceSpec<BatchFile> {

        public String getName() {
            return "Execute Windows batch command slicer";
        }

        public String getUrl() {
            return "windowsbatchslice";
        }

        @Override
        public BatchFile createBuilder(String command, List<BatchFile> existingBuilders, BatchFile oldBuilder) {
            return new BatchFile(command);
        }

        @Override
        public BatchFile[] createBuilderArray(int len) {
            return new BatchFile[len];
        }

        @Override
        public String getCommand(BatchFile builder) {
            return builder.getCommand();
        }

        @Override
        public List<BatchFile> getConcreteBuildersList(DescribableList<Builder, Descriptor<Builder>> buildersList) {
            return buildersList.getAll(BatchFile.class);
        }
    }
}
