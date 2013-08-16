package configurationslicing.maven;

import hudson.Extension;
import hudson.maven.MavenModule;
import hudson.maven.MavenModuleSet;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.model.TopLevelItem;
import hudson.tasks.Builder;
import hudson.tasks.Maven;
import hudson.tasks.Maven.MavenInstallation;
import hudson.util.DescribableList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import configurationslicing.UnorderedStringSlicer;
import configurationslicing.executeshell.AbstractBuildCommandSlicer.AbstractBuildCommandSliceSpec;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

@SuppressWarnings("unchecked")
@Extension
public class MavenVersionSlicer extends UnorderedStringSlicer<AbstractProject> {

	public MavenVersionSlicer() {
		super(new MavenVersionSlicerSpec());
	}

	public static class MavenVersionSlicerSpec extends
			UnorderedStringSlicerSpec<AbstractProject> {
		private static final String DEFAULT = "(Default)";
		private static final String MULTIPLE = "(MULTIPLE)";

		public String getDefaultValueString() {
			return DEFAULT;
		}

		public String getName() {
			return "Maven Version";
		}

		public String getName(AbstractProject item) {
			return item.getFullName();
		}

		public String getUrl() {
			return "mavenversion";
		}

		public List<String> getValues(AbstractProject item) {
			if (item instanceof MavenModuleSet) {
				return getValues((MavenModuleSet) item);
			} else {
				List<String> ret = new ArrayList<String>();
				List<Maven> builders = getBuilders(item);
				if (builders.isEmpty()) {
					return ret;
				}
				String last = null;
				Set<String> all = new HashSet<String>();
				for (Maven builder: builders) {
					last = builder.mavenName;
					all.add(last);
				}
				if (all.size() > 1) {
					ret.add(MULTIPLE);
				} else if (last != null) {
					ret.add(last);
				} else {
					ret.add(DEFAULT);
				}
				return ret;
			}
		}
		private List<Maven> getBuilders(AbstractProject item) {
			DescribableList<Builder,Descriptor<Builder>> buildersList = AbstractBuildCommandSliceSpec.getBuildersList(item);
			List<Maven> builders = buildersList.getAll(Maven.class);
			return builders;
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

		public List<AbstractProject> getWorkDomain() {
			List<AbstractProject> list = new ArrayList<AbstractProject>();

            // AbstractProject includes both FreeStyle/Matrix to have Maven build step and MavenModuleSet projects
            list.addAll(Hudson.getInstance().getAllItems(AbstractProject.class));

            CollectionUtils.filter(list, new Predicate() {
                public boolean evaluate(Object object) {
                    // exclude MatrixConfiguration, MavenModule, etc
                    return object instanceof TopLevelItem;
                }
            });
			return list;
		}

		public boolean setValues(AbstractProject item, List<String> set) {
			String mavenVersion = null;
			if (!set.isEmpty()) {
				mavenVersion = set.iterator().next();
			}
			// do not attempt to update the multiple versions at all
			if (MULTIPLE.equals(mavenVersion)) {
				return true;
			}
			if (item instanceof MavenModuleSet) {
				return setValues((MavenModuleSet) item, mavenVersion);
			} else {
				List<Maven> builders = getBuilders(item);
				DescribableList<Builder,Descriptor<Builder>> buildersList = AbstractBuildCommandSliceSpec.getBuildersList(item);
				for (Maven builder: builders) {
					String oldName = builder.mavenName;
					if (oldName == null) {
						oldName = DEFAULT;
					}
					if (!oldName.equals(mavenVersion)) {
						Maven newMaven = new Maven(builder.targets, mavenVersion, builder.pom, builder.properties, builder.jvmOptions, builder.usePrivateRepository);
						AbstractBuildCommandSliceSpec.replaceBuilder(buildersList, builder, newMaven);
					}
				}
				return true;
			}
		}
		public boolean setValues(MavenModuleSet item, String mavenVersion) {
			MavenInstallation old = item.getMaven();
			String oldName = null;
			if (old != null) {
				oldName = old.getName();
			}
			if (mavenVersion.trim().length() == 0 || DEFAULT.equals(mavenVersion)) {
				mavenVersion = null;
			}
			boolean save = false;
			if (mavenVersion == null) {
				if (oldName != null) {
					save = true;
				}
			} else if (!mavenVersion.equals(oldName)) {
				save = true;
			}
			if (save) {
				item.setMaven(mavenVersion);
				try {
					item.save();
					return true;
				} catch (IOException e) {
					return false;
				}
			}
			return false;
		}

	}

}
