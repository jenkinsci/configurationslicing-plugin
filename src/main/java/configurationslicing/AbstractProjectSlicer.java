package configurationslicing;

import java.util.List;

import hudson.Extension;
import hudson.ExtensionPoint;
import hudson.model.AbstractProject;
import hudson.model.Describable;
import hudson.model.Hudson;

public abstract class AbstractProjectSlicer<T> 
implements Slicer<T, AbstractProject>, ExtensionPoint{
    public final List<AbstractProject> getWorkDomain() {
        return Hudson.getInstance().getItems(AbstractProject.class);
    }
}
