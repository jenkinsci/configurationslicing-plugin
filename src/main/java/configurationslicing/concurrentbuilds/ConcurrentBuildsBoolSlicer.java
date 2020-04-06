package configurationslicing.concurrentbuilds;

import java.io.IOException;
import java.util.List;

import configurationslicing.BooleanSlicer;
import configurationslicing.TopLevelItemSelector;
import hudson.Extension;
import hudson.model.Job;

import static configurationslicing.AbstractJob.fix;

@Extension
public class ConcurrentBuildsBoolSlicer extends BooleanSlicer<Job> {
    public ConcurrentBuildsBoolSlicer() {
        super(new ConcurrentBuildsBoolSpec());
    }

    public static class ConcurrentBuildsBoolSpec implements BooleanSlicerSpec<Job> {
        public String getName() {
            return "Job Concurrent Builds Slicer (Bool)";
        }

        public String getName(Job item) {
            return item.getFullName();
        }

        public String getUrl() {
            return "concurrentbuildsbool";
        }

        public boolean getValue(Job item) {
            return fix(item).isConcurrentBuilds();
        }

        public List<Job> getWorkDomain() {
            return TopLevelItemSelector.getAllTopLevelItems(Job.class);
        }

        public boolean setValue(Job item, boolean value) {
            boolean oldval = fix(item).isConcurrentBuilds();
            if (oldval != value) {
                try {
                    fix(item).makeConcurrentBuilds(value);
                } catch (IOException e) {
                    return false;
                }
                return true;
            }
            return false;
        }
    }
}
