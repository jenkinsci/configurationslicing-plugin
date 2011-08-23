package configurationslicing;

import hudson.model.AbstractProject;
import hudson.triggers.Trigger;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jvnet.hudson.test.HudsonTestCase;

import configurationslicing.timer.AbstractTimerSliceSpec;
import configurationslicing.timer.SCMTimerSliceStringSlicer;
import configurationslicing.timer.TimerSliceStringSlicer;

public class TimerSliceStringSlicerTest extends HudsonTestCase {
	
	public void testTimerSliceStringSlicer() throws Exception {
		TimerSliceStringSlicer slicer = new TimerSliceStringSlicer();
		TimerSliceStringSlicer.TimerSliceSpec spec = new TimerSliceStringSlicer.TimerSliceSpec();
		doTestTimerSliceStringSlicer(slicer, spec);
	}

	public void testSCMTimerSliceStringSlicer() throws Exception {
		SCMTimerSliceStringSlicer slicer = new SCMTimerSliceStringSlicer();
		SCMTimerSliceStringSlicer.SCMTimerSliceSpec spec = new SCMTimerSliceStringSlicer.SCMTimerSliceSpec();
		doTestTimerSliceStringSlicer(slicer, spec);
	}

	/**
	 * Show before/after fixing chron spec splitting behavior.
	 * This is to test the slicer itself.  More detailed testing is in ChrinSplittingTest.
	 */
	@SuppressWarnings({ "unchecked" })
	private void doTestTimerSliceStringSlicer(UnorderedStringSlicer slicer, AbstractTimerSliceSpec spec) throws Exception {
		UnorderedStringSlice slice = new UnorderedStringSlice(spec);
		
		assertEquals(0, getRealValues(slice).size());

		accumulate(slicer, slice, spec, "p1", "30 * * * *");
		assertEquals(1, getRealValues(slice).size());

		// no additional configured values are added, because this is a duplicate
		accumulate(slicer, slice, spec, "p1a", "30 * * * *");
		assertEquals(1, getRealValues(slice).size());

		// only one additional value is added because the comment is not split out
		accumulate(slicer, slice, spec, "p2", "#this is my spec\n0 * * * *");
		assertEquals(2, getRealValues(slice).size());

		slice = new UnorderedStringSlice(spec);
		AbstractProject project = 
			accumulate(slicer, slice, spec, "p3", "\n\n#comment1\n\n#line2 of comment 1\n\n0 * * * *\n\n\n#comment2\n\n20 * * * *");
		assertEquals(2, getRealValues(slice).size());
		
		assertEquals(1, project.getTriggers().size());
		Set<String> set = new LinkedHashSet<String>(getRealValues(slice));
		spec.setValues(project, set);
		assertEquals(1, project.getTriggers().size());
		Trigger timer = project.getTrigger(spec.getTriggerClass());
		String specString = timer.getSpec();
		assertEquals("#comment1\n#line2 of comment 1\n0 * * * *\n\n#comment2\n20 * * * *", specString);
	}
	@SuppressWarnings("unchecked")
	private List<String> getRealValues(UnorderedStringSlice slice) {
		List<String> list = slice.getConfiguredValues();
		list.remove("");
		list.remove(AbstractTimerSliceSpec.DISABLED);
		return list;
	}
	@SuppressWarnings("unchecked")
	private AbstractProject accumulate(UnorderedStringSlicer slicer, UnorderedStringSlice slice, AbstractTimerSliceSpec spec,
			String name, String chron) throws Exception {
		AbstractProject project = createFreeStyleProject(name);
		Trigger trigger = spec.newTrigger(chron);
		project.addTrigger(trigger);
		slicer.accumulate(slice, project);
		return project;
	}
	
}
