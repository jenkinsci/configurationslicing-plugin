package configurationslicing.tools;

import hudson.model.AbstractProject;
import hudson.model.Hudson;
import hudson.model.Project;
import hudson.tasks.Builder;
import hudson.tools.ToolInstallation;
import hudson.util.DescribableList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import configurationslicing.TopLevelItemSelector;
import configurationslicing.UnorderedStringSlicer.UnorderedStringSlicerSpec;

/**
 * @author Maarten Dirkse
 */
public abstract class AbstractToolSlicerSpec extends UnorderedStringSlicerSpec<AbstractProject> {
  private static final Logger LOGGER = Logger.getLogger(AbstractToolSlicerSpec.class.getName());

  @Override
  public abstract String getDefaultValueString();

  protected abstract Class< ? extends Builder> getBuilderClass();

  protected abstract String getToolName(Builder builder);

  protected abstract ToolInstallation[] getToolInstallations();

  protected abstract Builder getNewBuilder(Builder oldBuilder, String toolInstallationName);

  @Override
  public String getName(AbstractProject item) {
    return item.getFullName();
  }

  @Override
  public List<AbstractProject> getWorkDomain() {
    return TopLevelItemSelector.getAllTopLevelItems(AbstractProject.class);
  }

  @Override
  public List<String> getValues(AbstractProject item) {
    List<String> ret = new ArrayList<String>();
    if (!(item instanceof Project)) {
      return ret;
    }

    List< ? extends Builder> builders = ((Project) item).getBuildersList().getAll(getBuilderClass());
    for (Builder builder : builders) {
      ret.add(getToolName(builder));
    }

    return ret;
  }

  @Override
  public boolean setValues(AbstractProject item, List<String> set) {
    if (!(item instanceof Project) || set.size() == 0)
      return false;

    DescribableList dl = ((Project) item).getBuildersList();
    List<Builder> builders = dl.getAll(getBuilderClass());

    if (LOGGER.isLoggable(Level.FINE))
      LOGGER.fine(builders.toString() + " : " + set.toString());

    String[] builderNames = new String[set.size()];
    set.toArray(builderNames);

    // If the number of builders and gradle install names don't match, something went wrong 
    if (builders.size() != builderNames.length)
      return false;

    for (int i = 0; i < builderNames.length; i++) {
      Builder oldBuilder = builders.get(i);
      // Check that the given gradle install name actually references a different installed gradle version, and, if so, set it
      if (StringUtils.equals(getToolName(oldBuilder), builderNames[i]))
        continue;

      if (null == getInstallations(builderNames[i]))
        return false;

      /*
       * If we're explicitly setting the gradle version, we don't want to use the wrapper, but since
       * it's inexplicably set using a !boolean construct, we have to pass in "true" :S
       */
      Builder newBuilder = getNewBuilder(oldBuilder, builderNames[i]);

      // Using this rather roundabout method as there seems to be no way to order a DescribableList
      List newBuilderList = new ArrayList(dl.toList());
      newBuilderList.add(newBuilderList.indexOf(oldBuilder), newBuilder);
      newBuilderList.remove(oldBuilder);

      dl.clear();
      dl.addAll(newBuilderList);
    }

    return true;
  }

  protected ToolInstallation getInstallations(String installationName) {
    for (ToolInstallation ti : getToolInstallations()) {
      if (StringUtils.equals(ti.getName(), installationName))
        return ti;
    }
    return null;
  }
}