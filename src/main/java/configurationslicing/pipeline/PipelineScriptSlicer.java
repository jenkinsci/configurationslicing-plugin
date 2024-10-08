package configurationslicing.pipeline;

import static org.apache.commons.lang.StringUtils.isEmpty;

import configurationslicing.TopLevelItemSelector;
import configurationslicing.UnorderedStringSlicer;
import hudson.Extension;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.flow.FlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;

@Extension(optional = true)
public class PipelineScriptSlicer extends UnorderedStringSlicer<WorkflowJob> {
    private static final Logger LOGGER = Logger.getLogger(PipelineScriptSlicer.class.getName());

    public PipelineScriptSlicer() {
        super(new PipelineScriptSliceSpec());
    }

    @Override
    public void loadPluginDependencyClass() {
        CpsFlowDefinition.class.getClass();
    }

    public static class PipelineScriptSliceSpec extends UnorderedStringSlicerSpec<WorkflowJob> {

        private static final String DEFINED_IN_SCM = "(Defined in SCM)";
        private static final String EMPTY = "(Empty)";

        public String getDefaultValueString() {
            return DEFINED_IN_SCM;
        }

        public String getName() {
            return "Pipeline Script Slicer";
        }

        public String getName(WorkflowJob item) {
            return item.getFullName();
        }

        public String getUrl() {
            return "pipelinescriptslicestring";
        }

        public List<String> getValues(WorkflowJob item) {
            FlowDefinition definition = item.getDefinition();
            String flow = definition == null ? EMPTY : DEFINED_IN_SCM;
            if (definition instanceof CpsFlowDefinition) {
                flow = ((CpsFlowDefinition) definition).getScript();
                if (isEmpty(flow)) {
                    flow = EMPTY;
                }
            }
            return Collections.singletonList(flow);
        }

        public List<WorkflowJob> getWorkDomain() {
            return TopLevelItemSelector.getAllTopLevelItems(WorkflowJob.class);
        }

        private void setDefinition(WorkflowJob item, String definition) {
            try {
                item.setDefinition(new CpsFlowDefinition(definition, true));
            } catch (hudson.model.Descriptor.FormException e) {
                LOGGER.log(Level.FINE, "Cannot set definition", e);
            }
        }

        public boolean setValues(WorkflowJob item, List<String> set) {
            if (set.isEmpty() || set.size() > 1) {
                return false;
            }

            String oldValue = getValues(item).iterator().next();
            String newValue = set.iterator().next();

            if (!oldValue.equals(newValue)) {
                switch (newValue) {
                    case DEFINED_IN_SCM:
                        break;
                    case EMPTY:
                        setDefinition(item, "");
                    default:
                        setDefinition(item, newValue);
                }
            }
            return false; // for some reason setDefinition is already saving the job
        }
    }
}
