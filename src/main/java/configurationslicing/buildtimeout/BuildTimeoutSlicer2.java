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
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import configurationslicing.ParametersStringSliceSpec;
import configurationslicing.ParametersStringSlicer;

@Extension
public class BuildTimeoutSlicer2 extends ParametersStringSlicer<AbstractProject<?,?>>{

	private static final String DEFAULT_TYPE = "absolute";
	
    public BuildTimeoutSlicer2() {
        super(new BuildTimeoutSliceSpec2());
    }

	@Override
    public boolean isLoaded() {
    	try {
    		BuildTimeoutSliceSpec2.newBuildTimeoutWrapper(0, false, DEFAULT_TYPE);
    		return true;
    	} catch (Throwable t) {
    		return false;
    	}
    }

    public static class BuildTimeoutSliceSpec2 extends ParametersStringSliceSpec<AbstractProject<?,?>> {

        private static final String DISABLED = "(Disabled)";

		@Override
		public String getName() {
			return "Build Timeout 2";
		}

		@Override
		public String getUrl() {
			return "buildtimeout2";
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public List<AbstractProject<?, ?>> getWorkDomain() {
            return (List) Hudson.getInstance().getAllItems(BuildableItemWithBuildWrappers.class);
		}
		
		@Override
		public List<String> getParamNames() {
			List<String> names = new ArrayList<String>();
			names.add("Timout Minutes");
			names.add("Fail Build");
			return names;
		}

		@Override
		public List<String> doGetValues(AbstractProject<?, ?> item) {
			BuildableItemWithBuildWrappers bi = (BuildableItemWithBuildWrappers) item;
			DescribableList<BuildWrapper,Descriptor<BuildWrapper>> wrappers = bi.getBuildWrappersList();
        	List<String> values = new ArrayList<String>();
			BuildTimeoutWrapper wrapper = wrappers.get(BuildTimeoutWrapper.class);
			if (wrapper != null) {
//				String value = wrapper.timeoutMinutes + SEPARATOR + wrapper.failBuild;
//				values.add(value);
				values.add(String.valueOf(wrapper.timeoutMinutes));
				values.add(String.valueOf(wrapper.failBuild));
			} else {
				values.add(DISABLED);
				values.add(DISABLED);
			}
			return values;
		}

		@Override
		public boolean doSetValues(AbstractProject<?, ?> item, List<String> values) {
			BuildableItemWithBuildWrappers bi = (BuildableItemWithBuildWrappers) item;
			DescribableList<BuildWrapper,Descriptor<BuildWrapper>> wrappers = bi.getBuildWrappersList();
			boolean changed = false;
			BuildTimeoutWrapper wrapper = wrappers.get(BuildTimeoutWrapper.class);

			boolean delete = false;
			boolean newFail = false;
			String oldType = DEFAULT_TYPE;
			int newTimeout = 0;
			String disabledTest = values.get(0);
			if (DISABLED.equals(disabledTest) || StringUtils.isEmpty(disabledTest)) {
				delete = true;
			} else {
				newTimeout = Integer.parseInt(values.get(0));
				newFail = Boolean.parseBoolean(values.get(1));
			}

			if (wrapper != null) {
				boolean oldFail = wrapper.failBuild;
				int oldTimeout = wrapper.timeoutMinutes;
				oldType = getType(wrapper);
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
					wrapper = newBuildTimeoutWrapper(newTimeout, newFail, oldType);
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
            return item.getFullName();
		}
		@Override
		public String doGetDefaultValueString() {
			return DISABLED;
		}
		public static String getType(BuildTimeoutWrapper wrapper) {
			try {
				Field field = BuildTimeoutWrapper.class.getDeclaredField("timeoutType");
				String value = (String) field.get(wrapper);
				return value;
			} catch (Exception e) {
				return DEFAULT_TYPE;
			}
		}
		public static BuildTimeoutWrapper newBuildTimeoutWrapper(int timeoutMinutes, boolean failBuild, String type) {
			//	(int timeoutMinutes, boolean failBuild)
			//	(int timeoutMinutes, boolean failBuild, boolean writingDescription, int timeoutPercentage, int timeoutMinutesElasticDefault, String timeoutType)
			
			Class<BuildTimeoutWrapper> cls = BuildTimeoutWrapper.class;
			Class<?>[] types1 = new Class<?>[] { Integer.TYPE, Boolean.TYPE };
			Constructor<BuildTimeoutWrapper> cons;
			try {
				cons = cls.getDeclaredConstructor(types1);
			} catch (Exception e) {
				cons = null;
			}
			
			Object[] args;
			if (cons != null) {
				args = new Object[] { timeoutMinutes, failBuild };
			} else {
				try {
					Class<?>[] types2 = new Class<?>[] { Integer.TYPE, Boolean.TYPE, Boolean.TYPE, Integer.TYPE, Integer.TYPE, String.class };
					cons = cls.getDeclaredConstructor(types2);
					args = new Object[] { timeoutMinutes, failBuild, false, 0, 0, type };
				} catch (Exception e) {
					throw new UnsupportedClassVersionError("Cannot find a version of BuildTimeoutWrapper constructor that can be used:" + e.getMessage());
				}
			}
			BuildTimeoutWrapper wrapper;
			try {
				wrapper = (BuildTimeoutWrapper) cons.newInstance(args);
			} catch (Exception e) {
				throw new UnsupportedClassVersionError("Cannot find a version of BuildTimeoutWrapper constructor that can be used:" + e.getMessage());
			}
			return wrapper;
		}
		
    }
}
