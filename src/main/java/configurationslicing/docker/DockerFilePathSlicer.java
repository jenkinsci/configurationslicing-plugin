package configurationslicing.docker;

import com.cloudbees.dockerpublish.DockerBuilder;
import configurationslicing.UnorderedStringSlicer;
import hudson.Extension;
import hudson.model.AbstractProject;

@Extension
public class DockerFilePathSlicer extends UnorderedStringSlicer<AbstractProject<?,?>> {

    public DockerFilePathSlicer() {super (new DockerSlicerSpec()); }

    public static class DockerSlicerSpec extends
            AbstractDockerSlicerSpec {

        @Override
        public String getName() {
            return "Docker Slicer - Dockerfile Path";
        }

        @Override
        public String getUrl() {
            return "dockerfilepath";
        }

        @Override
        public String getSliceParam(DockerBuilder builder) {
            if (builder.getDockerfilePath() == null || builder.getDockerfilePath().isEmpty()) {
                return DEFAULT;
            } else {
                return builder.getDockerfilePath();
            }
        }

        @Override
        public DockerBuilder setSliceParam(DockerBuilder builder, String value) {
            if (!value.equals(DEFAULT)) {
                builder.setDockerfilePath(value);
            } else {
                builder.setDockerfilePath(null);
            }
            return builder;
        }

    }
}
