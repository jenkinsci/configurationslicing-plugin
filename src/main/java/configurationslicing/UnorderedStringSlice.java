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
    public UnorderedStringSlice(UnorderedStringSlicerSpec<I> spec, List<ItemState> list) {
        this(spec);
        for(ItemState istate : list) {
            add(istate.itemname, istate.values);
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
        System.out.println(formData);
        return new UnorderedStringSlice<I>(UnorderedStringSlice.this.spec, req.bindJSONToList(ItemState.class, formData.get("itemstate")));
    }
    
    public static class ItemState {
        private String itemname;
        private List<String> values;

        @DataBoundConstructor
        public ItemState(String itemname, List<String> checked) {
            this.itemname=itemname;
            this.values=checked;
        }
    }
}