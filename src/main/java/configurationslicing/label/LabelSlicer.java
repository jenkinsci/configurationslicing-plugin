package configurationslicing.label;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Hudson;
import hudson.model.Label;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import configurationslicing.UnorderedStringSlicer;

@Extension
public class LabelSlicer extends UnorderedStringSlicer<AbstractProject<?,?>>{

    public LabelSlicer() {
        super(new LabelSliceSpec());
    }
    public static class LabelSliceSpec implements UnorderedStringSlicerSpec<AbstractProject<?,?>> {

        private static final String ROAMING = "(Roaming)";

        public String getName() {
            return "Tied Label Slicer";
        }

        public String getName(AbstractProject<?, ?> item) {
            return item.getName();
        }

        public String getUrl() {
            return "labelslicestring";
        }

        public List<String> getValues(AbstractProject<?, ?> item) {
            Label label = item.getAssignedLabel();
            String labelName = label == null ? ROAMING : label.getName();
            return Collections.singletonList(labelName);
        }

        public List<AbstractProject<?, ?>> getWorkDomain() {
            return (List)Hudson.getInstance().getItems(AbstractProject.class);
        }

        public boolean setValues(AbstractProject<?, ?> item, Set<String> set) {
            // can only have one label at a time.  do nothing if a node has zero
            // or multiple labels
            if(set.isEmpty() || set.size() > 1) return false;

            Label label = null;
            String labelName = set.iterator().next();
            if(ROAMING.equals(labelName)) {
                label = null;
            } else {
                label = Hudson.getInstance().getLabel(labelName);
                if(label == null) return false;
            }
            try {
                item.setAssignedLabel(label);
                return true;
            } catch (IOException e) {
                return false;
            }
        }
    }
}
