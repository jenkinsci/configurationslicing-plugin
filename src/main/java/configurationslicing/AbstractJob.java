package configurationslicing;

import java.io.IOException;

import hudson.model.AbstractProject;
import hudson.model.Job;
import hudson.triggers.Trigger;
import hudson.triggers.TriggerDescriptor;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;

/**
 * Object that tries to be missing link between {@link AbstractProject}
 * and {@link WorkflowJob} for the purpose of this plugin.
 *
 * @author Michal Slusarczyk
 */
public class AbstractJob {

    private Job item;

    public AbstractJob(Job item) {
        this.item = item;
    }

    public static AbstractJob fix(Job item) {
        return new AbstractJob(item);
    }

    public boolean isConcurrentBuilds() {
        if (item instanceof AbstractProject)
            return ((AbstractProject) item).isConcurrentBuild();
        if (item instanceof WorkflowJob)
            return ((WorkflowJob) item).isConcurrentBuild();

        return true;
    }

    public void makeConcurrentBuilds(boolean value) throws IOException {
        if (item instanceof AbstractProject)
            ((AbstractProject) item).setConcurrentBuild(value);
        if (item instanceof WorkflowJob)
            ((WorkflowJob) item).setConcurrentBuild(value);

        throw new IOException("Unsupported job type");
    }

    public <T extends Trigger> T getTrigger(Class<T> clazz) {
        if (item instanceof AbstractProject)
            return (T) ((AbstractProject) item).getTrigger(clazz);
        if (item instanceof WorkflowJob) {
            return getTrigger((WorkflowJob) item, clazz);
        }

        return null;
    }

    public void removeTrigger(TriggerDescriptor trigger) throws IOException {
        if (item instanceof AbstractProject)
            ((AbstractProject) item).removeTrigger(trigger);
        if (item instanceof WorkflowJob) {
            removeTrigger((WorkflowJob) item, trigger);
        }
    }

    public void addTrigger(Trigger<?> trigger) throws IOException {
        if (item instanceof AbstractProject)
            ((AbstractProject) item).addTrigger(trigger);
        if (item instanceof WorkflowJob) {
            ((WorkflowJob) item).addTrigger(trigger);
        }
    }

    private <T extends Trigger> T getTrigger(WorkflowJob pipeline, Class<T> clazz) {
        for (Trigger p : pipeline.getTriggersJobProperty().getTriggers()) {
            if (clazz.isInstance(p))
                return clazz.cast(p);
        }
        return null;
    }

    private void removeTrigger(WorkflowJob pipeline, TriggerDescriptor triggerDescriptor) {
        Trigger<?> trigger = pipeline.getTriggers().get(triggerDescriptor);
        if (trigger != null) {
            pipeline.getTriggersJobProperty().removeTrigger(trigger);
        }
    }
}
