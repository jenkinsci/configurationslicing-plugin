package configurationslicing.tools;

import configurationslicing.UnorderedStringSlicer;
import hudson.model.AbstractProject;
import hudson.tasks.Builder;

/**
 * @author Maarten Dirkse
 */
public abstract class AbstractToolSlicer extends UnorderedStringSlicer<AbstractProject> {

    public AbstractToolSlicer(UnorderedStringSlicerSpec<AbstractProject> spec) {
        super(spec);
    }

    @Override
    public boolean isLoaded() {
        // Attempt to load a class from the plugin
        try {
            getPluginClass();
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    /**
     * Method meant to let extending classes load a class that might not be present (because it's in a
     * plugin) in order to either succeed or force an error.
     *
     * @return a Builder class that is only found in the plugin
     */
    protected abstract Class<? extends Builder> getPluginClass();
}
