package configurationslicing.prioritysorter;

import configurationslicing.TopLevelItemSelector;
import configurationslicing.UnorderedStringSlicer;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.queueSorter.PrioritySorterJobProperty;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jacob_robertson
 */
public class PrioritySorterSlicer extends UnorderedStringSlicer<Job<?, ?>> {

    public PrioritySorterSlicer() {
        super(new PrioritySorterSliceSpec());
    }

    @Override
    public void loadPluginDependencyClass() {
        // this is just to demonstrate that the PrioritySorter plugin is loaded
        PrioritySorterJobProperty.class.getClass();
    }

    public static class PrioritySorterSliceSpec extends UnorderedStringSlicerSpec<Job<?, ?>> {

        // cannot get this default from the class itself, as it is a package-level field
        private static final int DEFAULT_PRIORITY_INT = 100;
        private static final String DEFAULT_PRIORITY = String.valueOf(DEFAULT_PRIORITY_INT);

        public String getDefaultValueString() {
            return DEFAULT_PRIORITY;
        }

        public String getName() {
            return "Job Priority Slicer";
        }

        public String getName(Job<?, ?> item) {
            return item.getFullName();
        }

        public String getUrl() {
            return "jobpriority";
        }

        public List<String> getValues(Job<?, ?> job) {
            List<String> values = new ArrayList<String>();
            PrioritySorterJobProperty prop = job.getProperty(PrioritySorterJobProperty.class);
            if (prop != null) {
                values.add(String.valueOf(prop.priority));
            } else {
                values.add(DEFAULT_PRIORITY);
            }
            return values;
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public List<Job<?, ?>> getWorkDomain() {
            return (List) TopLevelItemSelector.getAllTopLevelItems(Job.class);
        }

        @SuppressWarnings("unchecked")
        public boolean setValues(Job<?, ?> job, List<String> set) {
            String value = set.iterator().next();

            int priority;
            try {
                priority = Integer.parseInt(value);
            } catch (NumberFormatException nfe) {
                priority = DEFAULT_PRIORITY_INT;
            }
            boolean changed;
            PrioritySorterJobProperty prop = job.getProperty(PrioritySorterJobProperty.class);
            if (prop == null) {
                changed = (priority != DEFAULT_PRIORITY_INT);
                if (changed) {
                    try {
                        prop = new PrioritySorterJobProperty(priority);
                        job.addProperty((JobProperty) prop);
                    } catch (IOException e) {
                        return false;
                    }
                }
            } else {
                int oldPriority = prop.priority;
                changed = (oldPriority != priority);
                if (changed) {
                    try {
                        job.removeProperty((JobProperty) prop);
                        prop = new PrioritySorterJobProperty(priority);
                        job.addProperty((JobProperty) prop);
                    } catch (IOException e) {
                        return false;
                    }
                }
            }
            return changed;
        }
    }
}
