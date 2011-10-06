package configurationslicing.customworkspace;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.FreeStyleProject;
import hudson.model.Hudson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import configurationslicing.UnorderedStringSlicer;

/**
 * @author jacob_robertson
 */
@Extension
public class CustomWorkspaceStringSlicer extends UnorderedStringSlicer<AbstractProject<?,?>>{

    public CustomWorkspaceStringSlicer() {
        super(new CustomWorkspaceStringSliceSpec());
    }

    public static class CustomWorkspaceStringSliceSpec extends UnorderedStringSlicerSpec<AbstractProject<?,?>> {

        private static final String DISABLED = "(Disabled)";

        public String getDefaultValueString() {
        	return DISABLED;
        }
        public String getName() {
            return "Custom Workspace Slicer";
        }

        public String getName(AbstractProject<?, ?> item) {
            return item.getName();
        }

        public String getUrl() {
            return "customworkspace";
        }

        public List<String> getValues(AbstractProject<?, ?> item) {
        	List<String> workspace = new ArrayList<String>();
        	if (item instanceof FreeStyleProject) {
        		FreeStyleProject project = (FreeStyleProject) item;
        		String ws = project.getCustomWorkspace();
        		if (ws != null) {
        			workspace.add(ws);
        		}
        	}
        	if (workspace.isEmpty()) {
        		workspace.add(DISABLED);
        	}
            return workspace;
        }

        @SuppressWarnings({ "unchecked" })
		public List<AbstractProject<?, ?>> getWorkDomain() {
            return (List) Hudson.getInstance().getItems(FreeStyleProject.class);
        }

        public boolean setValues(AbstractProject<?, ?> item, Set<String> set) {
        	try {
	        	if (item instanceof FreeStyleProject) {
	        		FreeStyleProject project = (FreeStyleProject) item;
        			String ws;
	        		if (set.isEmpty()) {
	        			ws = null;
	        		} else {
	        			ws = set.iterator().next();
	        		}
	        		if (DISABLED.equals(ws)) {
	        			ws = null;
	        		}
	        		String old = project.getCustomWorkspace();
	        		// check for equal - we don't want to trigger a change for no reason
	        		if (!StringUtils.equals(ws, old)) {
	        			project.setCustomWorkspace(ws);
	        		}
	        	}
	        	return true;
        	} catch (IOException ioe) {
        		return false;
        	}
        }
    }
}
