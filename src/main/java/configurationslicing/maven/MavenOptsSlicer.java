package configurationslicing.maven;

import hudson.Extension;
import hudson.maven.MavenModuleSet;
import hudson.model.Hudson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import configurationslicing.UnorderedStringSlicer;

@Extension
public class MavenOptsSlicer extends UnorderedStringSlicer<MavenModuleSet> {

    public MavenOptsSlicer() {
        super(new MavenOptsSlicerSpec());
    }
    
    public static class MavenOptsSlicerSpec implements UnorderedStringSlicerSpec<MavenModuleSet> {

        public String getName() {
            return "MAVEN_OPTS per Maven project";
        }

        public String getName(MavenModuleSet item) {
            return item.getName();
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

        public List<MavenModuleSet> getWorkDomain() {
            return (List)Hudson.getInstance().getItems(MavenModuleSet.class);
        }

        public boolean setValues(MavenModuleSet item, Set<String> set) {
            if(set.isEmpty()) return false;
            String value = set.iterator().next();
            if(value.equals(MavenModuleSet.DESCRIPTOR.getGlobalMavenOpts())) {
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
 