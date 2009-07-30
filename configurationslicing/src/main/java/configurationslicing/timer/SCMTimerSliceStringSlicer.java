package configurationslicing.timer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import antlr.ANTLRException;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Hudson;
import hudson.triggers.SCMTrigger;
import configurationslicing.UnorderedStringSlicer;
import configurationslicing.UnorderedStringSlicer.UnorderedStringSlicerSpec;

@Extension
public class SCMTimerSliceStringSlicer extends UnorderedStringSlicer<AbstractProject<?,?>>{

    public SCMTimerSliceStringSlicer() {
        super(new SCMTimerSliceSpec());
    }
    public static class SCMTimerSliceSpec implements UnorderedStringSlicerSpec<AbstractProject<?,?>> {

        private static final String DISABLED = "(Disabled)";

        public String getName() {
            return "SCM Timer Trigger Slicer";
        }

        public String getName(AbstractProject<?, ?> item) {
            return item.getName();
        }

        public String getUrl() {
            return "scmtimerslicestring";
        }

        public List<String> getValues(AbstractProject<?, ?> item) {
            SCMTrigger trigger = item.getTrigger(SCMTrigger.class);
            String[] cronspec = null;
            if(trigger != null) {
                cronspec = trigger.getSpec().split("\n");
            }
            if(cronspec == null || cronspec.length == 0) {
                cronspec = new String[] {DISABLED};
            }
            return Arrays.asList(cronspec);
        }

        public List<AbstractProject<?, ?>> getWorkDomain() {
            return (List)Hudson.getInstance().getItems(AbstractProject.class);
        }

        public boolean setValues(AbstractProject<?, ?> item, Set<String> set) {
            if(set.isEmpty()) return false;

            SCMTrigger trigger = item.getTrigger(SCMTrigger.class);
            boolean disabled = false;
            StringBuilder triggerspec=new StringBuilder();
            for(String line : set) {
                if(line.equals(DISABLED)) {
                    disabled = true;
                } else {
                    triggerspec.append(line);
                }
            }
            // now do the transformation
            try {
                SCMTrigger newtrigger = null;
                if(!disabled) {
                    newtrigger = new SCMTrigger(triggerspec.toString());
                }
                if(trigger != null) {
                    item.removeTrigger(trigger.getDescriptor());
                }
                if(newtrigger != null) {
                    item.addTrigger(newtrigger);
                }
                return true;
            } catch (ANTLRException e) {
                // need to log this
                return false;
            } catch (IOException e) {
                // need to log this
                return false;
            }
        }
    }
}
