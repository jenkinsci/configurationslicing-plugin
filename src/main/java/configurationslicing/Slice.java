package configurationslicing;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

import hudson.ExtensionPoint;
import hudson.maven.MavenReporterDescriptor;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.model.Descriptor.FormException;

public abstract class Slice{
    public abstract Slice newInstance(StaplerRequest req, JSONObject formData)
            throws FormException ;
}
