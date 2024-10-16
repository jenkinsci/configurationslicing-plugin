package configurationslicing;

import hudson.model.AbstractProject;
import hudson.model.Job;
import hudson.triggers.Trigger;
import hudson.triggers.TriggerDescriptor;
import java.io.IOException;
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
        if (item instanceof AbstractProject<?, ?> project) return project.isConcurrentBuild();
        if (item instanceof WorkflowJob job) return job.isConcurrentBuild();

        return true;
    }

    public void makeConcurrentBuilds(boolean value) throws IOException {
        if (item instanceof AbstractProject<?, ?> project) project.setConcurrentBuild(value);
        if (item instanceof WorkflowJob job) job.setConcurrentBuild(value);

        throw new IOException("Unsupported job type");
    }

    @SuppressWarnings("unchecked")
    public <T extends Trigger> T getTrigger(Class<T> clazz) {
        if (item instanceof AbstractProject<?, ?> project) return (T) project.getTrigger(clazz);
        if (item instanceof WorkflowJob job) {
            return getTrigger(job, clazz);
        }

        return null;
    }

    public void removeTrigger(TriggerDescriptor trigger) throws IOException {
        if (item instanceof AbstractProject<?, ?> project) project.removeTrigger(trigger);
        if (item instanceof WorkflowJob job) {
            removeTrigger(job, trigger);
        }
    }

    public void addTrigger(Trigger<?> trigger) throws IOException {
        if (item instanceof AbstractProject<?, ?> project) project.addTrigger(trigger);
        if (item instanceof WorkflowJob job) {
            job.addTrigger(trigger);
        }
    }

    private <T extends Trigger> T getTrigger(WorkflowJob pipeline, Class<T> clazz) {
        for (Trigger p : pipeline.getTriggersJobProperty().getTriggers()) {
            if (clazz.isInstance(p)) return clazz.cast(p);
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
