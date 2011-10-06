package configurationslicing;

import java.util.List;

import hudson.ExtensionPoint;

public interface Slicer<T, I> extends ExtensionPoint, Comparable<Slicer<T, I>> {
    public String getName();
    public String getUrl();
    public List<I> getWorkDomain();
    public T getInitialAccumulator();
    public T accumulate(T t, I i);
    public boolean transform(T t, I i);
    
    /**
     * This method makes it easy to provide "optional" slicers that will only show up
     * if that plugin is installed.
     */
    boolean isLoaded();
}
