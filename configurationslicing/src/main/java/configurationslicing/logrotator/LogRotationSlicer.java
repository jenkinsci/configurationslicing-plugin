package configurationslicing.logrotator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Hudson;
import hudson.tasks.LogRotator;
import configurationslicing.UnorderedStringSlicer;

public abstract class LogRotationSlicer extends UnorderedStringSlicer<AbstractProject<?,?>> {
    public LogRotationSlicer(UnorderedStringSlicerSpec<AbstractProject<?,?>> spec) {
        super(spec);
    }
    
    @Extension
    public static class Days extends LogRotationSlicer {
        public Days() {
            super(new LogRotationDaysSliceSpec());
        }
    }
    @Extension
    public static class Count extends LogRotationSlicer {
        public Count() {
            super(new LogRotationBuildsSliceSpec());
        }
    }

    public static class LogRotationDaysSliceSpec implements UnorderedStringSlicerSpec<AbstractProject<?,?>> {

        private static final String DISABLED = "(Disabled)";

        public String getName() {
            return "Discard Old Builds Slicer - Days";
        }

        public String getName(AbstractProject<?, ?> item) {
            return item.getName();
        }

        public String getUrl() {
            return "logrotationdays";
        }

        public List<String> getValues(AbstractProject<?, ?> item) {
            List<String> ret = new ArrayList<String>();
            LogRotator logrotator = item.getLogRotator();
            if(logrotator == null) {
                ret.add(DISABLED);
            } else {
                ret.add(logrotator.getDaysToKeepStr());
            }
            return ret;
        }

        public List<AbstractProject<?, ?>> getWorkDomain() {
            return (List)Hudson.getInstance().getItems(AbstractProject.class);
        }

        public boolean setValues(AbstractProject<?, ?> item, Set<String> set) {
            if(set.isEmpty()) return false;

            LogRotator logrotator = item.getLogRotator();
            int days = -1,builds = -1;
            
            if(logrotator!= null) {
                days=logrotator.getDaysToKeep();
                builds=logrotator.getNumToKeep();
            }
            boolean disabled = false;
            for(String line : set) {
                if(line.equals(DISABLED)) {
                    disabled = true;
                } else {
                    int val = Integer.parseInt(line);
                    if(val > days) {
                        days = val;
                    }
                }
            }

            if(disabled) days = -1;
            
            LogRotator newlogrotator = new LogRotator(days,builds);
            item.setLogRotator(newlogrotator);
            try {
                item.save();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }
    
    public static class LogRotationBuildsSliceSpec implements UnorderedStringSlicerSpec<AbstractProject<?,?>> {

        private static final String DISABLED = "(Disabled)";

        public String getName() {
            return "Discard Old Builds Slicer - Builds";
        }

        public String getName(AbstractProject<?, ?> item) {
            return item.getName();
        }

        public String getUrl() {
            return "logrotationbuilds";
        }

        public List<String> getValues(AbstractProject<?, ?> item) {
            List<String> ret = new ArrayList<String>();
            LogRotator logrotator = item.getLogRotator();
            if(logrotator == null) {
                ret.add(DISABLED);
            } else {
                ret.add(logrotator.getNumToKeepStr());
            }
            return ret;
        }

        public List<AbstractProject<?, ?>> getWorkDomain() {
            return (List)Hudson.getInstance().getItems(AbstractProject.class);
        }

        public boolean setValues(AbstractProject<?, ?> item, Set<String> set) {
            if(set.isEmpty()) return false;

            LogRotator logrotator = item.getLogRotator();
            int days = -1,builds = -1;
            
            if(logrotator!= null) {
                days=logrotator.getDaysToKeep();
                builds=logrotator.getNumToKeep();
            }
            boolean disabled = false;
            for(String line : set) {
                if(line.equals(DISABLED)) {
                    disabled = true;
                } else {
                    int val = Integer.parseInt(line);
                    if(val > builds) {
                        builds = val;
                    }
                }
            }

            if(disabled) builds = -1;
            
            LogRotator newlogrotator = new LogRotator(days,builds);
            item.setLogRotator(newlogrotator);
            try {
                item.save();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }
}
