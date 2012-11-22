package configurationslicing;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import hudson.model.Descriptor.FormException;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

public class ParametersStringSlice<I> extends UnorderedStringSlice<I> {

	static Logger LOG = Logger.getLogger("ParametersStringSlicing");
	
	public ParametersStringSlice(ParametersStringSliceSpec<I> spec) {
		super(spec);
	}
	public ParametersStringSliceSpec<I> getParametersSliceSpec() {
		return (ParametersStringSliceSpec<I>) getSpec();
	}
	public UnorderedStringSlice<I> getInitialAccumulator() {
		return new ParametersStringSlice<I>(getParametersSliceSpec());
	}
	@Override
    public Slice newInstance(StaplerRequest req, JSONObject formData) throws FormException {
		/*
{"itemNames":
["MD\n","Secure-A-1\nSecure-A-2\nSecure-B-1\nSecure-B-2\n",""],
"paramValue":["3","false","(Disabled)","(Disabled)","",""]
}		 
		 */
		LOG.warning("formData." + formData);
		List<String> paramValues = getStringList(formData, "paramValue");
		List<String> joinedValues = new ArrayList<String>();
		int paramNamesCount = getParametersSliceSpec().getParamNamesCount();
		for (int i = 0; i < paramValues.size(); i += paramNamesCount) {
			StringBuilder buf = new StringBuilder();
			for (int j = 0; j < paramNamesCount; j++) {
				if (j > 0) {
					buf.append(ParametersStringSliceSpec.DELIM);
				}
				String value = paramValues.get(i + j);
				buf.append(value);
			}
			joinedValues.add(buf.toString());
		}
		
        return new UnorderedStringSlice<I>(getSpec(), 
        		joinedValues,
        		getStringList(formData, "itemNames")
        		);
    }
	
}
