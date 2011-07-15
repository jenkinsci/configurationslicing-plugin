package configurationslicing;

import static configurationslicing.timer.AbstractTimerSliceSpec.joinChronSpec;
import static configurationslicing.timer.AbstractTimerSliceSpec.splitChronSpec;

import java.util.List;

import junit.framework.TestCase;

public class ChronSplittingTest extends TestCase {

	public void testNoSplit() {
		String noSplit = "0 * * * *";
		List<String> lines = splitChronSpec(noSplit);
		assertEquals(1, lines.size());
		
		String join = joinChronSpec(lines);
		assertEquals(noSplit, join);
	}
	public void testSimpleSplit() {
		String line1 = "#comment 1\n0 * * * *";
		String line2 = "#comment 2\n10 * * * *";
		String simpleSplit = line1 + "\n\n" + line2;
		List<String> lines = splitChronSpec(simpleSplit);
		assertEquals(2, lines.size());
		assertEquals(line1, lines.get(0));
		assertEquals(line2, lines.get(1));
		
		String join = joinChronSpec(lines);
		assertEquals(simpleSplit, join);
	}
	public void testComplexSplit() {
		String complexSplit = "\n#comment 1\n0 * * * *\n15 * * * *\n#something\n#comment 2\n\n10 * * * *\n\n";
		List<String> lines = splitChronSpec(complexSplit);
		assertEquals(3, lines.size());
		assertEquals("#comment 1\n0 * * * *", lines.get(0));
		assertEquals("15 * * * *", lines.get(1));
		assertEquals("#something\n#comment 2\n10 * * * *", lines.get(2));
		
		String join = joinChronSpec(lines);
		String normalized = "#comment 1\n0 * * * *\n\n15 * * * *\n\n#something\n#comment 2\n10 * * * *";
		assertEquals(normalized, join);
	}
	
}
