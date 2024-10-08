package configurationslicing.timer;

import configurationslicing.UnorderedStringSlicer;
import hudson.Extension;
import hudson.model.Job;
import hudson.triggers.TimerTrigger;
import hudson.triggers.Trigger;

@Extension
public class TimerSliceStringSlicer extends UnorderedStringSlicer<Job> {

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
