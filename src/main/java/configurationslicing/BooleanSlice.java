/**
 *
 */
package configurationslicing;

import configurationslicing.BooleanSlicer.BooleanSlicerSpec;
import hudson.model.Descriptor.FormException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest2;

public class BooleanSlice<I> extends Slice {
    private Map<String, Boolean> nameToValue;
    private BooleanSlicer.BooleanSlicerSpec<I> spec;

    public BooleanSlice(BooleanSlicerSpec<I> spec, List<ItemState> list) {
        this(spec);
        for (ItemState istate : list) {
            add(istate.itemname, istate.checked);
        }
    }

    public BooleanSlice(BooleanSlicerSpec<I> spec) {
        nameToValue = new HashMap<String, Boolean>();
        this.spec = spec;
    }

    public void add(String name, boolean value) {
        nameToValue.put(name, value);
    }

    public boolean exists(String name) {
        return nameToValue.get(name) != null;
    }

    public boolean get(String name) {
        Boolean object = nameToValue.get(name);
        if (object == null) {
            return false;
        }
        return object.booleanValue();
    }

    public BooleanSlicerSpec<I> getSpec() {
        return spec;
    }

    public List<I> getConfiguredItems() {
        List<I> items = new ArrayList<I>();
        List<I> all = spec.getWorkDomain();
        for (I i : all) {
            String name = spec.getName(i);
            if (nameToValue.containsKey(name)) {
                items.add(i);
            }
        }
        return items;
    }

    @Override
    public Slice newInstance(StaplerRequest2 req, JSONObject formData) throws FormException {
        return new BooleanSlice<I>(
                BooleanSlice.this.spec, req.bindJSONToList(ItemState.class, formData.get("itemstate")));
    }

    public static class ItemState {
        private String itemname;
        private boolean checked;

        @DataBoundConstructor
        public ItemState(String itemname, boolean checked) {
            this.itemname = itemname;
            this.checked = checked;
        }
    }
}
