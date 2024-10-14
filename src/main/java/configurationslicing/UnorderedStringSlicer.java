package configurationslicing;

import java.util.ArrayList;
import java.util.List;

public class UnorderedStringSlicer<I> implements Slicer<UnorderedStringSlice<I>, I> {
    public abstract static class UnorderedStringSlicerSpec<I> {
        public abstract String getName();

        public abstract String getUrl();

        public abstract List<I> getWorkDomain();

        public abstract List<String> getValues(I item);

        public abstract String getName(I item);

        public abstract boolean setValues(I item, List<String> set);

        public abstract String getDefaultValueString();
        /**
         * Useful when there are common configurations we want to always be available.
         */
        public List<String> getCommonValueStrings() {
            return null;
        }

        public String getConfiguredValueDescription() {
            return Messages.configurationSlicing_configuredValueDescription();
        }
        /**
         * Allows you to use "MyJob[0]" to indicate separate values
         */
        public boolean isIndexUsed(int count) {
            return false;
        }

        public String getValueIndex(I item, int index) {
            return String.valueOf(index);
        }

        public int getValueIndex(I item, String indexName) {
            return Integer.parseInt(indexName);
        }

        public boolean isBlankNeededForValues() {
            return true;
        }

        /**
         * Some cases we do not want to trim the value.  For example, in Parameters this causes issues.
         * This just requires the user to be more conscientious when configuring parameters.
         * @return true always
         */
        public boolean isValueTrimmed() {
            return true;
        }
    }

    private UnorderedStringSlicerSpec<I> spec;

    public UnorderedStringSlicer(UnorderedStringSlicerSpec<I> spec) {
        this.spec = spec;
    }

    public UnorderedStringSlice<I> getInitialAccumulator() {
        return new UnorderedStringSlice<I>(spec);
    }

    public UnorderedStringSlicerSpec<I> getSpec() {
        return spec;
    }
    /**
     * Override this with different behavior if needed.
     */
    public boolean isLoaded() {
        try {
            loadPluginDependencyClass();
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * Override this with the right class if you want to use the default isLoaded behavior.
     */
    public void loadPluginDependencyClass() {}

    public UnorderedStringSlice<I> accumulate(UnorderedStringSlice<I> t, I item) {
        String name = spec.getName(item);
        List<String> values = spec.getValues(item);
        if (spec.isIndexUsed(values.size())) {
            for (int i = 0; i < values.size(); i++) {
                List<String> oneValueList = new ArrayList<String>();
                oneValueList.add(values.get(i));
                String valueIndex = spec.getValueIndex(item, i);
                String oneName = name + "[" + valueIndex + "]";
                t.add(oneName, oneValueList);
            }
        } else {
            t.add(spec.getName(item), values);
        }
        return t;
    }

    public boolean transform(UnorderedStringSlice<I> t, I i) {
        List<String> set = t.get(spec.getName(i));
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
        return getName().compareToIgnoreCase(o.getName());
    }
}
