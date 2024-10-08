package configurationslicing.label;

import configurationslicing.TopLevelItemSelector;
import configurationslicing.UnorderedStringSlicer;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Label;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import jenkins.model.Jenkins;

@Extension
public class LabelSlicer extends UnorderedStringSlicer<AbstractProject> {

    public LabelSlicer() {
        super(new LabelSliceSpec());
    }

    public static class LabelSliceSpec extends UnorderedStringSlicerSpec<AbstractProject> {

        private static final String ROAMING = "(Roaming)";

        public String getDefaultValueString() {
            return ROAMING;
        }

        public String getName() {
            return "Tied Label Slicer";
        }

        public String getName(AbstractProject item) {
            return item.getFullName();
        }

        public String getUrl() {
            return "labelslicestring";
        }

        public List<String> getValues(AbstractProject item) {
            Label label = item.getAssignedLabel();
            String labelName = label == null ? ROAMING : label.getName();
            return Collections.singletonList(labelName);
        }

        public List<AbstractProject> getWorkDomain() {
            return TopLevelItemSelector.getAllTopLevelItems(AbstractProject.class);
        }

        public boolean setValues(AbstractProject item, List<String> set) {
            // can only have one label at a time.  do nothing if a node has zero
            // or multiple labels
            if (set.isEmpty() || set.size() > 1) return false;

            Label label = null;
            String labelName = set.iterator().next();
            if (ROAMING.equals(labelName)) {
                label = null;
            } else {
                label = Jenkins.get().getLabel(labelName);
                if (label == null) return false;
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
