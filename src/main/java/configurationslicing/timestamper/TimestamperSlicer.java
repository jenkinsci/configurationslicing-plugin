package configurationslicing.timestamper;

import configurationslicing.TopLevelItemSelector;
import configurationslicing.UnorderedStringSlicer;
import hudson.Extension;
import hudson.model.BuildableItemWithBuildWrappers;
import hudson.plugins.timestamper.TimestamperBuildWrapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jacob_robertson
 */
@Extension(optional = true)
public class TimestamperSlicer extends UnorderedStringSlicer<BuildableItemWithBuildWrappers> {

    public TimestamperSlicer() {
        super(new TimestamperSliceSpec());
    }

    @Override
    public void loadPluginDependencyClass() {
        // this is just to demonstrate that the Timestamper plugin is loaded
        TimestamperBuildWrapper.class.getClass();
    }

    public static class TimestamperSliceSpec extends UnorderedStringSlicerSpec<BuildableItemWithBuildWrappers> {

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

        public String getName(BuildableItemWithBuildWrappers item) {
            return item.getFullName();
        }

        public String getUrl() {
            return "timestamper";
        }

        @Override
        public boolean isBlankNeededForValues() {
            return false;
        }

        public List<String> getValues(BuildableItemWithBuildWrappers item) {
            BuildableItemWithBuildWrappers project = (BuildableItemWithBuildWrappers) item;

            String value;
            TimestamperBuildWrapper wrapper =
                    (TimestamperBuildWrapper) project.getBuildWrappersList().get(TimestamperBuildWrapper.class);
            if (wrapper != null) {
                value = Boolean.TRUE.toString();
            } else {
                value = DISABLED;
            }

            List<String> booleans = new ArrayList<String>();
            booleans.add(value);
            return booleans;
        }

        public List<BuildableItemWithBuildWrappers> getWorkDomain() {
            return TopLevelItemSelector.getAllTopLevelItems(BuildableItemWithBuildWrappers.class);
        }

        public boolean setValues(BuildableItemWithBuildWrappers item, List<String> set) {
            BuildableItemWithBuildWrappers project = (BuildableItemWithBuildWrappers) item;
            String value = set.iterator().next();

            boolean isTimestampWanted = Boolean.parseBoolean(value);

            boolean changed;

            TimestamperBuildWrapper wrapper =
                    (TimestamperBuildWrapper) project.getBuildWrappersList().get(TimestamperBuildWrapper.class);
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
                project.getBuildWrappersList().add(new TimestamperBuildWrapper());
            } else {
                changed = false;
            }

            return changed;
        }
    }
}
