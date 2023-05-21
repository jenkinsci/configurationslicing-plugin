package configurationslicing.jdk;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Hudson;
import hudson.model.JDK;
import jenkins.model.Jenkins;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import configurationslicing.TopLevelItemSelector;
import configurationslicing.UnorderedStringSlicer;

@Extension
public class JdkSlicer extends UnorderedStringSlicer<AbstractProject> {

    public JdkSlicer() {
        super(new JdkSlicerSpec());
    }

    public static class JdkSlicerSpec extends UnorderedStringSlicerSpec<AbstractProject> {
        private static final String DEFAULT = "(Default)";

        public String getDefaultValueString() {
        	return DEFAULT;
        }
        public String getName() {
            return "JDK per project";
        }

        public String getName(AbstractProject item) {
            return item.getFullName();
        }

        public String getUrl() {
            return "projectjdk";
        }

        public List<String> getValues(AbstractProject item) {
            List<String> ret = new ArrayList<String>();
            JDK jdk = item.getJDK();
            String name = jdk == null ? DEFAULT : jdk.getName();
            ret.add(name);
            return ret;
        }

		public List<AbstractProject> getWorkDomain() {
            return TopLevelItemSelector.getAllTopLevelItems(AbstractProject.class);
        }

        public boolean setValues(AbstractProject item, List<String> set) {
            if(set.size() == 0) return false;
            Jenkins hudson = Jenkins.get();
            JDK jdk=null;
            for(String val : set) {
                jdk = hudson.getJDK(val);
                if(jdk!=null) break;
            }
            JDK oldJdk = item.getJDK();
            if (!equals(oldJdk, jdk)) {
	            try {
	                if (jdk != null) {
                            item.setJDK(jdk);
                        }
	                return true;
	            } catch (IOException e) {
	                e.printStackTrace();
	                return false;
	            }
            } else {
            	return false;
            }
        }
        public static boolean equals(JDK j1, JDK j2) {
        	if (ObjectUtils.equals(j1, j2)) {
        		return true;
        	}
        	if (j1 == null || j2 == null) {
        		return false;
        	}
        	if (!StringUtils.equals(j1.getHome(), j2.getHome())) {
        		return false;
        	}
        	if (!StringUtils.equals(j1.getName(), j2.getName())) {
        		return false;
        	}
        	return true;
        }

    }

}
