package configurationslicing.buildtimeout;

import hudson.Extension;
import hudson.model.BuildableItemWithBuildWrappers;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.plugins.build_timeout.BuildTimeoutWrapper;
import hudson.tasks.BuildWrapper;
import hudson.util.DescribableList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import configurationslicing.UnorderedStringSlicer;

@Extension
public class BuildTimeoutSlicer extends UnorderedStringSlicer<AbstractProject<?,?>>{

    public BuildTimeoutSlicer() {
        super(new BuildTimeoutSliceSpec());
    }

	@Override
    public boolean isLoaded() {
    	try {
    		new BuildTimeoutWrapper(0, false);
    		return true;
    	} catch (Throwable t) {
    		return false;
    	}
    }

    public static class BuildTimeoutSliceSpec extends UnorderedStringSlicerSpec<AbstractProject<?,?>> {

        private static final String DISABLED = "(Disabled)";
        private static final String SEPARATOR = ",";

		@Override
		public String getName() {
			return "Build Timeout";
		}

		@Override
		public String getUrl() {
			return "buildtimeout";
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<AbstractProject<?, ?>> getWorkDomain() {
            return (List) Hudson.getInstance().getItems(BuildableItemWithBuildWrappers.class);
		}

		@Override
		public List<String> getValues(AbstractProject<?, ?> item) {
			BuildableItemWithBuildWrappers bi = (BuildableItemWithBuildWrappers) item;
			DescribableList<BuildWrapper,Descriptor<BuildWrapper>> wrappers = bi.getBuildWrappersList();
        	List<String> values = new ArrayList<String>();
			BuildTimeoutWrapper wrapper = wrappers.get(BuildTimeoutWrapper.class);
			if (wrapper != null) {
				String value = wrapper.timeoutMinutes + SEPARATOR + wrapper.failBuild;
				values.add(value);
			}
			if (values.isEmpty()) {
				values.add(DISABLED);
			}
			return values;
		}

		@Override
		public boolean setValues(AbstractProject<?, ?> item, Set<String> set) {
			BuildableItemWithBuildWrappers bi = (BuildableItemWithBuildWrappers) item;
			DescribableList<BuildWrapper,Descriptor<BuildWrapper>> wrappers = bi.getBuildWrappersList();
			boolean changed = false;
			BuildTimeoutWrapper wrapper = wrappers.get(BuildTimeoutWrapper.class);

			boolean delete = false;
			boolean newFail = false;
			int newTimeout = 0;
			String line = set.iterator().next();
			if (DISABLED.equals(line) || StringUtils.isEmpty(line)) {
				delete = true;
			} else {
				String[] split = line.split(SEPARATOR);
				newTimeout = Integer.parseInt(split[0]);
				newFail = Boolean.parseBoolean(split[1]);
			}

			if (wrapper != null) {
				boolean oldFail = wrapper.failBuild;
				int oldTimeout = wrapper.timeoutMinutes;
				if (newFail != oldFail || newTimeout != oldTimeout) {
					changed = true;
				}
			} else {
				changed = true;
			}
			
			if (delete) {
				if (wrapper != null) {
					try {
						wrappers.remove(wrapper);
					} catch (IOException e) {
						System.err.println("IOException Thrown removing wrapper value");
						return false;
					}
				}
			} else if (changed) {
				try {
					wrapper = new BuildTimeoutWrapper(newTimeout, newFail);
					wrappers.replace(wrapper);
				} catch (IOException e) {
					System.err.println("IOException Thrown replacing wrapper value");
					return false;
				}
			}
			
			return changed;
		}

		@Override
		public String getName(AbstractProject<?, ?> item) {
            return item.getName();
		}
		@Override
		public String getDefaultValueString() {
			return DISABLED;
		}
		@Override
		public String getConfiguredValueDescription() {
			return "Timeout,Fail<br/><i>(e.g. 180,false)</i>";
		}
		
    }
}
