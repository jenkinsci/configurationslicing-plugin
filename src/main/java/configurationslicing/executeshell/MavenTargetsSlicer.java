package configurationslicing.executeshell;

import hudson.Extension;
import hudson.maven.MavenModuleSet;
import hudson.model.Descriptor;
import hudson.tasks.Builder;
import hudson.tasks.Maven;
import hudson.tasks.Maven.MavenInstallation;
import hudson.util.DescribableList;

import java.util.List;

/**
 * @author Jacob Robertson
 */
@Extension
public class MavenTargetsSlicer extends AbstractBuildCommandSlicer<Maven> {

    public MavenTargetsSlicer() {
        super(new MavenTargetsSliceSpec());
    }

    @Override
    public void loadPluginDependencyClass() {
        MavenModuleSet.class.getClass();
    }

    public static class MavenTargetsSliceSpec extends AbstractBuildCommandSliceSpec<Maven> {

    	private static final String DEFAULT_MAVEN = "(Default)";

        public String getName() {
            return "Maven top-level targets";
        }

        public String getUrl() {
            return "maventopleveltargets";
        }
        @Override
        public Maven createBuilder(String command, List<Maven> existingBuilders, Maven oldBuilder) {
            if (oldBuilder != null) {
                MavenInstallation mavenInstall = oldBuilder.getMaven();
                String mavenName = mavenInstall == null ? null : mavenInstall.getName();
                return new Maven(command, mavenName, oldBuilder.pom, oldBuilder.properties, oldBuilder.jvmOptions,
                        oldBuilder.usePrivateRepository, oldBuilder.getSettings(), oldBuilder.getGlobalSettings());
            } else {
                // if the job already has another maven command, use the right version of maven
                String mavenName = DEFAULT_MAVEN;
                for (Maven maven: existingBuilders) {
                    MavenInstallation install = maven.getMaven();
                    if (install != null) {
                        mavenName = install.getName();
                        break;
                    }
                }
                return new Maven(command, mavenName);
            }
        }
        @Override
        public Maven[] createBuilderArray(int len) {
        	return new Maven[len];
        }
        @Override
        public String getCommand(Maven builder) {
        	return builder.getTargets();
        }
        @Override
        public List<Maven> getConcreteBuildersList(
        		DescribableList<Builder, Descriptor<Builder>> buildersList) {
            return buildersList.getAll(Maven.class);
        }

    }
}

