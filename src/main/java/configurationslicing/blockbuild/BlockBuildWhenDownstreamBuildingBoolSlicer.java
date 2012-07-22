package configurationslicing.blockbuild;

import java.io.IOException;
import java.util.List;

import configurationslicing.BooleanSlicer;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Hudson;

@Extension
public class BlockBuildWhenDownstreamBuildingBoolSlicer extends BooleanSlicer<AbstractProject<?,?>> {
    public BlockBuildWhenDownstreamBuildingBoolSlicer() {
        super(new BlockBuildWhenDownstreamBuildingSpec());
    }
    
    public static class BlockBuildWhenDownstreamBuildingSpec implements BooleanSlicer.BooleanSlicerSpec<AbstractProject<?,?>>
    {
        public String getName() {
            return "Block Build when Downstream Building Slicer (bool)";
        }

        public String getName(AbstractProject<?,?> item) {
            return item.getName();
        }

        public String getUrl() {
            return "blockBuildWhenDownstreamBuilding";
        }

        public boolean getValue(AbstractProject<?,?> item) {
            return item.blockBuildWhenDownstreamBuilding();
        }

        public List<AbstractProject<?,?>> getWorkDomain() {
            return (List)Hudson.getInstance().getItems(AbstractProject.class);
        }

        public boolean setValue(AbstractProject<?,?> item, boolean value) {
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
