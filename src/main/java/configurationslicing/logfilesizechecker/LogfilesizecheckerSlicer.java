package configurationslicing.logfilesizechecker;

import hudson.Extension;
import hudson.model.BuildableItemWithBuildWrappers;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.plugins.logfilesizechecker.LogfilesizecheckerWrapper;
import hudson.tasks.BuildWrapper;
import hudson.util.DescribableList;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import configurationslicing.UnorderedStringSlicer;

/**
 * Slicing the configuration of the Logfilesizechecker plugin.
 * @author kstutz
 */
@Extension
public class LogfilesizecheckerSlicer extends UnorderedStringSlicer<AbstractProject<?, ?>> {

    public LogfilesizecheckerSlicer() {
        super(new LogfilesizeSliceSpec());
    }

    @Override
    public boolean isLoaded() {
        try {
            LogfilesizeSliceSpec.newLogfilesizecheckerWrapper(0, false, false);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }
    
    public static class LogfilesizeSliceSpec extends UnorderedStringSlicerSpec<AbstractProject<?, ?>> {
        private static final String DISABLED = "(Disabled)";
        private static final String SEPARATOR = ",";

        @Override
        public String getName() {
            return "Log File Size";
        }

        @Override
        public String getUrl() {
            return "logfilesize";
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        public List<AbstractProject<?, ?>> getWorkDomain() {
            return (List) Hudson.getInstance().getItems(BuildableItemWithBuildWrappers.class);
        }

        @Override
        public List<String> getValues(AbstractProject<?, ?> item) {
            final BuildableItemWithBuildWrappers bi = (BuildableItemWithBuildWrappers) item;
            final DescribableList<BuildWrapper, Descriptor<BuildWrapper>> wrappers = bi.getBuildWrappersList();
            final List<String> values = new ArrayList<String>();
            final LogfilesizecheckerWrapper wrapper = wrappers.get(LogfilesizecheckerWrapper.class);
            if (wrapper != null) {
                final String value = wrapper.setOwn + SEPARATOR + wrapper.maxLogSize + SEPARATOR + wrapper.failBuild;
                values.add(value);
            }
            if (values.isEmpty()) {
                values.add(DISABLED);
            }
            return values;
        }

        @Override
        public boolean setValues(AbstractProject<?, ?> item, List<String> set) {
            final BuildableItemWithBuildWrappers bi = (BuildableItemWithBuildWrappers) item;
            final DescribableList<BuildWrapper, Descriptor<BuildWrapper>> wrappers = bi.getBuildWrappersList();
            boolean changed = false;
            LogfilesizecheckerWrapper wrapper = wrappers.get(LogfilesizecheckerWrapper.class);

            boolean delete = false;
            boolean newSetOwn = false;
            boolean newFail = false;
            int newMaxLogSize = 0;
            final String line = set.iterator().next();
            if (DISABLED.equals(line) || StringUtils.isEmpty(line)) {
                delete = true;
            } else {
                final String[] split = line.split(SEPARATOR);
                newSetOwn = Boolean.parseBoolean(split[0]);
                newMaxLogSize = Integer.parseInt(split[1]);
                newFail = Boolean.parseBoolean(split[2]);
            }

            if (wrapper != null) {
                final boolean oldSetOwn = wrapper.setOwn;
                final int oldMaxLogSize = wrapper.maxLogSize;
                final boolean oldFail = wrapper.failBuild;
                if (newMaxLogSize != oldMaxLogSize || newFail != oldFail || newSetOwn != oldSetOwn) {
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
                    wrapper = newLogfilesizecheckerWrapper(newMaxLogSize, newFail, newSetOwn);
                    wrappers.replace(wrapper);
                } catch (IOException e) {
                    System.err.println("IOException thrown replacing wrapper value");
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
            return "SetOwn, MaxLogSize in MB, Fail <i>(e.g. true,7,false)</i>" 
                    + "<ul><li>Set your own maximum log size instead of using default (true/false)</li>" 
                    + "<li>Maximum log size (int) (use 0 to use global default)</li>" 
                    + "<li>Mark build as failed instead of aborted (true/false)</li></ul>";
        }

        public static LogfilesizecheckerWrapper newLogfilesizecheckerWrapper(int maxLogSize, boolean failBuild, boolean setOwn) {
            final Class<LogfilesizecheckerWrapper> cls = LogfilesizecheckerWrapper.class;
            final Class<?>[] types = new Class<?>[] {Integer.TYPE, Boolean.TYPE, Boolean.TYPE };
            Constructor<LogfilesizecheckerWrapper> cons;
            try {
                cons = cls.getDeclaredConstructor(types);
            } catch (Exception e) {
                throw new UnsupportedClassVersionError("Cannot find a version of LogfilesizecheckerWrapper constructor that can be used:" + e.getMessage());
            }
            
            Object[] args = null;
            if (cons != null) {
                args = new Object[] {maxLogSize, failBuild, setOwn};
            }

            LogfilesizecheckerWrapper wrapper;
            try {
                wrapper = (LogfilesizecheckerWrapper) cons.newInstance(args);
            } catch (Exception e) {
                throw new UnsupportedClassVersionError("Cannot find a version of LogfilesizecheckerWrapper constructor that can be used:" + e.getMessage());
            }
            return wrapper;
        }
    }
}