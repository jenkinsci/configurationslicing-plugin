package configurationslicing;

public class ParametersStringSlicer<I> extends UnorderedStringSlicer<I> {

    public ParametersStringSlicer(UnorderedStringSlicerSpec<I> spec) {
        super(spec);
    }

    @Override
    public UnorderedStringSlice<I> getInitialAccumulator() {
        return new ParametersStringSlice<I>((ParametersStringSliceSpec<I>) getSpec());
    }
}
