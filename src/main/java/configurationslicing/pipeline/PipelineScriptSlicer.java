package configurationslicing.pipeline;

import java.util.Collections;
import java.util.List;

import configurationslicing.TopLevelItemSelector;
import configurationslicing.UnorderedStringSlicer;
import hudson.Extension;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.flow.FlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;

import static org.apache.commons.lang.StringUtils.isEmpty;

@Extension
public class PipelineScriptSlicer extends UnorderedStringSlicer<WorkflowJob> {

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
                        item.setDefinition(new CpsFlowDefinition("", true));
                    default:
                        item.setDefinition(new CpsFlowDefinition(newValue, true));
                }

            }
            return false; // for some reason setDefinition is already saving the job
        }
    }
}
