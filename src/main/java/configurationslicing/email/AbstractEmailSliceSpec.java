package configurationslicing.email;

import hudson.model.AbstractProject;
import hudson.model.Hudson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import configurationslicing.UnorderedStringSlicer.UnorderedStringSlicerSpec;

@SuppressWarnings("unchecked")
public abstract class AbstractEmailSliceSpec extends UnorderedStringSlicerSpec<AbstractProject<?, ?>> {

	public static final String DISABLED = "(Disabled)";

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
			recipients = DISABLED;
		}
		List<String> values = new ArrayList<String>();
		values.add(recipients);
		return values;
	}
	public boolean setValues(AbstractProject<?, ?> project, Set<String> set) {
		String newEmail = join(set);
		
		// if no email is present, we're going to assume that's the same as disabled
		boolean disabled = (DISABLED.equals(newEmail) || newEmail == null);
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
		return (List) Hudson.getInstance().getItems(AbstractProject.class);
	}

	protected abstract ProjectHandler getProjectHandler(AbstractProject project);
	
	public String getDefaultValueString() {
		return DISABLED;
	}
	public String getName() {
		return name;
	}
	public String getName(AbstractProject<?, ?> item) {
		return item.getName();
	}

	public String getUrl() {
		return url;
	}

}
