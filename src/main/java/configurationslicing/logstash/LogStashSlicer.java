package configurationslicing.logstash;

import configurationslicing.BooleanSlicer;
import configurationslicing.TopLevelItemSelector;
import hudson.Extension;
import hudson.Util;
import hudson.maven.MavenModuleSet;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.Project;
import hudson.tasks.BuildWrapper;
import hudson.util.DescribableList;
import java.util.List;
import jenkins.plugins.logstash.LogstashBuildWrapper;

@Extension(optional = true)
public class LogStashSlicer extends BooleanSlicer<AbstractProject> {
    public LogStashSlicer() {
        super(new LogstashSpec());
    }

    public static class LogstashSpec implements BooleanSlicer.BooleanSlicerSpec<AbstractProject> {

        @Override
        public String getName() {
            return "Logstash Slicer";
        }

        @Override
        public String getUrl() {
            return "logstash";
        }

        public List<AbstractProject> getWorkDomain() {
            return TopLevelItemSelector.getAllTopLevelItems(AbstractProject.class);
        }

        public boolean getValue(AbstractProject item) {
            DescribableList<BuildWrapper, Descriptor<BuildWrapper>> buildWrappersList = getBuildWrappers(item);
            if (buildWrappersList == null) {
                return false;
            }
            return !buildWrappersList.getAll(LogstashBuildWrapper.class).isEmpty();
        }

        private DescribableList<BuildWrapper, Descriptor<BuildWrapper>> getBuildWrappers(AbstractProject item) {
            if (item instanceof Project<?, ?> project) {
                return project.getBuildWrappersList();
            } else if (item instanceof MavenModuleSet set) {
                return set.getBuildWrappersList();
            } else {
                return null;
            }
        }

        @Override
        public String getName(AbstractProject item) {
            return item.getName();
        }

        @Override
        public boolean setValue(AbstractProject item, boolean value) {
            LogstashBuildWrapper logstashWrapper = new LogstashBuildWrapper();
            if (item instanceof Project<?, ?> project) {
                DescribableList bwList = project.getBuildWrappersList();
                List<LogstashBuildWrapper> lsList = Util.filter(bwList, LogstashBuildWrapper.class);
                if (lsList.isEmpty() != value) {
                    // already matches value.  Do nothing.
                    return false;
                }
                if (value) {
                    bwList.add(new LogstashBuildWrapper());
                } else {
                    bwList.removeAll(lsList);
                }
                return true;
            } else if (item instanceof MavenModuleSet set) {
                DescribableList bwList = set.getBuildWrappersList();
                List<LogstashBuildWrapper> lsList = Util.filter(bwList, LogstashBuildWrapper.class);
                if (lsList.isEmpty() != value) {
                    // already matches value.  Do nothing.
                    return false;
                }
                if (value) {
                    bwList.add(new LogstashBuildWrapper());
                } else {
                    bwList.removeAll(lsList);
                }
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean isLoaded() {
        try {
            new LogstashBuildWrapper();
            return true;
        } catch (Throwable t) {
            return false;
        }
    }
}
