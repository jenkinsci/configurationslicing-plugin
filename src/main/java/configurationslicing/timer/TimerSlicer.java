package configurationslicing.timer;

import java.io.IOException;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import antlr.ANTLRException;

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
        try {
            return t.transform((TimerTrigger)i.getTrigger(TimerTrigger.class), i);
        } catch (ANTLRException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public String getName() {
        return "Timer Trigger Slicer";
    }

    public String getUrl() {
        return "timertrigger";
    }
}
