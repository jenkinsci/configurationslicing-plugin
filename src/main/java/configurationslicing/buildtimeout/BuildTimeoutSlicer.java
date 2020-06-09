package configurationslicing.buildtimeout;

import hudson.Extension;
import hudson.model.BuildableItemWithBuildWrappers;
import hudson.model.BuildableItemWithBuildWrappers;
import hudson.model.Descriptor;
import hudson.plugins.build_timeout.BuildTimeoutWrapper;
import hudson.tasks.BuildWrapper;
import hudson.util.DescribableList;
import hudson.util.XStream2;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import com.thoughtworks.xstream.XStreamException;

import configurationslicing.TopLevelItemSelector;
import configurationslicing.UnorderedStringSlicer;

@Extension
public class BuildTimeoutSlicer extends UnorderedStringSlicer<BuildableItemWithBuildWrappers>{
    private static final Logger LOGGER = Logger.getLogger(BuildTimeoutSlicer.class.getName());

    public BuildTimeoutSlicer() {
        super(new BuildTimeoutSliceSpec());
    }

    @Override
    public boolean isLoaded() {
        try { 
            BuildTimeoutWrapper.class.toString();
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    public static class BuildTimeoutSliceSpec extends UnorderedStringSlicerSpec<BuildableItemWithBuildWrappers> {

        private static final String DISABLED = "(Disabled)";

        @Override
        public String getName() {
            return "Build Timeout";
        }

        @Override
        public String getUrl() {
            return "buildtimeout";
        }

        @Override
        public List<BuildableItemWithBuildWrappers> getWorkDomain() {
            return TopLevelItemSelector.getAllTopLevelItems(BuildableItemWithBuildWrappers.class);
        }

        @Override
        public List<String> getValues(BuildableItemWithBuildWrappers item) {
            XStream2 xs = getXStream();
            BuildableItemWithBuildWrappers bi = (BuildableItemWithBuildWrappers) item;
            DescribableList<BuildWrapper,Descriptor<BuildWrapper>> wrappers = bi.getBuildWrappersList();
            List<String> values = new ArrayList<String>();
            BuildTimeoutWrapper wrapper = wrappers.get(BuildTimeoutWrapper.class);
            if (wrapper != null) {
                StringWriter sw = new StringWriter();
                xs.toXML(wrapper, sw);
                String value = sw.toString();

                values.add(value);
            }
            if (values.isEmpty()) {
                values.add(DISABLED);
            }
            return values;
        }

        private XStream2 getXStream() {
            XStream2 xs = new XStream2();
            addSimpleAlias(xs, "hudson.plugins.build_timeout.BuildTimeoutWrapper");
            addSimpleAlias(xs, "hudson.plugins.build_timeout.impl.AbsoluteTimeOutStrategy");
            addSimpleAlias(xs, "hudson.plugins.build_timeout.impl.DeadlineTimeOutStrategy");
            addSimpleAlias(xs, "hudson.plugins.build_timeout.impl.ElasticTimeOutStrategy");
            addSimpleAlias(xs, "hudson.plugins.build_timeout.impl.LikelyStuckTimeOutStrategy");
            addSimpleAlias(xs, "hudson.plugins.build_timeout.impl.NoActivityTimeOutStrategy");
            addSimpleAlias(xs, "hudson.plugins.build_timeout.operations.AbortOperation");
            addSimpleAlias(xs, "hudson.plugins.build_timeout.operations.BuildStepOperation");
            addSimpleAlias(xs, "hudson.plugins.build_timeout.operations.FailOperation");
            addSimpleAlias(xs, "hudson.plugins.build_timeout.operations.WriteDescriptionOperation");
            return xs;
        }

        private void addSimpleAlias(XStream2 xs, String className) {
            Class<?> type;
            try {
                type = Class.forName(className);
                String name = type.getSimpleName();
                xs.alias(name, type);
            } catch (ClassNotFoundException e) {
                LOGGER.info("Cannot load " + className);
            }
        }

        @Override
        public boolean setValues(BuildableItemWithBuildWrappers item, List<String> set) {
            LOGGER.info("BuildTimeoutSlicer.setValues for item " + item.getName());
            XStream2 xs = getXStream();
            BuildableItemWithBuildWrappers bi =
                    (BuildableItemWithBuildWrappers) item;
            DescribableList<BuildWrapper,Descriptor<BuildWrapper>> wrappers =
                    bi.getBuildWrappersList();
            boolean changed = false;
            BuildTimeoutWrapper wrapper = wrappers.get(BuildTimeoutWrapper.class);
            BuildTimeoutWrapper newWrapper = null;
            boolean delete = false;
            String line = set.iterator().next();
            if (DISABLED.equals(line) || StringUtils.isEmpty(line)) {
                delete = true;
            } else {
                try {
                    Object o = xs.fromXML(line);
                    if(o instanceof BuildTimeoutWrapper) {
                        newWrapper = (BuildTimeoutWrapper)o;
                        changed=true;
                    }
                } catch (XStreamException xse) {
                    LOGGER.warning("XStreamException parsing XML for BuildTimeoutSlicer: " + xse.getMessage());
                    changed=false;
                }
            }

            if (delete) {
                if (wrapper != null) {
                    wrappers.remove(wrapper);
                    changed=true;
                }
            } else if (newWrapper != null && changed) {
                try {
                    wrappers.replace(newWrapper);
                } catch (IOException e) {
                    LOGGER.warning("IOException Thrown replacing wrapper value");
                    return false;
                }
            }

            return changed;
        }

        @Override
        public String getName(BuildableItemWithBuildWrappers item) {
            return item.getFullName();
        }
        @Override
        public String getDefaultValueString() {
            return DISABLED;
        }
        @Override
        public String getConfiguredValueDescription() {
            return "Build Timeout XML";
        }
    }
}
