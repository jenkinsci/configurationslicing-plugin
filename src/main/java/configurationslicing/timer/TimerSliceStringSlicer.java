package configurationslicing.timer;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Job;
import hudson.triggers.TimerTrigger;
import hudson.triggers.Trigger;
import configurationslicing.UnorderedStringSlicer;

@Extension
public class TimerSliceStringSlicer extends UnorderedStringSlicer<Job>{

    public TimerSliceStringSlicer() {
        super(new TimerSliceSpec());
    }
    public static class TimerSliceSpec extends AbstractTimerSliceSpec {

    	public TimerSliceSpec() {
			super(TimerTrigger.class);
		}
    	
        public String getName() {
            return "Timer Trigger Slicer";
        }

        public String getUrl() {
            return "timerslicestring";
        }

        @SuppressWarnings("unchecked")
		public Trigger newTrigger(String spec, Trigger oldTrigger) {
            return new TimerTrigger(spec);
        }
        
    }
}
