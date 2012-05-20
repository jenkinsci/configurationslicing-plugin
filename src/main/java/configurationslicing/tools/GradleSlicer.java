package configurationslicing.tools;

import hudson.Extension;
import hudson.plugins.gradle.Gradle;
import hudson.plugins.gradle.Gradle.DescriptorImpl;
import hudson.tasks.Builder;
import hudson.tools.ToolInstallation;

/**
 * @author Maarten Dirkse
 */
@Extension
public class GradleSlicer extends AbstractToolSlicer {

  public GradleSlicer() {
    super(new GradleSlicerSpec());
  }

  @Override
  protected Class< ? extends Builder> getPluginClass() {
    return Gradle.class;
  }

  public static class GradleSlicerSpec extends AbstractToolSlicerSpec {
    @Override
    public String getDefaultValueString() {
      return "(Default)";
    }

    @Override
    public String getName() {
      return "Gradle version per project";
    }

    @Override
    public String getUrl() {
      return "projectgradle";
    }

    @Override
    protected Class< ? extends Builder> getBuilderClass() {
      return Gradle.class;
    }

    @Override
    protected ToolInstallation[] getToolInstallations() {
      return new DescriptorImpl().getInstallations();
    }

    @Override
    protected Builder getNewBuilder(Builder oldBuilder, String toolInstallationName) {
      Gradle oldGradle = (Gradle) oldBuilder;
      return new Gradle(oldGradle.getDescription(), oldGradle.getSwitches(), oldGradle.getTasks(), oldGradle.getRootBuildScriptDir(),
          oldGradle.getBuildFile(), toolInstallationName, true);
    }

    @Override
    protected String getToolName(Builder builder) {
      return ((Gradle) builder).getGradleName();
    }
  }
}