package configurationslicing.wscleanup;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.BuildableItemWithBuildWrappers;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.plugins.ws_cleanup.PreBuildCleanup;
import hudson.tasks.BuildWrapper;
import hudson.util.DescribableList;

import java.io.IOException;
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
			BuildableItemWithBuildWrappers bi = (BuildableItemWithBuildWrappers) item;
			DescribableList<BuildWrapper,Descriptor<BuildWrapper>> wrappers = bi.getBuildWrappersList();
			PreBuildCleanup wrapper = wrappers.get(PreBuildCleanup.class);

			if (wrapper == null) {
				return null;
			}
			
			CleanupInfo info = new CleanupInfo();
			info.appliesToDirectories = wrapper.getDeleteDirs();
			info.skipWhenFailed = false; // is N/A
			info.patterns = wrapper.getPatterns();
			
			return info;
		}

		@Override
		public boolean setCleanupInfo(AbstractProject<?, ?> item, CleanupInfo info) {
			BuildableItemWithBuildWrappers bi = (BuildableItemWithBuildWrappers) item;
			DescribableList<BuildWrapper,Descriptor<BuildWrapper>> wrappers = bi.getBuildWrappersList();
			PreBuildCleanup oldWrapper = wrappers.get(PreBuildCleanup.class);
			
			if (info == null) {
				if (oldWrapper == null) {
					return false;
				} else {
					try {
						wrappers.remove(oldWrapper);
						return true;
					} catch (IOException e) {
						return false;
					}
				}
			} else {
				PreBuildCleanup newWrapper = new PreBuildCleanup(info.patterns, info.appliesToDirectories);
				if (oldWrapper == null) {
					try {
						wrappers.add(newWrapper);
						return true;
					} catch (IOException e) {
						return false;
					}
				} else {
					CleanupInfo oldInfo = getCleanupInfo(item);
					if (oldInfo.equals(info)) {
						return false;
					} else {
						try {
							wrappers.replace(newWrapper);
							return true;
						} catch (IOException e) {
							return false;
						}
					}
				}
			}
		}
		
		@Override
		public boolean isSkipEnabled() {
			// we do not allow skip on fail in the cleanup before
			return false;
		}
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public List<AbstractProject<?, ?>> getWorkDomain() {
            return (List) Hudson.getInstance().getAllItems(BuildableItemWithBuildWrappers.class);
		}
	
	}
	
}
