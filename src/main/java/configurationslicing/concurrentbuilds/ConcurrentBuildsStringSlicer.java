package configurationslicing.concurrentbuilds;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import configurationslicing.TopLevelItemSelector;
import configurationslicing.UnorderedStringSlicer;
import hudson.Extension;
import hudson.model.Job;

import static configurationslicing.AbstractJob.fix;

@Extension
public class ConcurrentBuildsStringSlicer extends UnorderedStringSlicer<Job> {

    public ConcurrentBuildsStringSlicer() {
        super(new ConcurrentBuildsStringSliceSpec());
    }

    public static class ConcurrentBuildsStringSliceSpec extends UnorderedStringSlicerSpec<Job> {

        public String getDefaultValueString() {
            return null;
        }

        public String getName() {
            return "Job Concurrent Build Slicer (String)";
        }

        public String getName(Job item) {
            return item.getFullName();
        }

        public String getUrl() {
            return "concurrentbuildsstring";
        }

        @Override
        public boolean isBlankNeededForValues() {
            return false;
        }

        @Override
        public List<String> getCommonValueStrings() {
            List<String> values = new ArrayList<String>();
            values.add(String.valueOf(true));
            values.add(String.valueOf(false));
            return values;
        }

        public List<String> getValues(Job job) {
            List<String> values = new ArrayList<String>();
            boolean isConcurrent = fix(job).isConcurrentBuilds();
            values.add(String.valueOf(isConcurrent));
            return values;
        }

        public List<Job> getWorkDomain() {
            return TopLevelItemSelector.getAllTopLevelItems(Job.class);
        }

        public boolean setValues(Job job, List<String> set) {
            String value = set.iterator().next();

            boolean oldConcurrent = fix(job).isConcurrentBuilds();
            boolean newConcurrent = Boolean.parseBoolean(value);

            if (oldConcurrent != newConcurrent) {
                try {
                    fix(job).makeConcurrentBuilds(newConcurrent);
                } catch (IOException e) {
                    return false;
                }
                return true;
            } else {
                return false;
            }
        }
    }
}
