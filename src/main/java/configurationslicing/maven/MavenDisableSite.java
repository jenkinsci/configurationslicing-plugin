package configurationslicing.maven;

import configurationslicing.BooleanSlicer;
import hudson.Extension;
import hudson.maven.MavenModuleSet;
import jenkins.model.Jenkins;

import java.util.List;

@Extension(optional=true)
public class MavenDisableSite extends BooleanSlicer<MavenModuleSet> {

    public MavenDisableSite() {
        super(new MavenDisableSiteSlicerSpec());
    }
    
    public static class MavenDisableSiteSlicerSpec implements BooleanSlicerSpec<MavenModuleSet> {

        public String getName() {
            return "Maven Disable automatic site documentation artifact archiving";
        }

        public String getName(MavenModuleSet item) {
            return item.getFullName();
        }

        public String getUrl() {
            return "mavendisablesite";
        }

        @SuppressWarnings("unchecked")
        public List<MavenModuleSet> getWorkDomain() {
            return Jenkins.getInstance().getAllItems(MavenModuleSet.class);
        }

        @Override
        public boolean getValue(MavenModuleSet item) {
            return item.isSiteArchivingDisabled();
        }

        @Override
        public boolean setValue(MavenModuleSet item, boolean value) {
            item.setIsSiteArchivingDisabled(value);
            return true;
        }
        
    }
}
 