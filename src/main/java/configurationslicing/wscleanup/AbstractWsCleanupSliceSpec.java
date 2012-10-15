package configurationslicing.wscleanup;

import hudson.model.AbstractProject;
import hudson.plugins.ws_cleanup.Pattern;
import hudson.plugins.ws_cleanup.Pattern.PatternType;

import java.util.ArrayList;
import java.util.List;

import configurationslicing.UnorderedStringSlicer.UnorderedStringSlicerSpec;

public abstract class AbstractWsCleanupSliceSpec extends UnorderedStringSlicerSpec<AbstractProject<?,?>> {

	private static final String DISABLED = "(Disabled)";
	private static final String SEPARATOR = ",";
	private static final String INCLUDE = "+";
	private static final String EXCLUDE = "-";
	
	private String url;
	private String name;
	
	public AbstractWsCleanupSliceSpec(String url, String name) {
		this.url = url;
		this.name = name;
	}

	@Override
	public String getDefaultValueString() {
		return DISABLED;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getName(AbstractProject<?, ?> item) {
		return item.getName();
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public List<String> getValues(AbstractProject<?, ?> item) {
		List<String> values = new ArrayList<String>();
		CleanupInfo info = getCleanupInfo(item);
		if (info == null) {
			values.add(DISABLED);
		} else {
			StringBuilder buf = new StringBuilder();
			buf.append(String.valueOf(info.appliesToDirectories));
			if (isSkipEnabled()) {
				buf.append(SEPARATOR);
				buf.append(String.valueOf(info.skipWhenFailed));
			}
			for (Pattern pattern: info.patterns) {
				buf.append(SEPARATOR);
				if (pattern.getType() == PatternType.EXCLUDE) {
					buf.append(EXCLUDE);
				} else {
					buf.append(INCLUDE);
				}
				buf.append(pattern.getPattern());
			}
			values.add(buf.toString());
		}
		return values;
	}

	public abstract CleanupInfo getCleanupInfo(AbstractProject<?, ?> item);

	@Override
	public boolean setValues(AbstractProject<?, ?> item, List<String> list) {
		if (list != null && !list.isEmpty()) {
			String value = list.get(0);
			if (value.equals(DISABLED)) {
				setCleanupInfo(item, null);
				return true;
			} else {
				CleanupInfo info = new CleanupInfo();
				String[] split = value.split(SEPARATOR);
				int pos = 0;
				info.appliesToDirectories = Boolean.parseBoolean(split[pos++]);
				if (isSkipEnabled()) {
					info.skipWhenFailed = Boolean.parseBoolean(split[pos++]);
				}
				for (int i = pos; i < split.length; i++) {
					String patternTypeString = split[i].substring(0, 1);
					String patternString = split[i].substring(1);
					PatternType type;
					if (INCLUDE.equals(patternTypeString)) {
						type = PatternType.INCLUDE;
					} else {
						type = PatternType.EXCLUDE;
					}
					Pattern pattern = new Pattern(patternString, type);
					info.patterns.add(pattern);
				}
				
				return setCleanupInfo(item, info);
			}
		}
		return false;
	}
	public abstract boolean isSkipEnabled();
	public abstract boolean setCleanupInfo(AbstractProject<?, ?> item, CleanupInfo info);

	@Override
	public String getConfiguredValueDescription() {
		StringBuilder buf = new StringBuilder();
		buf.append("Apply to Directories,");
		if (isSkipEnabled()) {
			buf.append("Skip on Fail,");
		}
		buf.append("-Exclude Pattern,+Include Pattern,...");
		buf.append("<br/><i>(e.g. false,");
		if (isSkipEnabled()) {
			buf.append("true,");
		}
		buf.append("+config/**,-*.xml,-*.txt)</i>");
		return buf.toString();
	}

	public static class CleanupInfo {
		public List<Pattern> patterns = new ArrayList<Pattern>();
		public boolean appliesToDirectories = false;
		public boolean skipWhenFailed = false;
	}

}
