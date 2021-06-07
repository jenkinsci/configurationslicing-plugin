package configurationslicing.maven;

import hudson.Extension;
import hudson.maven.MavenModuleSet;
import hudson.model.Hudson;
import jenkins.model.Jenkins;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import configurationslicing.UnorderedStringSlicer;

@Extension(optional = true)
public class MavenGoals extends UnorderedStringSlicer<MavenModuleSet> {

    public MavenGoals() {
        super(new MavenGoalsSlicerSpec());
    }

    @Override
    public void loadPluginDependencyClass() {
        // this is just to demonstrate that the Maven plugin is loaded
        MavenModuleSet.class.getClass();
    }

    public static class MavenGoalsSlicerSpec extends UnorderedStringSlicerSpec<MavenModuleSet> {
        private static final String DEFAULT = "(Default)";

        public String getDefaultValueString() {
        	return DEFAULT;
        }

        public String getName() {
            return "Maven Goals and Options (Maven project)";
        }

        public String getName(MavenModuleSet item) {
            return item.getFullName();
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

        @SuppressWarnings("unchecked")
		public List<MavenModuleSet> getWorkDomain() {
            return (List) Jenkins.get().getAllItems(MavenModuleSet.class);
        }

        public boolean setValues(MavenModuleSet item, List<String> set) {
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
