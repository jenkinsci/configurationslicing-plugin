package configurationslicing.project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import hudson.model.Item;
import jenkins.model.Jenkins;
import hudson.model.AbstractProject;
import hudson.model.FreeStyleProject;
import hudson.model.Hudson;
import configurationslicing.UnorderedStringSlicer;

public abstract class AbstractSimpleProjectSlicer extends UnorderedStringSlicer<AbstractProject> {

    public AbstractSimpleProjectSlicer(AbstractSimpleProjectSliceSpec spec) {
        super(spec);
    }

    public static abstract class AbstractSimpleProjectSliceSpec extends UnorderedStringSlicerSpec<AbstractProject> {

        private static final String DISABLED = "(Disabled)";

        public String getDefaultValueString() {
        	return DISABLED;
        }
        public String getName(AbstractProject item) {
            return item.getFullName();
        }
        public List<String> getValues(AbstractProject item) {
        	List<String> values = new ArrayList<String>();
        	String value = getValue(item);
        	if (value != null) {
        		values.add(value);
        	}
        	if (values.isEmpty()) {
        		values.add(DISABLED);
        	}
            return values;
        }
        protected abstract String getValue(AbstractProject project);

        @SuppressWarnings({ "unchecked" })
		public List<AbstractProject> getWorkDomain() {
            return (List) Jenkins.getInstance().getAllItems(getWorkDomainClass());
        }
        /**
         * Override if needed.
         */
        protected Class<? extends Item> getWorkDomainClass() {
        	return FreeStyleProject.class;
        }

        public boolean setValues(AbstractProject item, List<String> set) {
        	try {
        		String value;
        		if (set.isEmpty()) {
        			value = null;
        		} else {
        			value = set.iterator().next();
        		}
        		if (DISABLED.equals(value)) {
        			value = null;
        		}
        		String old = getValue(item);
        		// check for equal - we don't want to trigger a change for no reason
        		if (!StringUtils.equals(value, old)) {
        			setValue(item, value);
        		}
	        	return true;
        	} catch (IOException ioe) {
        		return false;
        	}
        }
        /**
         * @throws IOException for the save operation
         */
        protected abstract void setValue(AbstractProject project, String value) throws IOException;
    }

}
