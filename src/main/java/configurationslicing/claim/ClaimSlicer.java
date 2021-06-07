package configurationslicing.claim;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.plugins.claim.ClaimPublisher;
import hudson.tasks.Publisher;
import hudson.util.DescribableList;

import java.util.List;

import configurationslicing.BooleanSlicer;
import configurationslicing.TopLevelItemSelector;

@Extension(optional = true)
public class ClaimSlicer extends BooleanSlicer<AbstractProject> {
    public ClaimSlicer() {
        super(new ClaimSpec());
    }

    public boolean isLoaded() {
        try {
            new ClaimPublisher();
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    public static class ClaimSpec implements BooleanSlicer.BooleanSlicerSpec<AbstractProject>
    {
        public String getName() {
            return "Claim Slicer";
        }

        public String getName(AbstractProject item) {
            return item.getFullName();
        }

        public String getUrl() {
            return "claim";
        }

        public boolean getValue(AbstractProject item) {

            DescribableList<Publisher, Descriptor<Publisher>> publishersList = item.getPublishersList();
            ClaimPublisher claimPublisher = publishersList.get(ClaimPublisher.class);
            return claimPublisher != null;
        }

        public List<AbstractProject> getWorkDomain() {
            return TopLevelItemSelector.getAllTopLevelItems(AbstractProject.class);
        }

        public boolean setValue(AbstractProject item, boolean value) {
            boolean oldval = getValue(item);
            if (value == oldval) {
                return true;
            } else if (value==false) { // request to remove the publisher
                DescribableList<Publisher, Descriptor<Publisher>> publishersList = item.getPublishersList();
                ClaimPublisher claimPublisher = publishersList.get(ClaimPublisher.class);
                publishersList.remove(claimPublisher);
                return true;
            } else { // request to add the publisher
                DescribableList<Publisher, Descriptor<Publisher>> publishersList = item.getPublishersList();
                publishersList.add(new ClaimPublisher());
                return true;
            }
        }
    }
}
