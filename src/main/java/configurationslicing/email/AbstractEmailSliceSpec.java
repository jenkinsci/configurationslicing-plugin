package configurationslicing.email;

import hudson.model.AbstractProject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import configurationslicing.TopLevelItemSelector;
import configurationslicing.UnorderedStringSlicer.UnorderedStringSlicerSpec;

public abstract class AbstractEmailSliceSpec extends UnorderedStringSlicerSpec<AbstractProject<?, ?>> {

	public static final String DISABLED = "(Disabled)";
	private static final String EMPTY = "";
        public static final String WHO_BROKE = "(send_to_individuals_who_broke)";

	private String joinString;
	private String name;
	private String url;
	
	protected AbstractEmailSliceSpec(String joinString, String name, String url) {
		this.joinString = joinString;
		this.name = name;
		this.url = url;
	}
	
	public List<String> getValues(AbstractProject<?, ?> project) {
		ProjectHandler handler = getProjectHandler(project);
		String recipients = handler.getRecipients(project);
		recipients = normalize(recipients, "\n");
		if (recipients == null) {
			if (handler.sendToIndividuals(project)) {
				recipients = WHO_BROKE;
			}
			else {
				recipients = DISABLED;
			}
		} else if (handler.sendToIndividuals(project)) {
			recipients = recipients + "\n" + WHO_BROKE;
		}
		List<String> values = new ArrayList<String>();
		values.add(recipients);
		return values;
	}
	public boolean setValues(AbstractProject<?, ?> project, List<String> set) {
		String newEmail = join(set);
		
		// only regard explicit (disabled) [regardless of case]
		boolean disabled = (newEmail == null) ? false : (DISABLED.toLowerCase().equals(newEmail.toLowerCase()));		
		boolean saved = false;
		ProjectHandler handler = getProjectHandler(project);

		try {
			if (disabled) {
				boolean oneSaved = handler.removeMailer(project);
				if (oneSaved) {
					saved = true;
				}
			} else {
				boolean oneSaved = handler.addMailer(project);
				if (oneSaved) {
					saved = true;
				}
				boolean wasSet = handler.setRecipients(project, newEmail);
				if (wasSet) {
					try {
						project.save();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
					saved = true;
				}
			}
			return saved;
		} catch (IOException e) {
			return false;
		}
	}
	public String normalize(String value, String joinString) {
		value = StringUtils.trimToNull(value);
		if (value == null) {
			return null;
		}
		// don't lowercase the templates
		if (value.startsWith("$")) {
			return value;
		}
		String[] split = value.split("[;,\\s]");
		Arrays.sort(split, String.CASE_INSENSITIVE_ORDER);
		
		StringBuffer buf = new StringBuffer();
		for (String s: split) {
			if (buf.length() > 0) {
				buf.append(joinString);
			}
			// TODO this strategy isn't really fair to admins that keep the proper-case names.
			//	In a future release, I would like a more complex strategy to preserve the case wherever possible.
			s = s.toLowerCase();
			buf.append(s);
		}
		return buf.toString();
	}
	public String join(Collection<String> set) {
		if (set.isEmpty()) {
			return null;
		}
		String value = set.iterator().next();
		if (!DISABLED.equals(value)) {
			value = normalize(value, joinString);
		}
		return value;
	}
	public List<AbstractProject<?, ?>> getWorkDomain() {
		return TopLevelItemSelector.getAllTopLevelItems(AbstractProject.class);
	}

	protected abstract ProjectHandler getProjectHandler(AbstractProject<?, ?>  project);
	
	public String getDefaultValueString() {
		return DISABLED;
	}
	public String getName() {
		return name;
	}
	public String getName(AbstractProject<?, ?> item) {
		return item.getFullName();
	}

	public String getUrl() {
		return url;
	}

}
