/**
 * 
 */
package configurationslicing;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Descriptor.FormException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import configurationslicing.UnorderedStringSlicer.UnorderedStringSlicerSpec;

public class UnorderedStringSlice<I> extends Slice {
    private Map<String, Set<String>> nameToValues;
    private Map<String, Set<String>> valueToNames;
    private UnorderedStringSlicer.UnorderedStringSlicerSpec<I> spec;
    
    // reconstruct our datastructure after the user has made changes
    public UnorderedStringSlice(UnorderedStringSlicerSpec<I> spec, String mapping) {
        this(spec);
        nameToValues = new HashMap<String, Set<String>>();
        String [] lines = mapping.split("\n");
        for(String line : lines) {
            String[] bits = line.split("::", 2);
            if(bits.length < 2) continue;
            String value = bits[0].trim();
            String [] itemNames = bits[1].split(",");
            for(String itemName : itemNames) {
                addLine(nameToValues, itemName.trim(), value.trim());
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
    
    public String getMapping() {
        StringBuffer ret = new StringBuffer();
        for(Map.Entry<String, Set<String>> ent : valueToNames.entrySet()) {
            ret.append(ent.getKey());
            ret.append(" :: ");
            boolean first = true;
            for(String proj : ent.getValue()) {
                if(!first) ret.append(", ");
                ret.append(proj);
                first=false;
            }
            ret.append("\n");
        }
        return ret.toString();
    }

    @Override
    public Slice newInstance(StaplerRequest req, JSONObject formData)
           throws FormException {
        return new UnorderedStringSlice<I>(UnorderedStringSlice.this.spec, formData.getString("mapping"));
    }
}