package configurationslicing.email;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import hudson.Extension;
import hudson.plugins.emailext.EmailType;
import hudson.plugins.emailext.ExtendedEmailPublisher;
import hudson.plugins.emailext.plugins.trigger.FailureTrigger;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.tasks.Publisher;
import hudson.util.DescribableList;
import configurationslicing.UnorderedStringSlicer;

@Extension
public class ExtEmailSlicer extends	UnorderedStringSlicer<AbstractProject<?, ?>> {

	public ExtEmailSlicer() {
		super(new ExtEmailSliceSpec());
	}
    public boolean isLoaded() {
    	try {
    		new EmailType();
    		return true;
    	} catch (Throwable t) {
    		return false;
    	}
    }

	@SuppressWarnings("unchecked")
	public static class ExtEmailSliceSpec extends AbstractEmailSliceSpec implements ProjectHandler {

		public ExtEmailSliceSpec() {
			super(",", "Editable Email Notification", "emailext");
		}
		
		@Override
		protected ProjectHandler getProjectHandler(AbstractProject project) {
			return this;
		}
		@Override
		public List<String> getCommonValueStrings() {
			List<String> values = new ArrayList<String>();
			values.add("$DEFAULT_RECIPIENTS");
			return values;
		}
		public String getRecipients(AbstractProject project) {
			ExtendedEmailPublisher mailer = getMailer(project);
			if (mailer != null) {
				return mailer.recipientList;
			} else {
				return null;
			}
		}
		private ExtendedEmailPublisher getMailer(AbstractProject project) {
			DescribableList<Publisher,Descriptor<Publisher>> publishers = project.getPublishersList();
			Descriptor<Publisher> descriptor = Hudson.getInstance().getDescriptor(ExtendedEmailPublisher.class);
			Publisher emailPublisher = publishers.get(descriptor);
			return (ExtendedEmailPublisher) emailPublisher;
		}
		public boolean setRecipients(AbstractProject project, String value) {
			ExtendedEmailPublisher mailer = getMailer(project);
			if (!StringUtils.equals(value, mailer.recipientList)) {
				mailer.recipientList = value;
				return true;
			} else {
				return false;
			}
		}
		public boolean addMailer(AbstractProject project) throws IOException {
			ExtendedEmailPublisher mailer = getMailer(project);
			if (mailer == null) {
				DescribableList<Publisher,Descriptor<Publisher>> publishers = project.getPublishersList();
				ExtendedEmailPublisher publisher = new ExtendedEmailPublisher();
				FailureTrigger trigger = new FailureTrigger();
				EmailType email = new EmailType();
				email.setSendToDevelopers(true);
				email.setSendToRecipientList(true);
				trigger.setEmail(email);
				publisher.getConfiguredTriggers().add(trigger);
				publishers.add(publisher);
				return true;
			} else {
				return false;
			}
		}
		public boolean removeMailer(AbstractProject project) throws IOException {
			ExtendedEmailPublisher mailer = getMailer(project);
			if (mailer != null) {
				DescribableList<Publisher,Descriptor<Publisher>> publishers = project.getPublishersList();
				publishers.remove(mailer);
				return true;
			} else {
				return false;
			}
		}
	}
	
}
