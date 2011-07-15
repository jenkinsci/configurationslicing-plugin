package configurationslicing.timer;

import hudson.model.AbstractProject;
import hudson.model.Hudson;
import hudson.triggers.TimerTrigger;
import hudson.triggers.Trigger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import antlr.ANTLRException;
import configurationslicing.UnorderedStringSlicer.UnorderedStringSlicerSpec;

public abstract class AbstractTimerSliceSpec implements
		UnorderedStringSlicerSpec<AbstractProject<?, ?>> {

	static final String DISABLED = "(Disabled)";

	public String getName(AbstractProject<?, ?> item) {
		return item.getName();
	}

	public List<String> getValues(AbstractProject<?, ?> item) {
		TimerTrigger trigger = item.getTrigger(TimerTrigger.class);
		return getValues(trigger);
	}

	@SuppressWarnings("unchecked")
	public static List<String> getValues(Trigger trigger) {
		if (trigger == null) {
			return Collections.singletonList(DISABLED);
		}
		String spec = trigger.getSpec();
		return splitChronSpec(spec);
	}

	@SuppressWarnings("unchecked")
	public List<AbstractProject<?, ?>> getWorkDomain() {
		return (List) Hudson.getInstance().getItems(AbstractProject.class);
	}

	@SuppressWarnings("unchecked")
	protected abstract Trigger newTrigger(String spec) throws ANTLRException;

	@SuppressWarnings("unchecked")
	public boolean setValues(AbstractProject<?, ?> item, Set<String> set) {
		if (set.isEmpty())
			return false;

		List<String> list = new ArrayList<String>(set);
		String spec = joinChronSpec(list);

		TimerTrigger trigger = item.getTrigger(TimerTrigger.class);
		boolean disabled = DISABLED.equals(spec);

		// now do the transformation
		try {
			Trigger newtrigger = null;
			if (!disabled) {
				newtrigger = newTrigger(spec);
			}
			if (trigger != null) {
				item.removeTrigger(trigger.getDescriptor());
			}
			if (newtrigger != null) {
				item.addTrigger(newtrigger);
			}
			return true;
		} catch (ANTLRException e) {
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