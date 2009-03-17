/**
 * 
 */
package configurationslicing;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Descriptor.FormException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import configurationslicing.BooleanSlicer.BooleanSlicerSpec;

public class BooleanSlice<I> extends Slice {
    private Map<String, Boolean> nameToValue;
    private BooleanSlicer.BooleanSlicerSpec<I> spec;
    
    public BooleanSlice(BooleanSlicerSpec<I> spec, List<ItemState> list) {
        this(spec);
        for(ItemState istate : list) {
            add(istate.itemname, istate.checked);
        }
    }
    public BooleanSlice(BooleanSlicerSpec<I> spec) {
        nameToValue=new HashMap<String, Boolean>();
        this.spec=spec;
    }
    public void add(String name, boolean value) {
        nameToValue.put(name, value);
    }

    public boolean get(String name) {
        return nameToValue.get(name);
    }
    
    public BooleanSlicerSpec<I> getSpec() {
        return spec;
    }
    
    @Override
    public Slice newInstance(StaplerRequest req, JSONObject formData)
           throws FormException {
        System.out.println(formData);
        return new BooleanSlice<I>(BooleanSlice.this.spec, req.bindJSONToList(ItemState.class, formData.get("itemstate")));
    }
    
    public static class ItemState {
        private String itemname;
        private boolean checked;

        @DataBoundConstructor
        public ItemState(String itemname, boolean checked) {
            this.itemname=itemname;
            this.checked=checked;
        }
    }
}