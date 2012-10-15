package configurationslicing.wscleanup;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.BuildableItemWithBuildWrappers;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.plugins.ws_cleanup.PreBuildCleanup;
import hudson.tasks.BuildWrapper;
import hudson.util.DescribableList;

import java.util.List;

import configurationslicing.UnorderedStringSlicer;

@Extension
public class CleanupBeforeSlicer extends UnorderedStringSlicer<AbstractProject<?, ?>> {

	public CleanupBeforeSlicer() {
		super(new CleanupBeforeSliceSpec());
	}

	public static class CleanupBeforeSliceSpec extends AbstractWsCleanupSliceSpec {

		public CleanupBeforeSliceSpec() {
			super("wscleanupbefore", "Delete workspace before build starts");
		}

		@Override
		public CleanupInfo getCleanupInfo(AbstractProject<?, ?> item) {
			CleanupInfo info = new CleanupInfo();
			
			BuildableItemWithBuildWrappers bi = (BuildableItemWithBuildWrappers) item;
			DescribableList<BuildWrapper,Descriptor<BuildWrapper>> wrappers = bi.getBuildWrappersList();
			boolean changed = false;
			PreBuildCleanup wrapper = wrappers.get(PreBuildCleanup.class);
			
			if (changed) {
				
			}
			
			return info;
		}

		@Override
		public boolean setCleanupInfo(AbstractProject<?, ?> item, CleanupInfo info) {
			return false;
		}
		@Override
		public boolean isSkipEnabled() {
			// we do not allow skip on fail in the cleanup before
			return false;
		}
		@SuppressWarnings("unchecked")
		@Override
		public List<AbstractProject<?, ?>> getWorkDomain() {
            return (List) Hudson.getInstance().getItems(BuildableItemWithBuildWrappers.class);
		}
	
	}
	
}
