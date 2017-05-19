package configurationslicing.maven;

import configurationslicing.BooleanSlicer;
import hudson.Extension;
import hudson.maven.MavenModuleSet;
import jenkins.model.Jenkins;

import java.util.List;

@Extension(optional=true)
public class MavenIncremental extends BooleanSlicer<MavenModuleSet> {

    public MavenIncremental() {
        super(new MavenIncrementalSlicerSpec());
    }
    
    public static class MavenIncrementalSlicerSpec implements BooleanSlicerSpec<MavenModuleSet> {

        public String getName() {
            return "Maven Incremental Build";
        }

        public String getName(MavenModuleSet item) {
            return item.getFullName();
        }

        public String getUrl() {
            return "mavenincremental";
        }

        @SuppressWarnings("unchecked")
        public List<MavenModuleSet> getWorkDomain() {
            return Jenkins.getInstance().getAllItems(MavenModuleSet.class);
        }

        @Override
        public boolean getValue(MavenModuleSet item) {
            return item.isIncrementalBuild();
        }

        @Override
        public boolean setValue(MavenModuleSet item, boolean value) {
            item.setIncrementalBuild(value);
            return true;
        }
        
    }
}
 