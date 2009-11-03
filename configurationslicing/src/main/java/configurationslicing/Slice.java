package configurationslicing;

import hudson.model.Descriptor.FormException;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

public abstract class Slice{
    public abstract Slice newInstance(StaplerRequest req, JSONObject formData)
            throws FormException ;
}
