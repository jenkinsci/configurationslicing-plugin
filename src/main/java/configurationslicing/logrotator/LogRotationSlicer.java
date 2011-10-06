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

    protected abstract static class AbstractLogRotationSliceSpec extends UnorderedStringSlicerSpec<AbstractProject<?,?>> {
        private static final String DISABLED = "(Disabled)";

        public String getDefaultValueString() {
        	return DISABLED;
        }
        public String getName(AbstractProject<?, ?> item) {
            return item.getName();
        }

        public List<String> getValues(AbstractProject<?, ?> item) {
            String retString = null;
            LogRotator logrotator = item.getLogRotator();
            if (logrotator == null) {
                retString = DISABLED;
            } else {
            	retString = getValue(logrotator);
                if (retString.length() == 0) {
                    retString = DISABLED;
                }
            }
            List<String> ret = new ArrayList<String>();
            ret.add(retString);
            return ret;
        }
        abstract protected String getValue(LogRotator rotator);

        @SuppressWarnings("unchecked")
		public List<AbstractProject<?, ?>> getWorkDomain() {
            return (List) Hudson.getInstance().getItems(AbstractProject.class);
        }

        public boolean setValues(AbstractProject<?, ?> item, Set<String> set) {
            if (set.isEmpty()) return false;

            LogRotator logrotator = item.getLogRotator();
            int days = -1;
            int builds = -1;
    	    int artifactDaysToKeep = -1;
    	    int artifactNumToKeep = -1;
            
            if (logrotator != null) {
                days = logrotator.getDaysToKeep();
                builds = logrotator.getNumToKeep();
                artifactDaysToKeep = logrotator.getArtifactDaysToKeep();
                artifactNumToKeep = logrotator.getArtifactNumToKeep();
            }
            
            int newInt = -1;
            String newString = null;
            for(String line: set) {
            	newString = line;
            	break;
            }
            if (!DISABLED.equals(newString)) {
                newInt = Integer.parseInt(newString);
            }
            days = getNewDays(days, newInt);
            builds = getNewBuilds(builds, newInt);
    	    
            LogRotator newlogrotator = new LogRotator(days, builds, artifactDaysToKeep, artifactNumToKeep);
            
            if (!LogRotationSlicer.equals(newlogrotator, logrotator)) {
	            item.setLogRotator(newlogrotator);
	            try {
	                item.save();
	            } catch (IOException e) {
	                e.printStackTrace();
	                return false;
	            }
	            return true;
            } else {
            	return false;
            }
        }
        protected int getNewDays(int oldDays, int newValue) {
       		return oldDays;
        }
        protected int getNewBuilds(int oldBuilds, int newValue) {
       		return oldBuilds;
        }
    }
    
    public static class LogRotationDaysSliceSpec extends AbstractLogRotationSliceSpec {
        public String getName() {
            return "Discard Old Builds Slicer - Days";
        }
        public String getUrl() {
            return "logrotationdays";
        }
        @Override
        protected String getValue(LogRotator rotator) {
        	return rotator.getDaysToKeepStr();
        }
        @Override
        protected int getNewDays(int oldDays, int newValue) {
        	return newValue;
        }
    }
    public static class LogRotationBuildsSliceSpec extends AbstractLogRotationSliceSpec {
        public String getName() {
            return "Discard Old Builds Slicer - Builds";
        }
        public String getUrl() {
            return "logrotationbuilds";
        }
        @Override
        protected String getValue(LogRotator rotator) {
        	return rotator.getNumToKeepStr();
        }
        @Override
        protected int getNewBuilds(int oldBuilds, int newValue) {
        	return newValue;
        }
    }
    public static boolean equals(LogRotator r1, LogRotator r2) {
    	if (r1 == r2) {
    		return true;
    	}
    	if (r1 == null || r2 == null) {
    		return false;
    	}
    	if (r1.getDaysToKeep() != r2.getDaysToKeep()) {
    		return false;
    	}
    	if (r1.getNumToKeep() != r2.getNumToKeep()) {
    		return false;
    	}
    	return true;
    }
}
