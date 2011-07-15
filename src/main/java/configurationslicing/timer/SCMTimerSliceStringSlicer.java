package configurationslicing.timer;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.triggers.SCMTrigger;
import hudson.triggers.Trigger;
import antlr.ANTLRException;
import configurationslicing.UnorderedStringSlicer;

@Extension
public class SCMTimerSliceStringSlicer extends UnorderedStringSlicer<AbstractProject<?,?>>{

    public SCMTimerSliceStringSlicer() {
        super(new SCMTimerSliceSpec());
    }
    public static class SCMTimerSliceSpec extends AbstractTimerSliceSpec {

        public String getName() {
            return "SCM Timer Trigger Slicer";
        }

        public String getUrl() {
            return "scmtimerslicestring";
        }
        
        @SuppressWarnings("unchecked")
		protected Trigger newTrigger(String spec) throws ANTLRException {
        	return new SCMTrigger(spec);
        }
    }
}