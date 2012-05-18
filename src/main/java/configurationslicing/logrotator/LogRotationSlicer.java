package configurationslicing.logrotator;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Hudson;
import hudson.tasks.LogRotator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    @Extension
    public static class ArtifactDays extends LogRotationSlicer {
        public ArtifactDays() {
            super(new ArtifactDaysSliceSpec());
        }
    }
    @Extension
    public static class ArtifactBuilds extends LogRotationSlicer {
        public ArtifactBuilds() {
            super(new ArtifactBuildsSliceSpec());
        }
    }

    protected abstract static class AbstractLogRotationSliceSpec extends UnorderedStringSlicerSpec<AbstractProject<?,?>> {
        private static final String DISABLED = "(Disabled)";
        private String displayName;
        private String url;
         
        public AbstractLogRotationSliceSpec(String displayName, String url) {
			this.displayName = "Discard Old Builds Slicer - " + displayName;
			this.url = url;
		}
        @Override
        public String getName() {
        	return displayName;
        }
        @Override
        public String getUrl() {
        	return url;
        }
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

        public boolean setValues(AbstractProject<?, ?> item, List<String> set) {
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
            artifactDaysToKeep = getNewArtifactDays(artifactDaysToKeep, newInt);
            artifactNumToKeep = getNewArtifactBuilds(artifactNumToKeep, newInt);
    	    
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
        protected int getNewArtifactDays(int oldValue, int newValue) {
       		return oldValue;
        }
        protected int getNewArtifactBuilds(int oldValue, int newValue) {
       		return oldValue;
        }
    }
    
    public static class LogRotationDaysSliceSpec extends AbstractLogRotationSliceSpec {
        public LogRotationDaysSliceSpec() {
            super("Days to keep builds", "logrotationdays");
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
        public LogRotationBuildsSliceSpec() {
        	super("Max # of builds to keep", "logrotationbuilds");
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
    public static class ArtifactDaysSliceSpec extends AbstractLogRotationSliceSpec {
        public ArtifactDaysSliceSpec() {
            super("Days to keep artifacts", "artifactsdays");
        }
        @Override
        protected String getValue(LogRotator rotator) {
        	return rotator.getArtifactDaysToKeepStr();
        }
        @Override
        protected int getNewArtifactDays(int oldDays, int newValue) {
        	return newValue;
        }
    }
    public static class ArtifactBuildsSliceSpec extends AbstractLogRotationSliceSpec {
        public ArtifactBuildsSliceSpec() {
            super("Max # of builds to keep with artifacts", "artifactsbuilds");
        }
        @Override
        protected String getValue(LogRotator rotator) {
        	return rotator.getArtifactNumToKeepStr();
        }
        @Override
        protected int getNewArtifactBuilds(int oldValue, int newValue) {
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
    	if (r1.getArtifactDaysToKeep() != r2.getArtifactDaysToKeep()) {
    		return false;
    	}
    	if (r1.getArtifactNumToKeep() != r2.getArtifactNumToKeep()) {
    		return false;
    	}
    	return true;
    }
}
