package configurationslicing.blockbuild;

import configurationslicing.BooleanSlicer;
import configurationslicing.TopLevelItemSelector;
import hudson.Extension;
import hudson.model.AbstractProject;
import java.io.IOException;
import java.util.List;

@Extension
public class BlockBuildWhenUpstreamBuildingBoolSlicer extends BooleanSlicer<AbstractProject> {
    public BlockBuildWhenUpstreamBuildingBoolSlicer() {
        super(new BlockBuildWhenUpstreamBuildingSpec());
    }

    public static class BlockBuildWhenUpstreamBuildingSpec implements BooleanSlicer.BooleanSlicerSpec<AbstractProject> {
        public String getName() {
            return "Block Build when Upstream Building Slicer (bool)";
        }

        public String getName(AbstractProject item) {
            return item.getFullName();
        }

        public String getUrl() {
            return "blockBuildWhenUpstreamBuilding";
        }

        public boolean getValue(AbstractProject item) {
            return item.blockBuildWhenUpstreamBuilding();
        }

        public List<AbstractProject> getWorkDomain() {
            return TopLevelItemSelector.getAllTopLevelItems(AbstractProject.class);
        }

        public boolean setValue(AbstractProject item, boolean value) {
            boolean oldval = item.blockBuildWhenUpstreamBuilding();
            try {
                item.setBlockBuildWhenUpstreamBuilding(value);
            } catch (IOException e) {
                return false;
            }
            return oldval != value;
        }
    }
}
