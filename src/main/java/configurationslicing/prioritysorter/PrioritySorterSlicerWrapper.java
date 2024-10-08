package configurationslicing.prioritysorter;

import configurationslicing.Slicer;
import configurationslicing.SlicerLoader;
import configurationslicing.UnorderedStringSlice;
import hudson.Extension;
import hudson.model.Job;

@Extension
public class PrioritySorterSlicerWrapper extends SlicerLoader<UnorderedStringSlice<Job<?, ?>>, Job<?, ?>> {
    protected Slicer<UnorderedStringSlice<Job<?, ?>>, Job<?, ?>> buildDelegateOnConstruction() throws Throwable {
        return new PrioritySorterSlicer();
    }
}
