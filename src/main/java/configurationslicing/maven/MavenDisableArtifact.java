package configurationslicing.maven;

import configurationslicing.BooleanSlicer;
import hudson.Extension;
import hudson.maven.MavenModuleSet;
import jenkins.model.Jenkins;

import java.util.List;

@Extension(optional=true)
public class MavenDisableArtifact extends BooleanSlicer<MavenModuleSet> {

    public MavenDisableArtifact() {
        super(new MavenDisableArtifactSlicerSpec());
    }
    
    public static class MavenDisableArtifactSlicerSpec implements BooleanSlicerSpec<MavenModuleSet> {

        public String getName() {
            return "Maven Disable automatic artifact archiving";
        }

        public String getName(MavenModuleSet item) {
            return item.getFullName();
        }

        public String getUrl() {
            return "mavendisableartifact";
        }

        @SuppressWarnings("unchecked")
        public List<MavenModuleSet> getWorkDomain() {
            return Jenkins.getInstance().getAllItems(MavenModuleSet.class);
        }

        @Override
        public boolean getValue(MavenModuleSet item) {
            return item.isArchivingDisabled();
        }

        @Override
        public boolean setValue(MavenModuleSet item, boolean value) {
            item.setIsArchivingDisabled(value);
            return true;
        }
        
    }
}
 