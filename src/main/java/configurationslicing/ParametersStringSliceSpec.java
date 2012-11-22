package configurationslicing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import configurationslicing.UnorderedStringSlicer.UnorderedStringSlicerSpec;

public abstract class ParametersStringSliceSpec<I> extends UnorderedStringSlicerSpec<I> {
	
	static Logger LOG = Logger.getLogger("ParametersStringSlicing");
	
	static final String DELIM = "@@//@@";
	
	abstract public List<String> getParamNames();
	
	public final boolean setValues(I item, List<String> set) {
		String value = set.get(0);
		String[] split = split(value);
		List<String> splitList = Arrays.asList(split);
		return doSetValues(item, splitList);
	}
	public abstract boolean doSetValues(I item, List<String> set);
	
	public final List<String> getValues(I item) {
		List<String> values = doGetValues(item);
		String joinString = StringUtils.join(values, DELIM);
		List<String> joined = new ArrayList<String>();
		joined.add(joinString);
		return joined;
	}
	
	public final String getDefaultValueString() {
		String value = doGetDefaultValueString();
		int count = getParamNamesCount();
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < count; i++) {
			if (i > 0) {
				buf.append(DELIM);
			}
			buf.append(value);
		}
		return buf.toString();
	}
	public abstract String doGetDefaultValueString();
	
	public abstract List<String> doGetValues(I item);
	
	public String getParamValue(String paramName, String configuredValue) {
		LOG.warning("paramName." + paramName);
		LOG.warning("configuredValue." + configuredValue);
		List<String> names = getParamNames();
		String[] values = split(configuredValue);
		for (int i = 0; i < names.size(); i++) {
			if (names.get(i).equals(paramName)) {
				if (i >= values.length) {
					LOG.warning("return blank");
					return "";
				}
				LOG.warning("return." + values[i]);
				return values[i];
			}
		}
		return null;
	}
	private String[] split(String configuredValue) {
		return configuredValue.split(DELIM);
	}
	public int getParamNamesCount() {
		return getParamNames().size();
	}
}
