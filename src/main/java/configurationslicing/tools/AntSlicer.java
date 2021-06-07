package configurationslicing.tools;

import hudson.Extension;
import hudson.tasks.Builder;
import hudson.tasks.Ant;
import hudson.tasks.Ant.DescriptorImpl;
import hudson.tools.ToolInstallation;

/**
 * @author Maarten Dirkse
 */
@Extension(optional = true)
public class AntSlicer extends AbstractToolSlicer {

  public AntSlicer() {
    super(new AntSlicerSpec());
  }

  @Override
  protected Class< ? extends Builder> getPluginClass() {
    return Ant.class;
  }

  public static class AntSlicerSpec extends AbstractToolSlicerSpec {
    @Override
    public String getDefaultValueString() {
      return "Default";
    }

    @Override
    public String getName() {
      return "Ant version per project";
    }

    @Override
    public String getUrl() {
      return "projectant";
    }

    @Override
    protected Class< ? extends Builder> getBuilderClass() {
      return Ant.class;
    }

    @Override
    protected Builder getNewBuilder(Builder oldBuilder, String toolInstallationName) {
      Ant oldAnt = (Ant) oldBuilder;
      return new Ant(oldAnt.getTargets(), toolInstallationName, oldAnt.getAntOpts(), oldAnt.getBuildFile(), oldAnt.getProperties());
    }

    @Override
    protected ToolInstallation[] getToolInstallations() {
      return new DescriptorImpl().getInstallations();
    }

    @Override
    protected String getToolName(Builder builder) {
      return ((Ant) builder).getAnt() == null ? getDefaultValueString() : ((Ant) builder).getAnt().getName();
    }
  }
}