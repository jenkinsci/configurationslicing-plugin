package configurationslicing.docker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.cloudbees.dockerpublish.DockerBuilder;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import configurationslicing.UnorderedStringSlicer.UnorderedStringSlicerSpec;
import hudson.matrix.MatrixProject;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.Project;
import hudson.tasks.Builder;
import hudson.util.DescribableList;
import jenkins.model.Jenkins;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDockerSlicerSpec
        extends UnorderedStringSlicerSpec<AbstractProject<?, ?>> {

    private static final Logger log = LoggerFactory.getLogger(AbstractDockerSlicerSpec.class);

    private static final String NOTHING = "(Nothing)";
    static final String DEFAULT = "(Default)";

    public abstract String getName();
    public abstract String getUrl();
    public abstract String getSliceParam(DockerBuilder builder);
    public abstract DockerBuilder setSliceParam(DockerBuilder builder, String value);

    @Override
    public String getDefaultValueString() {
        return DEFAULT;
    }

    @Override
    public String getName(AbstractProject<?, ?> item) {
        return item.getDisplayName();
    }

    public List<AbstractProject<?, ?>> getWorkDomain() {
        List<AbstractProject<?, ?>> list = new ArrayList<AbstractProject<?, ?>>();
        List<AbstractProject> temp = Jenkins.getInstance().getAllItems(AbstractProject.class);
        for (AbstractProject p: temp) {
            if (p instanceof Project || p instanceof MatrixProject) {
                list.add(p);
            }
        }
        return list;
    }


    @Override
    public List<String> getValues(AbstractProject<?,?> item) {

        List<String> valuesList = new ArrayList<String>();

        DescribableList<Builder, Descriptor<Builder>> buildersList = getBuildersList(item);
        List<DockerBuilder> builders = getDockerBuildersList(buildersList);

        for (DockerBuilder builder: builders) {
            valuesList.add(getSliceParam(builder));
        }
        if (valuesList.isEmpty()) {
            valuesList.add(NOTHING);
        }

        return valuesList;
    }

    @Override
    public boolean setValues(AbstractProject<?,?> item, List<String> values) {

        DescribableList<Builder, Descriptor<Builder>> buildersList = getBuildersList(item);
        List<DockerBuilder> dockerBuildersList = getDockerBuildersList(buildersList);

        int maxListSize = Math.max(values.size(), dockerBuildersList.size());
        DockerBuilder[] oldBuilders = new DockerBuilder[maxListSize];
        DockerBuilder[] newBuilders = new DockerBuilder[maxListSize];

        for (int i = 0; i < dockerBuildersList.size(); i++) {
            oldBuilders[i] = dockerBuildersList.get(i);
        }

        for (int i = 0; i < values.size(); i++) {
            String value = values.get(i);
            if(valueIsWritable(value)) {
                newBuilders[i] = oldBuilders[i];
                if((oldBuilders[i] != null && !getSliceParam(oldBuilders[i]).equals(value))) {
                    newBuilders[i] = setSliceParam(newBuilders[i], value);
                }
            }
        }

        // perform any replacements
        for (int i = 0; i < maxListSize; i++) {
            if (oldBuilders[i] != null && newBuilders[i] != null && oldBuilders[i] != newBuilders[i]) {
                replaceBuilder(buildersList, oldBuilders[i], newBuilders[i]);
            }
        }

        // add any new ones (can't see when this would happen)
        for (int i = 0; i < maxListSize; i++) {
            if (oldBuilders[i] == null && newBuilders[i] != null) {
                buildersList.add(newBuilders[i]);
                log.warn(
                        "added new builder {} to list",
                        oldBuilders[i].getDescriptor().getDisplayName()
                );
            }
        }

        // delete old ones (this shouldn't happen)
        for (int i = 0; i < maxListSize; i++) {
            if (oldBuilders[i] != null && newBuilders[i] == null) {
                buildersList.remove(oldBuilders[i]);
                log.error(
                        "removed builder {} from list",
                        oldBuilders[i].getDescriptor().getDisplayName()
                );
            }
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    private static DescribableList<Builder,Descriptor<Builder>> getBuildersList(
            AbstractProject<?, ?> item
    ) {
        if (item instanceof Project) {
            return ((Project) item).getBuildersList();
        } else if (item instanceof MatrixProject) {
            return ((MatrixProject) item).getBuildersList();
        } else {
            return null;
        }

    }

    private List<DockerBuilder> getDockerBuildersList(
            DescribableList<Builder, Descriptor<Builder>> builders
    ) {
        return builders.getAll(DockerBuilder.class);
    }

    private boolean valueIsWritable(String value) {
        return !(value.equals(NOTHING) || value.isEmpty());
    }

    private void replaceBuilder(
            DescribableList<Builder,Descriptor<Builder>> builders,
            Builder oldBuilder,
            Builder newBuilder
    ) {
        List<Builder> newList = Lists.newArrayList(builders.toList());
        for(int i = 0; i < newList.size(); i++) {
            Builder builder = newList.get(i);
            if(builder == oldBuilder) {
                newList.set(i, newBuilder);
            }
        }

        try {
            builders.replaceBy(newList);
        } catch (IOException e) {
            log.error(
                    "Failed to replace builder {} in list",
                    oldBuilder.getDescriptor().getDisplayName()
            );
            Throwables.propagate(e);
        }
    }
}
