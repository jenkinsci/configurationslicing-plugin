package configurationslicing;

import java.util.List;
import java.util.Set;

public class UnorderedStringSlicer<I> implements Slicer<UnorderedStringSlice<I>, I>{
    public static abstract class UnorderedStringSlicerSpec<I> {
        public abstract String getName();
        public abstract String getUrl();
        public abstract List<I> getWorkDomain();
        public abstract List<String> getValues(I item);
        public abstract String getName(I item);
        public abstract boolean setValues(I item, Set<String> set);
        public abstract String getDefaultValueString();
        /**
         * Useful when there are common configurations we want to always be available.
         */
        public List<String> getCommonValueStrings() {
        	return null;
        }
    }

    private UnorderedStringSlicerSpec<I> spec;

    public UnorderedStringSlicer(UnorderedStringSlicerSpec<I> spec) {
        this.spec=spec;
    }
    public UnorderedStringSlice<I> getInitialAccumulator() {
        return new UnorderedStringSlice<I>(spec);
    }
    public boolean isLoaded() {
    	return true;
    }

    public UnorderedStringSlice<I> accumulate(UnorderedStringSlice<I> t, I i) {
        t.add(spec.getName(i), spec.getValues(i));
        return t;
    }

    public boolean transform(UnorderedStringSlice<I> t, I i) {
    	Set<String> set = t.get(spec.getName(i));
    	if (set == null) {
    		return false;
    	} else {
    		return spec.setValues(i, set);
    	}
    }

    public String getName() {
        return spec.getName();
    }

    public String getUrl() {
        return spec.getUrl();
    }

    public List<I> getWorkDomain() {
        return spec.getWorkDomain();
    }

    public int compareTo(Slicer<UnorderedStringSlice<I>, I> o) {
    	return getName().compareTo(o.getName());
    }
}
