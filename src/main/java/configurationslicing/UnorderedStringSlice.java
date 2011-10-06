/**
 * 
 */
package configurationslicing;

import hudson.model.Descriptor.FormException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

import configurationslicing.UnorderedStringSlicer.UnorderedStringSlicerSpec;

public class UnorderedStringSlice<I> extends Slice {
    private Map<String, Set<String>> nameToValues;
    private Map<String, Set<String>> valueToNames;
    private UnorderedStringSlicer.UnorderedStringSlicerSpec<I> spec;
    
    // reconstruct our datastructure after the user has made changes
    public UnorderedStringSlice(UnorderedStringSlicerSpec<I> spec, List<String> configurationValues, List<String> itemNames) {
        this(spec);
        nameToValues = new HashMap<String, Set<String>>();
        for (int i = 0; i < configurationValues.size(); i++) {
        	String value = configurationValues.get(i);
        	String namesString = itemNames.get(i);
        	String[] namesSplit = namesString.split("\\n");
            for(String itemName : namesSplit) {
            	itemName = itemName.trim();
            	if (itemName.length() > 0) {
            		addLine(nameToValues, itemName, value.trim());
            	}
            }
        }
    }
    
    public UnorderedStringSlice(UnorderedStringSlicerSpec<I> spec) {
        valueToNames=new HashMap<String, Set<String>>();
        this.spec=spec;
    }
    
    public void add(String name, Collection<String> values) {
        for(String value : values) {
            addLine(valueToNames, value, name);
        }
    }

    private static void addLine(Map<String, Set<String>> map, String s, String name) {
        if(!map.containsKey(s)) {
            map.put(s, new HashSet<String>());
        }
        Set<String> list= map.get(s);
        list.add(name);
    }

    public Set<String> get(String name) {
        return nameToValues.get(name);
    }
    
    public UnorderedStringSlicerSpec<I> getSpec() {
        return spec;
    }
    public List<String> getConfiguredValues() {
    	String defaultValueString = spec.getDefaultValueString();
    	List<String> list = new ArrayList<String>(valueToNames.keySet());
    	if (list.contains(defaultValueString)) {
    		list.remove(defaultValueString);
    	}
    	Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
    	
    	// add any common values
    	List<String> commonValues = spec.getCommonValueStrings();
    	if (commonValues != null) {
    		for (String commonValue: commonValues) {
    			if (!list.contains(commonValue)) {
    				list.add(commonValue);
    			}
    		}
    	}
    	
    	// add the default as second to last
    	if (defaultValueString != null) {
    		list.add(defaultValueString);
    	}
    	// we need this so you can add new items
    	if (!list.contains("")) {
    		list.add("");
    	}
    	return list;
    }
    public String getItemNamesString(String configurationString) {
    	List<String> list = getItemNames(configurationString);
    	StringBuilder buf = new StringBuilder();
    	for (String job: list) {
    		buf.append(job);
    		// add it afterwards so we have an extra linefeed in the gui to make cut and paste easier
			buf.append("\n");
    	}
    	return buf.toString();
    }
    public List<String> getItemNames(String configurationString) {
    	Set<String> set = valueToNames.get(configurationString);
    	if (set == null) {
        	// particularly applies to the empty option
    		return new ArrayList<String>();
    	}
    	List<String> list = new ArrayList<String>(set);
    	Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
    	return list;
    }

    @Override
    public Slice newInstance(StaplerRequest req, JSONObject formData)
           throws FormException {
        return new UnorderedStringSlice<I>(UnorderedStringSlice.this.spec, 
        		getStringList(formData, "configValue"),
        		getStringList(formData, "itemNames")
        		);
    }
    private List<String> getStringList(JSONObject formData, String key) {
    	JSONArray array = formData.getJSONArray(key);
    	List<String> list = new ArrayList<String>();
    	for (Object o: array) {
    		list.add((String) o);
    	}
    	return list;
    }
}