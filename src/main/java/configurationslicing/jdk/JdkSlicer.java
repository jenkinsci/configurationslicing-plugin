package configurationslicing.jdk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Hudson;
import hudson.model.JDK;
import configurationslicing.UnorderedStringSlicer;
import configurationslicing.UnorderedStringSlicer.UnorderedStringSlicerSpec;

@Extension
public class JdkSlicer extends UnorderedStringSlicer<AbstractProject<?, ?>> {

    public JdkSlicer() {
        super(new JdkSlicerSpec());
    }
    
    public static class JdkSlicerSpec implements UnorderedStringSlicerSpec<AbstractProject<?,?>> {
        private static final String DEFAULT = "(Default)";

        public String getName() {
            return "JDK per project";
        }

        public String getName(AbstractProject<?, ?> item) {
            return item.getName();
        }

        public String getUrl() {
            return "projectjdk";
        }

        public List<String> getValues(AbstractProject<?, ?> item) {
            List<String> ret = new ArrayList<String>();
            JDK jdk = item.getJDK();
            String name = jdk == null ? DEFAULT : jdk.getName();
            ret.add(name);
            return ret;
        }

        public List<AbstractProject<?, ?>> getWorkDomain() {
            return (List)Hudson.getInstance().getItems(AbstractProject.class);
        }

        public boolean setValues(AbstractProject<?, ?> item, Set<String> set) {
            if(set.size() == 0) return false;
            Hudson hudson = Hudson.getInstance();
            JDK jdk=null;
            for(String val : set) {
                jdk = hudson.getJDK(val);
                if(jdk!=null) break;
            }
            try {
                item.setJDK(jdk);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        
    }

}
