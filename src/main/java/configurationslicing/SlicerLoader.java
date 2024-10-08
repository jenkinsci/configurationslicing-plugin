package configurationslicing;

import java.util.List;

/**
 * Handles the problem with Slicers that have class loading issues.
 * @author jacob
 */
public abstract class SlicerLoader<T, I> implements Slicer<T, I> {

    protected abstract Slicer<T, I> buildDelegateOnConstruction() throws Throwable;

    private Slicer<T, I> delegate;

    public SlicerLoader() {
        try {
            delegate = buildDelegateOnConstruction();
        } catch (Throwable t) {
            delegate = null;
        }
    }

    public boolean isLoaded() {
        return delegate != null && delegate.isLoaded();
    }

    public Slicer<T, I> getDelegate() {
        return delegate;
    }

    public int compareTo(Slicer<UnorderedStringSlice<I>, I> o) {
        return 0;
    }

    public String getName() {
        return null;
    }

    public String getUrl() {
        return null;
    }

    public List<I> getWorkDomain() {
        return null;
    }

    public T getInitialAccumulator() {
        return null;
    }

    public UnorderedStringSlice<I> accumulate(UnorderedStringSlice<I> t, I i) {
        return null;
    }

    public boolean transform(UnorderedStringSlice<I> t, I i) {
        return false;
    }
}
