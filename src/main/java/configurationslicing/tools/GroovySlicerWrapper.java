package configurationslicing.tools;

import hudson.Extension;
import hudson.model.AbstractProject;
import configurationslicing.Slicer;
import configurationslicing.SlicerLoader;
import configurationslicing.UnorderedStringSlice;

@Extension
public class GroovySlicerWrapper extends SlicerLoader<UnorderedStringSlice<AbstractProject>, AbstractProject> {
	protected Slicer<UnorderedStringSlice<AbstractProject>, AbstractProject> buildDelegateOnConstruction() throws Throwable {
		return new GroovySlicer();
	}
}