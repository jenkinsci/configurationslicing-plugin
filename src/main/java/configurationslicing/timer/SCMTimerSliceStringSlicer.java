package configurationslicing.timer;

import configurationslicing.UnorderedStringSlicer;
import hudson.Extension;
import hudson.model.Job;
import hudson.triggers.SCMTrigger;
import hudson.triggers.Trigger;

@Extension
public class SCMTimerSliceStringSlicer extends UnorderedStringSlicer<Job> {

    public SCMTimerSliceStringSlicer() {
        super(new SCMTimerSliceSpec());
    }

    public static class SCMTimerSliceSpec extends AbstractTimerSliceSpec {

        public SCMTimerSliceSpec() {
            super(SCMTrigger.class);
        }

        public String getName() {
            return "SCM Timer Trigger Slicer";
        }

        public String getUrl() {
            return "scmtimerslicestring";
        }

        @SuppressWarnings("unchecked")
        public Trigger newTrigger(String spec, Trigger oldTrigger) {
            boolean ignorePostCommitHooks = false;
            if (oldTrigger instanceof SCMTrigger) {
                ignorePostCommitHooks = ((SCMTrigger) oldTrigger).isIgnorePostCommitHooks();
            }
            return new SCMTrigger(spec, ignorePostCommitHooks);
        }
    }
}
