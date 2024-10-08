package configurationslicing.tools;

import configurationslicing.Slicer;
import configurationslicing.SlicerLoader;
import configurationslicing.UnorderedStringSlice;
import hudson.Extension;
import hudson.model.AbstractProject;

@Extension
public class GradleSlicerWrapper extends SlicerLoader<UnorderedStringSlice<AbstractProject>, AbstractProject> {
    protected Slicer<UnorderedStringSlice<AbstractProject>, AbstractProject> buildDelegateOnConstruction()
            throws Throwable {
        return new GradleSlicer();
    }
}
