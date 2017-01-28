package configurationslicing.project;

import hudson.Extension;
import hudson.model.AbstractProject;

import java.io.IOException;

@Extension
public class QuietPeriodSlicer extends AbstractSimpleProjectSlicer {

    public QuietPeriodSlicer() {
        super(new QuietPeriodSliceSpec());
    }

    public static class QuietPeriodSliceSpec extends AbstractSimpleProjectSlicer.AbstractSimpleProjectSliceSpec {

        public String getName() {
            return "Quiet Period Slicer";
        }
        public String getUrl() {
            return "quietperiod";
        }
        @Override
        protected String getValue(AbstractProject project) {
        	int q = project.getQuietPeriod();
        	return String.valueOf(q);
        }
        @Override
        protected void setValue(AbstractProject project, String value) throws IOException {
        	Integer q;
        	try {
        		q = Integer.parseInt(value);
        	} catch (NumberFormatException e) {
        		q = null;
        	}
       		project.setQuietPeriod(q);
        }
    }

}
