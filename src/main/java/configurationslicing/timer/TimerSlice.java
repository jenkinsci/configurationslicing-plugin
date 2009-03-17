package configurationslicing.timer;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import antlr.ANTLRException;

import configurationslicing.Slice;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Descriptor.FormException;
import hudson.triggers.TimerTrigger;

@Extension
public class TimerSlice extends Slice implements Serializable {
    private HashMap<String, List<String>> lineToProject;
    private HashMap<String, List<String>> projectToLines;
    
    public TimerSlice() {
        lineToProject = new HashMap<String, List<String>>();
    }
    
    @DataBoundConstructor
    public TimerSlice(String spec) {
        projectToLines = new HashMap<String, List<String>>();
        String [] lines = spec.split("\n");
        for(String line : lines) {
            System.out.println("Processing: " + line);
            String[] bits = line.split("::", 2);
            if(bits.length < 2) continue;
            String timerline = bits[0].trim();
            String [] projects = bits[1].split(",");
            for(String project : projects) {
                addLine(projectToLines, project.trim(), timerline.trim());
            }
            
        }
    }
    
    public void add(TimerTrigger trigger, AbstractProject<?, ?> i) {
        String spec = trigger == null ? "(disabled)" : trigger.getSpec();
        String [] lines = spec.split("\n");
        for(String s : lines) {
            addLine(lineToProject, s, i.getName());
        }
    }
    
    private static void addLine(Map<String, List<String>> map, String s, String name) {
        if(!map.containsKey(s)) {
            map.put(s, new ArrayList<String>());
        }
        List<String> list= map.get(s);
        list.add(name);
    }

    public String getSpec() {
        StringBuffer ret = new StringBuffer();
        for(Map.Entry<String, List<String>> ent : lineToProject.entrySet()) {
            ret.append(ent.getKey());
            ret.append(" :: ");
            boolean first = true;
            for(String proj : ent.getValue()) {
                if(!first) ret.append(", ");
                ret.append(proj);
                first=false;
            }
            ret.append("\n");
        }
        return ret.toString();
    }
    
    public boolean transform(TimerTrigger trigger, AbstractProject<?, ?> i) throws ANTLRException, IOException {
        List<String> lines = projectToLines.get(i.getName());
        if(lines == null) return false;
        boolean disabled = false;
        StringBuffer triggerspec = new StringBuffer();
        for(String line : lines) {
            line = line.trim();
            if(line.equals("(disabled)")) {
                disabled=true;
                continue;
            }
            triggerspec.append(line);
        }
        TimerTrigger newtrigger = null;
        if(!disabled) {
            newtrigger = new TimerTrigger(triggerspec.toString());
        }
        if(trigger != null) {
            i.removeTrigger(trigger.getDescriptor());
        }
        if(newtrigger != null) 
            i.addTrigger(newtrigger);
        return true;
    }

    @Override
    public TimerSlice newInstance(StaplerRequest req, JSONObject formData)
            throws FormException {
        return req.bindJSON(TimerSlice.class, formData);
    }
}
