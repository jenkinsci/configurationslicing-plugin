package configurationslicing.jobdisabled;

import java.io.IOException;
import java.util.List;

import configurationslicing.BooleanSlicer;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Hudson;

@Extension
public class JobDisabledBoolSlicer extends BooleanSlicer<AbstractProject<?,?>> {
    public JobDisabledBoolSlicer() {
        super(new JobDisabledSpec());
    }
    public static class JobDisabledSpec implements BooleanSlicer.BooleanSlicerSpec<AbstractProject<?,?>>
    {
        public String getName() {
            return "Job Disabled Build Slicer (bool)";
        }

        public String getName(AbstractProject<?,?> item) {
            return item.getName();
        }

        public String getUrl() {
            return "jobdisabledbool";
        }

        public boolean getValue(AbstractProject<?,?> item) {
            return item.isDisabled();
        }

        public List<AbstractProject<?,?>> getWorkDomain() {
            return (List)Hudson.getInstance().getItems(AbstractProject.class);
        }

        public boolean setValue(AbstractProject<?,?> item, boolean value) {
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
