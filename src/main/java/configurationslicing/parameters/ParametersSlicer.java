package configurationslicing.parameters;

import hudson.Extension;
import hudson.model.BooleanParameterDefinition;
import hudson.model.BooleanParameterValue;
import hudson.model.ChoiceParameterDefinition;
import hudson.model.Hudson;
import hudson.model.Job;
import hudson.model.ParameterDefinition;
import hudson.model.ParameterValue;
import hudson.model.ParametersDefinitionProperty;
import hudson.model.StringParameterDefinition;
import hudson.model.StringParameterValue;

import java.util.ArrayList;
import java.util.List;

import configurationslicing.UnorderedStringSlicer;

/**
 * @author Jacob Robertson
 * TODO add Text parameter, validating string, etc - any of the parameters!
 */
@SuppressWarnings("unchecked")
@Extension
public class ParametersSlicer extends UnorderedStringSlicer<JobParameter> {

    public ParametersSlicer() {
        super(new ParametersSliceSpec());
    }

    public static class ParametersSliceSpec extends UnorderedStringSlicerSpec<JobParameter> {

        public String getName() {
            return "Parameters";
        }

        public String getUrl() {
            return "parameters";
        }

    	/**
    	 * There is no concept of default value for this slicer.
    	 */
        public String getDefaultValueString() {
            return "";
        }

        public String getName(JobParameter item) {
            return item.getJob().getName();
        }

        @Override
        public boolean isMultipleItemsAllowed() {
        	return true;
        }
        
        public List<String> getValues(JobParameter item) {
            List<String> content = new ArrayList<String>();
            content.add(item.getParameterValue());
            return content;
        }

        public List<JobParameter> getWorkDomain() {
        	List<Job> jobs = Hudson.getInstance().getItems(Job.class);
        	List<JobParameter> params = new ArrayList<JobParameter>();
        	for (Job job: jobs) {
        		ParametersDefinitionProperty prop = (ParametersDefinitionProperty) job.getProperty(ParametersDefinitionProperty.class);
        		if (prop != null) {
	        		for (ParameterDefinition def: prop.getParameterDefinitions()) {
	        			if (isSliceableProperty(def)) {
	        				ParameterValue value = def.getDefaultParameterValue();
	        				String stringValue = toStringValue(value);
	        				JobParameter param = new JobParameter(job, def.getName(), stringValue);
	        				params.add(param);
	        			}
	        		}
        		}
        	}
        	return params;
        }
        
        public String toStringValue(ParameterValue value) {
        	if (value instanceof BooleanParameterValue) {
        		return String.valueOf(((BooleanParameterValue) value).value);
        	} else if (value instanceof StringParameterValue) {
        		return ((StringParameterValue) value).value;
        	} else {
        		throw new IllegalArgumentException("Don't know how to convert " + value); 
        	}
        }
        
        public boolean isSliceableProperty(ParameterDefinition def) {
        	return (def instanceof BooleanParameterDefinition
        			|| def instanceof StringParameterDefinition
        			|| def instanceof ChoiceParameterDefinition
        			);
        }
        
        public boolean setValues(JobParameter item, List<String> list) {
        	return true;
        }
    }
    protected String getValueIndex(JobParameter item, int index) {
    	return item.getParameterName();
    }

}

