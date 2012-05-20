package configurationslicing.maven;

import hudson.Extension;
import hudson.maven.MavenModuleSet;
import hudson.model.Hudson;
import hudson.tasks.Maven.MavenInstallation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import configurationslicing.UnorderedStringSlicer;

@Extension
public class MavenVersionSlicer extends UnorderedStringSlicer<MavenModuleSet> {

	public MavenVersionSlicer() {
		super(new MavenVersionSlicerSpec());
	}

	public static class MavenVersionSlicerSpec extends
			UnorderedStringSlicerSpec<MavenModuleSet> {
		private static final String DEFAULT = "(Default)";

		public String getDefaultValueString() {
			return DEFAULT;
		}

		public String getName() {
			return "Maven Version (Maven Projects)";
		}

		public String getName(MavenModuleSet item) {
			return item.getName();
		}

		public String getUrl() {
			return "mavenversion";
		}

		
		public List<String> getValues(MavenModuleSet item) {
			List<String> ret = new ArrayList<String>();
			MavenInstallation itemMaven = item.getMaven();
			if (itemMaven != null) {
				String itemMavenName = itemMaven.getName();
				for (MavenInstallation maven : MavenModuleSet.DESCRIPTOR
						.getMavenDescriptor().getInstallations()) {
					String mavenName = maven.getName();
					if (itemMavenName.equals(mavenName)) {
						ret.add(itemMavenName);
					}
				}
			}
			return ret;
		}

		@SuppressWarnings("unchecked")
		public List<MavenModuleSet> getWorkDomain() {
			return (List) Hudson.getInstance().getItems(MavenModuleSet.class);
		}

		public boolean setValues(MavenModuleSet item, List<String> set) {
			MavenInstallation old = item.getMaven();
			String oldName = null;
			if (old != null) {
				oldName = old.getName();
			}
			for (String maven : set) {
				if (maven.trim().length() == 0 || DEFAULT.equals(maven)) {
					maven = null;
				}
				boolean save = false;
				if (maven == null) {
					if (oldName != null) {
						save = true;
					}
				} else if (!maven.equals(oldName)) {
					save = true;
				}
				if (save) {
					item.setMaven(maven);
					try {
						item.save();
						return true;
					} catch (IOException e) {
						return false;
					}
				}
			}
			return false;
		}

	}

}
