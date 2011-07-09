package configurationslicing.customworkspace;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.FreeStyleProject;
import hudson.model.Hudson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import configurationslicing.UnorderedStringSlicer;

/**
 * @author jacob_robertson
 */
@Extension
public class CustomWorkspaceStringSlicer extends UnorderedStringSlicer<AbstractProject<?,?>>{

    public CustomWorkspaceStringSlicer() {
        super(new TimerSliceSpec());
    }
    public static class TimerSliceSpec implements UnorderedStringSlicerSpec<AbstractProject<?,?>> {

        private static final String DISABLED = "(Disabled)";

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

        @SuppressWarnings({ "unchecked", "rawtypes" })
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
        			project.setCustomWorkspace(ws);
	        	}
	        	return true;
        	} catch (IOException ioe) {
        		return false;
        	}
        }
    }
}
