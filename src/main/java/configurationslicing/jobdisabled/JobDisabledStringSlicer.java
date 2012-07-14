package configurationslicing.jobdisabled;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Hudson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import configurationslicing.UnorderedStringSlicer;

/**
 * @author jacob_robertson
 */
@Extension
public class JobDisabledStringSlicer extends UnorderedStringSlicer<AbstractProject<?,?>>{

    public JobDisabledStringSlicer() {
        super(new JobDisabledStringSliceSpec());
    }

    public static class JobDisabledStringSliceSpec extends UnorderedStringSlicerSpec<AbstractProject<?,?>> {

        public String getDefaultValueString() {
        	return null;
        }
        public String getName() {
            return "Job Disabled Build Slicer (String)";
        }

        public String getName(AbstractProject<?, ?> item) {
            return item.getName();
        }

        public String getUrl() {
            return "jobdisabledstring";
        }
        @Override
        public boolean isBlankNeededForValues() {
        	return false;
        }
        
        @Override
        public List<String> getCommonValueStrings() {
        	List<String> values = new ArrayList<String>();
        	values.add(String.valueOf(true));
        	values.add(String.valueOf(false));
            return values;
        }

		public List<String> getValues(AbstractProject<?, ?> job) {
        	List<String> values = new ArrayList<String>();
        	boolean isDisabled = job.isDisabled();
        	values.add(String.valueOf(isDisabled));
            return values;
        }

        @SuppressWarnings({ "unchecked" })
		public List<AbstractProject<?, ?>> getWorkDomain() {
            return (List) Hudson.getInstance().getItems(AbstractProject.class);
        }

		public boolean setValues(AbstractProject<?, ?> job, List<String> set) {
			String value = set.iterator().next();
			
			boolean oldDisabled = job.isDisabled();
			boolean newDisabled = Boolean.parseBoolean(value);
			
			if (oldDisabled != newDisabled) {
				try {
					job.makeDisabled(newDisabled);
				} catch (IOException e) {
					return false;
				}
				return true;
			} else {
				return false;
			}
        }
    }
}
