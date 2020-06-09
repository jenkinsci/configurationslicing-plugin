package configurationslicing.maven;

import configurationslicing.BooleanSlicer;
import hudson.Extension;
import hudson.maven.MavenModuleSet;
import jenkins.model.Jenkins;

import java.util.List;

/**
 * A simple boolean configuration slicer to set the "Build whenever a SNAPSHOT dependency is built" build trigger in Maven projects
 * Created by Jeff Bischoff on 4/12/16.
 */
@Extension
public class MavenSnapshotBuildTrigger extends BooleanSlicer<MavenModuleSet> {

    public MavenSnapshotBuildTrigger() {
        super(new MavenSnapshotBuildTriggerSlicerSpec());
    }

    public static class MavenSnapshotBuildTriggerSlicerSpec implements BooleanSlicerSpec<MavenModuleSet> {

        public String getName() {
            return "Maven Snapshot dependency Build Trigger";
        }

        public String getName(MavenModuleSet item) {
            return item.getFullName();
        }

        public String getUrl() {
            return "mavenSnapshotBuildTrigger";
        }

        @SuppressWarnings("unchecked")
        public List<MavenModuleSet> getWorkDomain() {
            return (List) Jenkins.getInstance().getAllItems(MavenModuleSet.class);
        }

        @Override
        public boolean getValue(MavenModuleSet item) {
            // The UI displays the box checked if upstream builds are *not* ignored, so this slicer should match that behavior
            return ! item.ignoreUpstremChanges();
        }

        @Override
        public boolean setValue(MavenModuleSet item, boolean value) {
            // The UI displays the box checked if upstream builds are *not* ignored, so this slicer should match that behavior
            boolean ignored = ! value;
            item.setIgnoreUpstremChanges(ignored);
            return true;
        }

    }
}
