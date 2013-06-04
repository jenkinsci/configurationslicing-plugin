package configurationslicing.wscleanup;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.plugins.ws_cleanup.WsCleanup;
import hudson.tasks.Publisher;
import hudson.util.DescribableList;

import java.io.IOException;
import java.util.List;

import configurationslicing.UnorderedStringSlicer;

@Extension
public class CleanupAfterSlicer extends UnorderedStringSlicer<AbstractProject<?, ?>> {

	public CleanupAfterSlicer() {
		super(new CleanupAfterSliceSpec());
	}

	public static class CleanupAfterSliceSpec extends AbstractWsCleanupSliceSpec {

		public CleanupAfterSliceSpec() {
			super("wscleanupafter", "Delete workspace when build is done");
		}

		@Override
		public CleanupInfo getCleanupInfo(AbstractProject<?, ?> item) {
			DescribableList<Publisher,Descriptor<Publisher>> publishers = item.getPublishersList();
			WsCleanup publisher = publishers.get(WsCleanup.class);
			if (publisher == null) {
				return null;
			}
			
			CleanupInfo info = new CleanupInfo();
			info.appliesToDirectories = publisher.getDeleteDirs();
			info.skipWhenFailed = publisher.getSkipWhenFailed();
			info.patterns = publisher.getPatterns();
			
			return info;
		}

		@Override
		public boolean setCleanupInfo(AbstractProject<?, ?> item, CleanupInfo info) {
			DescribableList<Publisher,Descriptor<Publisher>> publishers = item.getPublishersList();
			WsCleanup oldPublisher = publishers.get(WsCleanup.class);
			
			if (info == null) {
				if (oldPublisher == null) {
					return false;
				} else {
					try {
						publishers.remove(oldPublisher);
						return true;
					} catch (IOException e) {
						return false;
					}
				}
			} else {
				WsCleanup newPublisher = new WsCleanup(info.patterns, info.appliesToDirectories, info.skipWhenFailed);
				if (oldPublisher == null) {
					try {
						publishers.add(newPublisher);
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
							publishers.replace(newPublisher);
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
			return true;
		}
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public List<AbstractProject<?, ?>> getWorkDomain() {
            return (List) Hudson.getInstance().getAllItems(AbstractProject.class);
		}
	
	}
	
}
