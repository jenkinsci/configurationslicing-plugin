package configurationslicing;

import hudson.model.Descriptor.FormException;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest2;

public abstract class Slice {
    public abstract Slice newInstance(StaplerRequest2 req, JSONObject formData) throws FormException;
}
