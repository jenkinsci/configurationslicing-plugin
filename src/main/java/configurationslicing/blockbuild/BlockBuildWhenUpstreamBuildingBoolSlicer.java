package configurationslicing.blockbuild;

import java.io.IOException;
import java.util.List;

import configurationslicing.BooleanSlicer;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Hudson;

@Extension
public class BlockBuildWhenUpstreamBuildingBoolSlicer extends BooleanSlicer<AbstractProject<?,?>> {
    public BlockBuildWhenUpstreamBuildingBoolSlicer() {
        super(new BlockBuildWhenUpstreamBuildingSpec());
    }
    
    public static class BlockBuildWhenUpstreamBuildingSpec implements BooleanSlicer.BooleanSlicerSpec<AbstractProject<?,?>>
    {
        public String getName() {
            return "Block Build when Upstream Building Slicer (bool)";
        }

        public String getName(AbstractProject<?,?> item) {
            return item.getFullName();
        }

        public String getUrl() {
            return "blockBuildWhenUpstreamBuilding";
        }

        public boolean getValue(AbstractProject<?,?> item) {
            return item.blockBuildWhenUpstreamBuilding();
        }

        public List<AbstractProject<?,?>> getWorkDomain() {
            return (List)Hudson.getInstance().getAllItems(AbstractProject.class);
        }

        public boolean setValue(AbstractProject<?,?> item, boolean value) {
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
