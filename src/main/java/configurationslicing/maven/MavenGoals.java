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
public class MavenGoals extends UnorderedStringSlicer<MavenModuleSet> {

    public MavenGoals() {
        super(new MavenGoalsSlicerSpec());
    }
    
    public static class MavenGoalsSlicerSpec implements UnorderedStringSlicerSpec<MavenModuleSet> {
        private static final String DEFAULT = "(Default)";

        public String getName() {
            return "Goals and Options per Maven project";
        }

        public String getName(MavenModuleSet item) {
            return item.getName();
        }

        public String getUrl() {
            return "mavengoals";
        }

        public List<String> getValues(MavenModuleSet item) {
            List<String> ret = new ArrayList<String>();
            String goals = item.getUserConfiguredGoals();
            ret.add(goals == null ? DEFAULT : goals);
            return ret;
        }

        public List<MavenModuleSet> getWorkDomain() {
            return (List)Hudson.getInstance().getItems(MavenModuleSet.class);
        }

        public boolean setValues(MavenModuleSet item, Set<String> set) {
            if(set.isEmpty()) return false;
            String value = set.iterator().next();
            if(DEFAULT.equalsIgnoreCase(value)) {
                item.setGoals(null);
            } else {
                item.setGoals(value);
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
 