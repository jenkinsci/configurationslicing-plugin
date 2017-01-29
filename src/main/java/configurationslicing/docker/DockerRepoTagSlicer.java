package configurationslicing.docker;

import com.cloudbees.dockerpublish.DockerBuilder;
import configurationslicing.UnorderedStringSlicer;
import hudson.Extension;
import hudson.model.AbstractProject;


@Extension
public class DockerRepoTagSlicer extends UnorderedStringSlicer<AbstractProject<?,?>> {

    public DockerRepoTagSlicer() {super (new DockerSlicerSpec()); }

    public static class DockerSlicerSpec extends
            AbstractDockerSlicerSpec {

        @Override
        public String getName() {
            return "Docker Slicer - Repo Tags";
        }

        @Override
        public String getUrl() {
            return "dockerrepotags";
        }

        @Override
        public String getSliceParam(DockerBuilder builder) {
            return builder.getRepoTag();
        }

        @Override
        public DockerBuilder setSliceParam(DockerBuilder builder, String value) {
            builder.setRepoTag(value);
            return builder;
        }

    }
}
