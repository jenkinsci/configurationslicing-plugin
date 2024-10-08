package configurationslicing.tools;

import hudson.plugins.groovy.Groovy;
import hudson.tasks.Builder;
import hudson.tools.ToolInstallation;
import jenkins.model.Jenkins;

/**
 * @author Maarten Dirkse
 */
public class GroovySlicer extends AbstractToolSlicer {

    public GroovySlicer() {
        super(new GroovySlicerSpec());
    }

    @Override
    protected Class<? extends Builder> getPluginClass() {
        return Groovy.class;
    }

    public static class GroovySlicerSpec extends AbstractToolSlicerSpec {

        @Override
        public String getDefaultValueString() {
            return "(Default)";
        }

        @Override
        public String getName() {
            return "Groovy version per project";
        }

        @Override
        public String getUrl() {
            return "projectgroovy";
        }

        @Override
        protected Class<? extends Builder> getBuilderClass() {
            return Groovy.class;
        }

        @Override
        protected Builder getNewBuilder(Builder oldInstall, String installationName) {
            Groovy oldGroovy = (Groovy) oldInstall;
            return new Groovy(
                    oldGroovy.getScriptSource(),
                    installationName,
                    oldGroovy.getParameters(),
                    oldGroovy.getScriptParameters(),
                    oldGroovy.getProperties(),
                    oldGroovy.getJavaOpts(),
                    oldGroovy.getClassPath());
        }

        @Override
        protected String getToolName(Builder builder) {
            return ((Groovy) builder).getGroovyName();
        }

        @Override
        protected ToolInstallation[] getToolInstallations() {
            return Jenkins.get()
                    .getDescriptorByType(Groovy.DescriptorImpl.class)
                    .getInstallations();
        }
    }
}
