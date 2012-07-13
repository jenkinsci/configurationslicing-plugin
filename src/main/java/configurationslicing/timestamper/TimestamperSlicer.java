package configurationslicing.timestamper;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.BuildableItemWithBuildWrappers;
import hudson.model.Hudson;
import hudson.plugins.timestamper.TimestamperBuildWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import configurationslicing.UnorderedStringSlicer;

/**
 * @author jacob_robertson
 */
@Extension
public class TimestamperSlicer extends UnorderedStringSlicer<AbstractProject<?,?>>{

    public TimestamperSlicer() {
        super(new TimestamperSliceSpec());
    }

    public static class TimestamperSliceSpec extends UnorderedStringSlicerSpec<AbstractProject<?,?>> {

        private static final String DISABLED = Boolean.FALSE.toString();

        public List<String> getCommonValueStrings() {
        	List<String> booleans = new ArrayList<String>();
        	booleans.add(Boolean.TRUE.toString());
        	return booleans;
        }
        public String getDefaultValueString() {
        	return DISABLED;
        }
        public String getName() {
            return "Timestamper Slicer";
        }

        public String getName(AbstractProject<?, ?> item) {
            return item.getName();
        }

        public String getUrl() {
            return "timestamper";
        }

		public List<String> getValues(AbstractProject<?, ?> item) {
        	BuildableItemWithBuildWrappers project = (BuildableItemWithBuildWrappers) item;
        	
        	String value;
        	TimestamperBuildWrapper wrapper = (TimestamperBuildWrapper) project.getBuildWrappersList().get(TimestamperBuildWrapper.class);
        	if (wrapper != null) {
        		value = Boolean.TRUE.toString();
        	} else {
        		value = DISABLED;
        	}

        	List<String> booleans = new ArrayList<String>();
        	booleans.add(value);
            return booleans;
        }

        @SuppressWarnings({ "unchecked" })
		public List<AbstractProject<?, ?>> getWorkDomain() {
            return (List) Hudson.getInstance().getItems(BuildableItemWithBuildWrappers.class);
        }

		public boolean setValues(AbstractProject<?, ?> item, List<String> set) {
        	BuildableItemWithBuildWrappers project = (BuildableItemWithBuildWrappers) item;
        	String value = set.iterator().next();
        	
        	boolean isTimestampWanted = Boolean.parseBoolean(value);

        	boolean changed;
        	
        	TimestamperBuildWrapper wrapper = (TimestamperBuildWrapper) project.getBuildWrappersList().get(TimestamperBuildWrapper.class);
        	boolean isTimestampPresent = (wrapper != null);

        	if (isTimestampPresent && !isTimestampWanted) {
        		changed = true;
        		try {
					project.getBuildWrappersList().remove(TimestamperBuildWrapper.class);
				} catch (IOException e) {
					return false;
				}
        	} else if (!isTimestampPresent && isTimestampWanted) {
        		changed = true;
				try {
					project.getBuildWrappersList().add(new TimestamperBuildWrapper());
				} catch (IOException e) {
					return false;
				}
        	} else {
        		changed = false;
        	}
        	
        	return changed;
        }
    }
}
