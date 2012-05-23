package configurationslicing.parameters;

import hudson.Extension;
import hudson.model.ParameterValue;
import hudson.model.BooleanParameterDefinition;
import hudson.model.BooleanParameterValue;
import hudson.model.ChoiceParameterDefinition;
import hudson.model.Hudson;
import hudson.model.Job;
import hudson.model.ParameterDefinition;
import hudson.model.ParametersDefinitionProperty;
import hudson.model.StringParameterDefinition;
import hudson.model.StringParameterValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import configurationslicing.UnorderedStringSlicer;

/**
 * @author Jacob Robertson
 * TODO add Text parameter, validating string, etc - any of the parameters!
 */
@SuppressWarnings("unchecked")
@Extension
public class ParametersSlicer extends UnorderedStringSlicer<Job> {

    public ParametersSlicer() {
        super(new ParametersSliceSpec());
    }

    public static class ParametersSliceSpec extends UnorderedStringSlicerSpec<Job> {

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

        public String getName(Job item) {
            return item.getName();
        }
        @Override
        public boolean isIndexUsed(int count) {
        	return true;
        }
        @Override
        public String getValueIndex(Job item, int index) {
        	List<ParameterItem> pitems = getParameterItems(item);
        	return pitems.get(index).name;
        }
        @Override
        public int getValueIndex(Job item, String indexName) {
        	List<ParameterItem> pitems = getParameterItems(item);
        	for (ParameterItem pitem: pitems) {
        		if (pitem.name.equals(indexName)) {
        			return pitem.index;
        		}
        	}
        	// this will happen if the user sets a param name that is not valid
        	throw new IllegalArgumentException(indexName);
        }
        
        public List<String> getValues(Job item) {
        	List<String> values = new ArrayList<String>();
        	List<ParameterItem> pitems = getParameterItems(item);
        	for (ParameterItem pitem: pitems) {
        		values.add(pitem.value);
    		}
            return values;
        }
        public boolean setValues(Job item, List<String> list) {
            ParametersDefinitionProperty prop = (ParametersDefinitionProperty) item.getProperty(ParametersDefinitionProperty.class);
            List<ParameterDefinition> defs = prop.getParameterDefinitions();
    		if (prop != null) {
            	List<ParameterItem> pitems = getParameterItems(item);
    			boolean changes = false;
    			for (int i = 0; i < list.size(); i++) {
    				ParameterItem pitem = pitems.get(i);
    				String newValue = list.get(i);
					String oldValue = pitem.value;
					if (!newValue.equals(oldValue)) {
						replace(pitem, newValue, defs);
						changes = true;
					}
				}
    			if (changes) {
    				try {
						item.save();
					} catch (IOException e) {
						e.printStackTrace();
					}
    			}
    		}
        	return true;
        }
        private void replace(ParameterItem item, String newValue, List<ParameterDefinition> defs) {
        	for (int i = 0; i < defs.size(); i++) {
        		ParameterDefinition def = defs.get(i);
        		if (def.getName().equals(item.name)) {
        			ParameterDefinition newDef = newParameterDefinition(newValue, def);
        			defs.set(i, newDef);
        		}
			}
        }
        private ParameterDefinition newParameterDefinition(String newValue, ParameterDefinition old) {
        	if (old instanceof StringParameterDefinition) {
        		return new StringParameterDefinition(old.getName(), newValue, old.getDescription());
        	} else if (old instanceof BooleanParameterDefinition) {
        		return new BooleanParameterDefinition(old.getName(), Boolean.parseBoolean(newValue), old.getDescription());
        	} else if (old instanceof ChoiceParameterDefinition) {
        		return new ChoiceParameterDefinition(old.getName(), newValue, old.getDescription());
        	}
        	return null;
        }
        private List<ParameterItem> getParameterItems(Job item) {
            List<ParameterItem> items = new ArrayList<ParameterItem>();
            ParametersDefinitionProperty prop = (ParametersDefinitionProperty) item.getProperty(ParametersDefinitionProperty.class);
    		if (prop != null) {
    			int count = 0;
        		for (ParameterDefinition def: prop.getParameterDefinitions()) {
        			if (isSliceableProperty(def)) {
        				ParameterValue value = def.getDefaultParameterValue();
        				String stringValue = toStringValue(value, def);
        				ParameterItem pitem = new ParameterItem();
        				pitem.index = count++;
        				pitem.value = stringValue;
        				pitem.name = value.getName();
        				items.add(pitem);
        			}
        		}
    		}
            return items;
        }
        private static class ParameterItem {
        	String name;
        	String value;
        	int index;
        }
        public List<Job> getWorkDomain() {
        	List<Job> jobs = Hudson.getInstance().getItems(Job.class);
        	return jobs;
        }
        
        public String toStringValue(ParameterValue value, ParameterDefinition def) {
        	if (value instanceof BooleanParameterValue) {
        		return String.valueOf(((BooleanParameterValue) value).value);
        	} else if (def instanceof ChoiceParameterDefinition) {
        		ChoiceParameterDefinition cdef = (ChoiceParameterDefinition) def;
        		List<String> choices = cdef.getChoices();
        		StringBuilder buf = new StringBuilder();
        		for (String choice: choices) {
        			if (buf.length() > 0) {
        				buf.append("\n");
        			}
        			buf.append(choice);
        		}
        		return buf.toString();
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
        
    }

}

