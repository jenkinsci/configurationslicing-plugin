package configurationslicing.blockbuild;

import configurationslicing.BooleanSlicer;
import configurationslicing.TopLevelItemSelector;
import hudson.Extension;
import hudson.model.AbstractProject;
import java.io.IOException;
import java.util.List;

@Extension
public class BlockBuildWhenDownstreamBuildingBoolSlicer extends BooleanSlicer<AbstractProject> {
    public BlockBuildWhenDownstreamBuildingBoolSlicer() {
        super(new BlockBuildWhenDownstreamBuildingSpec());
    }

    public static class BlockBuildWhenDownstreamBuildingSpec
            implements BooleanSlicer.BooleanSlicerSpec<AbstractProject> {
        public String getName() {
            return "Block Build when Downstream Building Slicer (bool)";
        }

        public String getName(AbstractProject item) {
            return item.getFullName();
        }

        public String getUrl() {
            return "blockBuildWhenDownstreamBuilding";
        }

        public boolean getValue(AbstractProject item) {
            return item.blockBuildWhenDownstreamBuilding();
        }

        public List<AbstractProject> getWorkDomain() {
            return TopLevelItemSelector.getAllTopLevelItems(AbstractProject.class);
        }

        public boolean setValue(AbstractProject item, boolean value) {
            boolean oldval = item.blockBuildWhenDownstreamBuilding();
            try {
                item.setBlockBuildWhenDownstreamBuilding(value);
            } catch (IOException e) {
                return false;
            }
            return oldval != value;
        }
    }
}
