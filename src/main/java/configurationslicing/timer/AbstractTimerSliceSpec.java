package configurationslicing.timer;

import hudson.model.Job;
import hudson.triggers.Trigger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import configurationslicing.TopLevelItemSelector;
import configurationslicing.UnorderedStringSlicer.UnorderedStringSlicerSpec;

import static configurationslicing.AbstractJob.fix;

@SuppressWarnings("unchecked")
public abstract class AbstractTimerSliceSpec extends
		UnorderedStringSlicerSpec<Job> {

	public static final String DISABLED = "(Disabled)";

	private Class triggerClass;

	protected AbstractTimerSliceSpec(Class triggerClass) {
		this.triggerClass = triggerClass;
	}
    public String getDefaultValueString() {
    	return DISABLED;
    }

	public Class getTriggerClass() {
		return triggerClass;
	}

	public String getName(Job item) {
		return item.getFullName();
	}

	public List<String> getValues(Job item) {
		Trigger trigger = fix(item).getTrigger(triggerClass);
		return getValues(trigger);
	}

	public static List<String> getValues(Trigger trigger) {
		if (trigger == null) {
			return Collections.singletonList(DISABLED);
		}
		String spec = trigger.getSpec();
		return splitChronSpec(spec);
	}

	public List<Job> getWorkDomain() {
		return TopLevelItemSelector.getAllTopLevelItems(Job.class);
	}

	public abstract Trigger newTrigger(String spec, Trigger oldTrigger);

	public boolean setValues(Job item, List<String> set) {
		if (set.isEmpty())
			return false;

		List<String> list = new ArrayList<String>(set);
		String spec = joinChronSpec(list);
		boolean disabled = DISABLED.equals(spec);

		Trigger oldTrigger = fix(item).getTrigger(triggerClass);
		
		// see if there are any changes
		if (oldTrigger != null) {
			String oldSpec = oldTrigger.getSpec();
			if (oldSpec.equals(spec)) {
				return false;
			}
		}

		// now do the transformation
		try {
			Trigger newtrigger = null;
			if (!disabled) {
				newtrigger = newTrigger(spec, oldTrigger);
			}
			if (oldTrigger != null) {
				fix(item).removeTrigger(oldTrigger.getDescriptor());
			}
			if (newtrigger != null) {
				fix(item).addTrigger(newtrigger);
				// this is necessary, otherwise the trigger has a null job
				// this method as currently implemented in Trigger does nothing more than save the job
				newtrigger.start(item, true);
			}
			return true;
		} catch (IllegalArgumentException e) {
			// need to log this
			return false;
		} catch (IOException e) {
			// need to log this
			return false;
		}
	}

	/**
	 * Will both split and normalize, then return one spec per string, with
	 * comments above.
	 */
	public static List<String> splitChronSpec(String spec) {
		String[] split = spec.trim().split("\n");
		List<String> specs = new ArrayList<String>();

		boolean lastWasComment = false;
		StringBuilder currentSpec = new StringBuilder();
		for (String line : split) {
			line = line.trim();
			if (line.length() == 0) {
				continue;
			}
			boolean isComment = line.startsWith("#");
			if (currentSpec.length() > 0) {
				// check for ends of specs
				boolean startNew = !isComment && !lastWasComment || isComment
						&& !lastWasComment;
				if (startNew) {
					specs.add(currentSpec.toString().trim());
					currentSpec = new StringBuilder();
				}
			}
			currentSpec.append(line);
			currentSpec.append("\n");
			lastWasComment = isComment;
		}
		if (currentSpec.length() > 0) {
			specs.add(currentSpec.toString().trim());
		}

		return specs;
	}

	public static String joinChronSpec(List<String> lines) {
		// because we're coming back from what was entered in the gui -
		// normalize first
		StringBuilder builder = new StringBuilder();
		for (String line : lines) {
			List<String> splitOne = splitChronSpec(line);
			for (String one : splitOne) {
				if (builder.length() > 0) {
					builder.append("\n\n");
				}
				builder.append(one);
			}
		}
		return builder.toString();
	}
}