package configurationslicing.claim;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.plugins.claim.ClaimPublisher;
import hudson.tasks.Publisher;
import hudson.util.DescribableList;

import java.io.IOException;
import java.util.List;

import configurationslicing.BooleanSlicer;

@Extension
public class ClaimSlicer extends BooleanSlicer<AbstractProject<?,?>> {
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
    
    public static class ClaimSpec implements BooleanSlicer.BooleanSlicerSpec<AbstractProject<?,?>>
    {
        public String getName() {
            return "Claim Slicer";
        }

        public String getName(AbstractProject<?,?> item) {
            return item.getFullName();
        }

        public String getUrl() {
            return "claim";
        }

        public boolean getValue(AbstractProject<?,?> item) {
            
            DescribableList<Publisher, Descriptor<Publisher>> publishersList = item.getPublishersList();
            ClaimPublisher claimPublisher = publishersList.get(ClaimPublisher.class);
            return claimPublisher != null;
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        public List<AbstractProject<?,?>> getWorkDomain() {
            return (List)Hudson.getInstance().getAllItems(AbstractProject.class);
        }

        public boolean setValue(AbstractProject<?,?> item, boolean value) {
            boolean oldval = getValue(item);
            if (value == oldval) {
                return true;
            } else if (value==false) { // request to remove the publisher
                DescribableList<Publisher, Descriptor<Publisher>> publishersList = item.getPublishersList();
                ClaimPublisher claimPublisher = publishersList.get(ClaimPublisher.class);
                try {
                    publishersList.remove(claimPublisher);
                } catch (IOException e) {
                    return false;
                }
                return true;
            } else { // request to add the publisher
                DescribableList<Publisher, Descriptor<Publisher>> publishersList = item.getPublishersList();
                try {
                    publishersList.add(new ClaimPublisher());
                } catch (IOException e) {
                    return false;
                }
                return true;
            }
        }
    }
}
