package configurationslicing.jobdisabled;

import configurationslicing.BooleanSlicer;
import configurationslicing.TopLevelItemSelector;
import hudson.Extension;
import hudson.model.AbstractProject;
import java.io.IOException;
import java.util.List;

@Extension
public class JobDisabledBoolSlicer extends BooleanSlicer<AbstractProject> {
    public JobDisabledBoolSlicer() {
        super(new JobDisabledSpec());
    }

    public static class JobDisabledSpec implements BooleanSlicer.BooleanSlicerSpec<AbstractProject> {
        public String getName() {
            return "Job Disabled Build Slicer (bool)";
        }

        public String getName(AbstractProject item) {
            return item.getFullName();
        }

        public String getUrl() {
            return "jobdisabledbool";
        }

        public boolean getValue(AbstractProject item) {
            return item.isDisabled();
        }

        public List<AbstractProject> getWorkDomain() {
            return TopLevelItemSelector.getAllTopLevelItems(AbstractProject.class);
        }

        public boolean setValue(AbstractProject item, boolean value) {
            boolean oldval = item.isDisabled();
            try {
                item.makeDisabled(value);
            } catch (IOException e) {
                return false;
            }
            return oldval != value;
        }
    }
}
