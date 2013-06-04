package configurationslicing;

import hudson.model.Project;
import hudson.tasks.Shell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jvnet.hudson.test.HudsonTestCase;

import configurationslicing.executeshell.ExecuteShellSlicer;

public class ShellTest extends HudsonTestCase {

	@SuppressWarnings("unchecked")
	public void testGetMultipleShells() throws Exception {
		ExecuteShellSlicer.ExecuteShellSliceSpec spec = new ExecuteShellSlicer.ExecuteShellSliceSpec();
		
		String command1 = "foo";
		String command2 = "bar";
		
		Project project = createProject("shell-get", command1, command2);
		
		List<String> values = spec.getValues(project);
		assertEquals(command1, values.get(0));
		assertEquals(command2, values.get(1));
	}
	
	public void testSetMultipleShells() throws Exception {
		int count = 0;
		doTestSetMultipleShells("shell-" + (count++), new String[] { "a", "b" }, new String[] { "c", "d", "e"});
		doTestSetMultipleShells("shell-" + (count++), new String[] { "a", "b" }, new String[] { "a", "e", "b"});
		doTestSetMultipleShells("shell-" + (count++), new String[] { "a", "b", "c" }, new String[] { "a", "c"});
		doTestSetMultipleShells("shell-" + (count++), new String[] { "a", "b", "c" }, new String[] { "a", "", "c"});
		doTestSetMultipleShells("shell-" + (count++), new String[] { "a", "b", "c" }, new String[] { "" });
		doTestSetMultipleShells("shell-" + (count++), new String[] { "a", "b", "c" }, new String[] { "", "d", "", "e" });
		doTestSetMultipleShells("shell-" + (count++), new String[] { "a", "b", "c" }, new String[] { "c", "b", "a" });
		doTestSetMultipleShells("shell-" + (count++), new String[] { }, new String[] { "a", "b" });
	}
	@SuppressWarnings("unchecked")
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
		
		List<String> newCommandsClean = new ArrayList<String>();
		for (int i = 0; i < newCommands.length; i++) {
			if (!"".equals(newCommands[i])) {
				newCommandsClean.add(newCommands[i]);
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
		Project project = createFreeStyleProject(name);
		for (String shell: shells) {
			project.getBuildersList().add(new Shell(shell));
		}
		return project;
	}
	
	@SuppressWarnings("unchecked")
	public void testNoBracketNames() {
		ExecuteShellSlicer.ExecuteShellSliceSpec spec = new ExecuteShellSlicer.ExecuteShellSliceSpec();
		
		String v1 = "v1";
		List<String> configurationValues = new ArrayList<String>();
		configurationValues.add(v1);
		
		String n1 = "n1";
		List<String> itemNames = new ArrayList<String>();
		itemNames.add(n1);
		
		UnorderedStringSlice slice = new UnorderedStringSlice(spec, configurationValues, itemNames);
		
		List<String> values = slice.get(n1);
		assertEquals(1, values.size());
		assertEquals(v1, values.get(0));
	}
	@SuppressWarnings("unchecked")
	public void testBracketNames() throws Exception {
		
		createFreeStyleProject("a");
		createFreeStyleProject("b");
		createFreeStyleProject("c");
		
		ExecuteShellSlicer.ExecuteShellSliceSpec spec = new ExecuteShellSlicer.ExecuteShellSliceSpec();
		
		String v1 = "v1";
		String v2 = "v2";
		List<String> configurationValues = new ArrayList<String>();
		configurationValues.add(v1);
		configurationValues.add(v2);
		
		List<String> itemNames = new ArrayList<String>();
		itemNames.add("a[1]\nb[2]");
		itemNames.add("a[0]\nc[4]");
		
		UnorderedStringSlice slice = new UnorderedStringSlice(spec, configurationValues, itemNames);
		
		assertEquals(2, slice.get("a").size());
		assertEquals(3, slice.get("b").size());
		assertEquals(5, slice.get("c").size());
	}
}
