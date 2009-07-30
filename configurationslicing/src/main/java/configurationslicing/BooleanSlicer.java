package configurationslicing;


import java.util.List;



public class BooleanSlicer<I> implements Slicer<BooleanSlice<I>, I>{
    public static interface BooleanSlicerSpec<I> {
        public abstract String getName();
        public abstract String getUrl();
        public abstract List<I> getWorkDomain();
        public abstract boolean getValue(I item);
        public abstract String getName(I item);
        public abstract boolean setValue(I item, boolean value);
    }
    
    private BooleanSlicerSpec<I> spec;
    
    public BooleanSlicer(BooleanSlicerSpec<I> spec) {
        this.spec=spec;
    }
    public BooleanSlice<I> getInitialAccumulator() {
        return new BooleanSlice<I>(spec);
    }

    public BooleanSlice<I> accumulate(BooleanSlice<I> t, I i) {
        t.add(spec.getName(i), spec.getValue(i));
        return t;
    }

    public boolean transform(BooleanSlice<I> t, I i) {
        return spec.setValue(i, t.get(spec.getName(i)));
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
}
