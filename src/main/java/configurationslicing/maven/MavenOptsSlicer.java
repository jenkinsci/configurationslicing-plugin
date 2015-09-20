package configurationslicing.maven;

import hudson.Extension;
import hudson.maven.MavenModuleSet;
import hudson.maven.MavenModuleSet.DescriptorImpl;
import hudson.model.Hudson;
import hudson.tasks.Maven;
import jenkins.model.Jenkins;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import configurationslicing.UnorderedStringSlicer;

@Extension
public class MavenOptsSlicer extends UnorderedStringSlicer<MavenModuleSet> {

    public MavenOptsSlicer() {
        super(new MavenOptsSlicerSpec());
    }
    
    @Override
    public void loadPluginDependencyClass() {
        // this is just to demonstrate that the Maven plugin is loaded
        MavenModuleSet.class.getClass();
    }
    
    public static class MavenOptsSlicerSpec extends UnorderedStringSlicerSpec<MavenModuleSet> {

        public String getDefaultValueString() {
        	return null;
        }

        public String getName() {
            return "MAVEN_OPTS per Maven project";
        }

        public String getName(MavenModuleSet item) {
            return item.getFullName();
        }

        public String getUrl() {
            return "mavenopts";
        }

        public List<String> getValues(MavenModuleSet item) {
            List<String> ret = new ArrayList<String>();
            String mavenOpts = item.getMavenOpts();
            ret.add(mavenOpts);
            return ret;
        }

        @SuppressWarnings("unchecked")
		public List<MavenModuleSet> getWorkDomain() {
            return (List) Jenkins.getInstance().getAllItems(MavenModuleSet.class);
        }

        public boolean setValues(MavenModuleSet item, List<String> set) {
            if(set.isEmpty()) return false;
            String value = set.iterator().next();
            DescriptorImpl descriptor =
                    Jenkins.getInstance().getDescriptorByType(MavenModuleSet.DescriptorImpl.class);
            if(value.equals(descriptor.getGlobalMavenOpts())) {
                item.setMavenOpts(null);
            } else {
                item.setMavenOpts(value);
            }
            try {
                item.save();
            } catch (IOException e) {
                return false;
            }
            return true;
        }
        
    }
}
 