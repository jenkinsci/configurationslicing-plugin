package configurationslicing.maven;

import hudson.Extension;
import hudson.maven.MavenModuleSet;

import java.util.List;

import jenkins.model.Jenkins;
import configurationslicing.BooleanSlicer;

@Extension
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
            return (List) Jenkins.get().getAllItems(MavenModuleSet.class);
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
