package configurationslicing.timestamper;

import hudson.Extension;
import hudson.model.BuildableItemWithBuildWrappers;
import hudson.model.AbstractProject;
import hudson.plugins.timestamper.TimestamperBuildWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import configurationslicing.TopLevelItemSelector;
import configurationslicing.UnorderedStringSlicer;

/**
 * @author jacob_robertson
 */
@Extension
public class TimestamperSlicer extends UnorderedStringSlicer<AbstractProject<?,?>>{

    public TimestamperSlicer() {
        super(new TimestamperSliceSpec());
    }

    @Override
    public void loadPluginDependencyClass() {
        // this is just to demonstrate that the Timestamper plugin is loaded
        TimestamperBuildWrapper.class.getClass();
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
            return item.getFullName();
        }

        public String getUrl() {
            return "timestamper";
        }
        @Override
        public boolean isBlankNeededForValues() {
        	return false;
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

		public List<AbstractProject<?, ?>> getWorkDomain() {
            return TopLevelItemSelector.getAllTopLevelItems(BuildableItemWithBuildWrappers.class);
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
