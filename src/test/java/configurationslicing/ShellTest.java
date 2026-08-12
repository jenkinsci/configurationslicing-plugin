package configurationslicing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import configurationslicing.executeshell.ExecuteShellSlicer;
import configurationslicing.executeshell.ExecuteShellUnstableReturnSlicer;
import hudson.matrix.AxisList;
import hudson.matrix.MatrixConfiguration;
import hudson.matrix.MatrixProject;
import hudson.matrix.TextAxis;
import hudson.model.AbstractProject;
import hudson.model.Project;
import hudson.tasks.Shell;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class ShellTest {

    private JenkinsRule r;

    @BeforeEach
    void setUp(JenkinsRule rule) {
        r = rule;
    }

    @Test
    void testGetMultipleShells() throws Exception {
        ExecuteShellSlicer.ExecuteShellSliceSpec spec = new ExecuteShellSlicer.ExecuteShellSliceSpec();

        String command1 = "foo";
        String command2 = "bar";

        Project project = createProject("shell-get", command1, command2);

        List<String> values = spec.getValues(project);
        assertEquals(command1, values.get(0));
        assertEquals(command2, values.get(1));
    }

    @Test
    void workDomainExcludesMatrixConfigurations() throws Exception {
        MatrixProject matrix = r.jenkins.createProject(MatrixProject.class, "FoxtrotLauncher");
        matrix.setAxes(new AxisList(
                new TextAxis("Env", "testenv"),
                new TextAxis("Group", "tools", "alarms", "api")));
        matrix.getBuildersList().add(new Shell("echo matrix"));
        matrix.save();

        Project freestyle = createProject("standalone", "echo freestyle");

        assertFalse(matrix.getItems().isEmpty(), "matrix configurations should exist");

        assertWorkDomainExcludesConfigurations(new ExecuteShellSlicer.ExecuteShellSliceSpec(), matrix, freestyle);
        assertWorkDomainExcludesConfigurations(
                new ExecuteShellUnstableReturnSlicer.ExecuteShellUnstableReturnSliceSpec(), matrix, freestyle);
    }

    private void assertWorkDomainExcludesConfigurations(
            UnorderedStringSlicer.UnorderedStringSlicerSpec<AbstractProject> spec,
            MatrixProject matrix,
            Project freestyle) {
        List<AbstractProject> domain = spec.getWorkDomain();

        assertTrue(domain.contains(matrix), "matrix parent job should be in work domain");
        assertTrue(domain.contains(freestyle), "freestyle job should be in work domain");
        for (MatrixConfiguration configuration : matrix.getItems()) {
            assertFalse(
                    domain.contains(configuration),
                    () -> "matrix configuration should not be in work domain: " + configuration.getFullName());
        }
    }

    @Test
    void testSetMultipleShells() throws Exception {
        int count = 0;
        doTestSetMultipleShells("shell-" + (count++), new String[] {"a", "b"}, new String[] {"c", "d", "e"});
        doTestSetMultipleShells("shell-" + (count++), new String[] {"a", "b"}, new String[] {"a", "e", "b"});
        doTestSetMultipleShells("shell-" + (count++), new String[] {"a", "b", "c"}, new String[] {"a", "c"});
        doTestSetMultipleShells("shell-" + (count++), new String[] {"a", "b", "c"}, new String[] {"a", "", "c"});
        doTestSetMultipleShells("shell-" + (count++), new String[] {"a", "b", "c"}, new String[] {""});
        doTestSetMultipleShells("shell-" + (count++), new String[] {"a", "b", "c"}, new String[] {"", "d", "", "e"});
        doTestSetMultipleShells("shell-" + (count++), new String[] {"a", "b", "c"}, new String[] {"c", "b", "a"});
        doTestSetMultipleShells("shell-" + (count++), new String[] {}, new String[] {"a", "b"});
    }

    public void doTestSetMultipleShells(String name, String[] oldCommands, String[] newCommands) throws Exception {
        ExecuteShellSlicer.ExecuteShellSliceSpec spec = new ExecuteShellSlicer.ExecuteShellSliceSpec();

        Project project = createProject(name, oldCommands);

        // smoke test that the create worked
        List<String> oldValues = spec.getValues(project);
        for (int i = 0; i < oldCommands.length; i++) {
            assertEquals(oldCommands[i], oldValues.get(i));
        }

        List<String> newShells = Arrays.asList(newCommands);
        spec.setValues(project, newShells);

        List<String> newCommandsClean = new ArrayList<>();
        for (String newCommand : newCommands) {
            if (!"".equals(newCommand)) {
                newCommandsClean.add(newCommand);
            }
        }
        if (newCommandsClean.isEmpty()) {
            newCommandsClean.add(ExecuteShellSlicer.ExecuteShellSliceSpec.NOTHING);
        }

        List<String> newValues = spec.getValues(project);
        assertEquals(newCommandsClean.size(), newValues.size());
        for (int i = 0; i < newCommandsClean.size(); i++) {
            assertEquals(newCommandsClean.get(i), newValues.get(i));
        }
    }

    @SuppressWarnings("unchecked")
    private Project createProject(String name, String... shells) throws Exception {
        Project project = r.createFreeStyleProject(name);
        for (String shell : shells) {
            project.getBuildersList().add(new Shell(shell));
        }
        return project;
    }

    @SuppressWarnings("unchecked")
    @Test
    void testNoBracketNames() {
        ExecuteShellSlicer.ExecuteShellSliceSpec spec = new ExecuteShellSlicer.ExecuteShellSliceSpec();

        String v1 = "v1";
        List<String> configurationValues = new ArrayList<>();
        configurationValues.add(v1);

        String n1 = "n1";
        List<String> itemNames = new ArrayList<>();
        itemNames.add(n1);

        UnorderedStringSlice slice = new UnorderedStringSlice(spec, configurationValues, itemNames);

        List<String> values = slice.get(n1);
        assertEquals(1, values.size());
        assertEquals(v1, values.get(0));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testBracketNames() throws Exception {

        r.createFreeStyleProject("a");
        r.createFreeStyleProject("b");
        r.createFreeStyleProject("c");

        ExecuteShellSlicer.ExecuteShellSliceSpec spec = new ExecuteShellSlicer.ExecuteShellSliceSpec();

        String v1 = "v1";
        String v2 = "v2";
        List<String> configurationValues = new ArrayList<>();
        configurationValues.add(v1);
        configurationValues.add(v2);

        List<String> itemNames = new ArrayList<>();
        itemNames.add("a[1]\nb[2]");
        itemNames.add("a[0]\nc[4]");

        UnorderedStringSlice slice = new UnorderedStringSlice(spec, configurationValues, itemNames);

        assertEquals(2, slice.get("a").size());
        assertEquals(3, slice.get("b").size());
        assertEquals(5, slice.get("c").size());
    }
}
