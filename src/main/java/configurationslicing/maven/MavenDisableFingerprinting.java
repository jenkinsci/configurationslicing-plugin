package configurationslicing.maven;

import configurationslicing.BooleanSlicer;
import hudson.Extension;
import hudson.maven.MavenModuleSet;
import jenkins.model.Jenkins;

import java.util.List;

@Extension
public class MavenDisableFingerprinting extends BooleanSlicer<MavenModuleSet> {

    public MavenDisableFingerprinting() {
        super(new MavenDisableFingerprintingSlicerSpec());
    }
    
    public static class MavenDisableFingerprintingSlicerSpec implements BooleanSlicerSpec<MavenModuleSet> {

        public String getName() {
            return "Maven Disable automatic fingerprinting of consumed and produced artifacts";
        }

        public String getName(MavenModuleSet item) {
            return item.getFullName();
        }

        public String getUrl() {
            return "mavendisablefingerprinting";
        }

        @SuppressWarnings("unchecked")
		public List<MavenModuleSet> getWorkDomain() {
            return (List) Jenkins.getInstance().getAllItems(MavenModuleSet.class);
        }

		@Override
		public boolean getValue(MavenModuleSet item) {
            return item.isFingerprintingDisabled();
        }

		@Override
		public boolean setValue(MavenModuleSet item, boolean value) {
			item.setIsFingerprintingDisabled(value);
			return true;
		}
        
    }
}
 