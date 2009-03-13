package configurationslicing.timer;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import configurationslicing.AbstractProjectSlicer;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.Descriptor.FormException;
import hudson.tasks.ArtifactArchiver;
import hudson.triggers.TimerTrigger;

@Extension
public class TimerSlicer extends AbstractProjectSlicer<TimerSlice> {
    public TimerSlice getInitialAccumulator() {
        return new TimerSlice();
    }

    public TimerSlice accumulate(TimerSlice t, AbstractProject i) {
        t.add((TimerTrigger)i.getTrigger(TimerTrigger.class), i);
        return t;
    }

    public boolean transform(TimerSlice t, AbstractProject i) {
        return t.transform(i);
    }

    public String getName() {
        return "Timer Trigger Slicer";
    }

    public String getUrl() {
        return "timertrigger";
    }
}
