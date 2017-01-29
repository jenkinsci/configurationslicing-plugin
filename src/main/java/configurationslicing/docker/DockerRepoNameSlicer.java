package configurationslicing.docker;

import com.cloudbees.dockerpublish.DockerBuilder;
import configurationslicing.UnorderedStringSlicer;
import hudson.Extension;
import hudson.model.AbstractProject;


@Extension
public class DockerRepoNameSlicer extends UnorderedStringSlicer<AbstractProject<?,?>> {

    public DockerRepoNameSlicer() {super (new DockerSlicerSpec()); }

    public static class DockerSlicerSpec extends
            AbstractDockerSlicerSpec {

        @Override
        public String getName() {
            return "Docker Slicer - Repo Name";
        }

        @Override
        public String getUrl() {
            return "dockerreponame";
        }

        @Override
        public String getSliceParam(DockerBuilder builder) {
            return builder.getRepoName();
        }

        @Override
        public DockerBuilder setSliceParam(DockerBuilder builder, String value) {
            // setting repo name isn't exposed by the api so we're copying the current one and
            // replacing the repo name using the constructor
            return setRepoName(builder, value);
        }

        private DockerBuilder setRepoName(DockerBuilder oldBuilder, String repoName) {

            DockerBuilder newBuilder = new DockerBuilder(repoName);

            newBuilder.setBuildAdditionalArgs(oldBuilder.getBuildAdditionalArgs());
            newBuilder.setBuildContext(oldBuilder.getBuildContext());
            newBuilder.setCreateFingerprint(oldBuilder.isCreateFingerprint());
            newBuilder.setDockerfilePath(oldBuilder.getDockerfilePath());
            newBuilder.setDockerToolName(oldBuilder.getDockerToolName());
            newBuilder.setForcePull(oldBuilder.isForcePull());
            newBuilder.setForceTag(oldBuilder.isForceTag());
            newBuilder.setNoCache(oldBuilder.isNoCache());
            newBuilder.setRepoTag(oldBuilder.getRepoTag());
            newBuilder.setRegistry(oldBuilder.getRegistry());
            newBuilder.setServer(oldBuilder.getServer());
            newBuilder.setSkipBuild(oldBuilder.isSkipBuild());
            newBuilder.setSkipDecorate(oldBuilder.isSkipDecorate());
            newBuilder.setSkipPush(oldBuilder.isSkipPush());
            newBuilder.setSkipTagLatest(oldBuilder.isSkipTagLatest());

            return newBuilder;

        }

    }
}
