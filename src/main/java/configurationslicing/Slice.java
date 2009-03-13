package configurationslicing;

import hudson.ExtensionPoint;
import hudson.maven.MavenReporterDescriptor;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Hudson;

public abstract class Slice implements Describable<Slice> {
    public Descriptor<Slice> getDescriptor() {
        return (Descriptor<Slice>)Hudson.getInstance().getDescriptor(getClass());
    }
}
