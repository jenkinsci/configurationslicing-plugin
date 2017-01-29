package configurationslicing;

import java.util.List;

import com.cloudbees.dockerpublish.DockerBuilder;
import com.google.common.collect.Lists;
import configurationslicing.docker.DockerFilePathSlicer;
import configurationslicing.docker.DockerRegistryURLSlicer;
import configurationslicing.docker.DockerRepoNameSlicer;
import configurationslicing.docker.DockerRepoTagSlicer;
import hudson.model.Project;
import org.jenkinsci.plugins.docker.commons.credentials.DockerRegistryEndpoint;
import org.jvnet.hudson.test.HudsonTestCase;

public class DockerSlicerTest extends HudsonTestCase {

    private String dockerfilePath = "test/file/path/";
    private String registryUrl = "test/url";
    private String credentials = "credId";
    private DockerRegistryEndpoint dockerRegistry = new DockerRegistryEndpoint(registryUrl, credentials);
    private String repoName = "testRepoName";
    private String repoTag = "testRepoTag";

    @SuppressWarnings("unchecked")
    public void testSetDockerfilePath() throws Exception {
        DockerFilePathSlicer.DockerSlicerSpec spec = new DockerFilePathSlicer.DockerSlicerSpec();
        Project project = createProject();
        String updatedFilePath = "new/file/path";

        assertEquals(Lists.newArrayList(dockerfilePath), spec.getValues(project));

        spec.setValues(project, Lists.newArrayList(updatedFilePath));

        List<String> values = spec.getValues(project);
        assert(values.contains(updatedFilePath));
        assert(!values.contains(dockerfilePath));

    }

    @SuppressWarnings("unchecked")
    public void testSetRegistryUrl() throws Exception {
        DockerRegistryURLSlicer.DockerSlicerSpec spec = new DockerRegistryURLSlicer.DockerSlicerSpec();
        Project project = createProject();
        String updatedRegistryUrl = "new/url";

        assertEquals(Lists.newArrayList(dockerRegistry.getUrl()), spec.getValues(project));

        spec.setValues(project, Lists.newArrayList(updatedRegistryUrl));

        List<String> values = spec.getValues(project);
        assert(values.contains(updatedRegistryUrl));
        assert(!values.contains(registryUrl));
    }

    @SuppressWarnings("unchecked")
    public void testSetRepoName() throws Exception {
        DockerRepoNameSlicer.DockerSlicerSpec spec = new DockerRepoNameSlicer.DockerSlicerSpec();
        Project project = createProject();
        String updatedRepoName = "newRepoName";

        assertEquals(Lists.newArrayList(repoName), spec.getValues(project));

        spec.setValues(project, Lists.newArrayList(updatedRepoName));

        List<String> values = spec.getValues(project);
        assert(values.contains(updatedRepoName));
        assert(!values.contains(repoName));
    }

    @SuppressWarnings("unchecked")
    public void testSetRepoTag() throws Exception {
        DockerRepoTagSlicer.DockerSlicerSpec spec = new DockerRepoTagSlicer.DockerSlicerSpec();
        Project project = createProject();
        String updatedRepoTag = "newRepoTag";

        assertEquals(Lists.newArrayList(repoTag), spec.getValues(project));

        spec.setValues(project, Lists.newArrayList(updatedRepoTag));

        List<String> values = spec.getValues(project);
        assert(values.contains(updatedRepoTag));
        assert(!values.contains(repoTag));
    }

    @SuppressWarnings("unchecked")
    private Project createProject() throws Exception {
        Project project = createFreeStyleProject();
        project.getBuildersList().add(getDockerBuilder());
        return project;
    }

    private DockerBuilder getDockerBuilder() {
        DockerBuilder dockerBuilder = new DockerBuilder(repoName);
        dockerBuilder.setDockerfilePath(dockerfilePath);
        dockerBuilder.setRegistry(dockerRegistry);
        dockerBuilder.setRepoTag(repoTag);

        return dockerBuilder;
    }
}
