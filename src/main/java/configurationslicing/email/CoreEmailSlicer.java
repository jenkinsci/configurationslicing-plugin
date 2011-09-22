package configurationslicing.email;

import java.io.IOException;

import hudson.Extension;
import hudson.maven.MavenModuleSet;
import hudson.maven.MavenReporter;
import hudson.maven.reporters.MavenMailer;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.tasks.Mailer;
import hudson.tasks.Publisher;
import hudson.util.DescribableList;

import org.apache.commons.lang.StringUtils;

import configurationslicing.UnorderedStringSlicer;

@Extension
public class CoreEmailSlicer extends
		UnorderedStringSlicer<AbstractProject<?, ?>> {

	public CoreEmailSlicer() {
		super(new CoreEmailSliceSpec());
	}

	@SuppressWarnings("unchecked")
	public static class CoreEmailSliceSpec extends AbstractEmailSliceSpec {

		public CoreEmailSliceSpec() {
			super(" ", "E-mail Notification", "mailer");
		}
		@Override
		protected ProjectHandler getProjectHandler(AbstractProject project) {
			if (project instanceof MavenModuleSet) {
				return MavenEmailProjectHandler.INSTANCE;
			} else {
				return CoreEmailProjectHandler.INSTANCE;
			}
		}
	}
	@SuppressWarnings("unchecked")
	private static class CoreEmailProjectHandler implements ProjectHandler {
		public static final CoreEmailProjectHandler INSTANCE = new CoreEmailProjectHandler();
		public String getRecipients(AbstractProject project) {
			Mailer mailer = getMailer(project);
			if (mailer != null) {
				return mailer.recipients;
			} else {
				return null;
			}
		}
		private Mailer getMailer(AbstractProject project) {
			DescribableList<Publisher,Descriptor<Publisher>> publishers = project.getPublishersList();
			Descriptor<Publisher> descriptor = Hudson.getInstance().getDescriptor(Mailer.class);
			Publisher emailPublisher = publishers.get(descriptor);
			return (Mailer) emailPublisher;
		}
		public boolean setRecipients(AbstractProject project, String value) {
			Mailer mailer = getMailer(project);
			if (!StringUtils.equals(value, mailer.recipients)) {
				mailer.recipients = value;
				return true;
			} else {
				return false;
			}
		}
		public boolean addMailer(AbstractProject project) throws IOException {
			Mailer mailer = getMailer(project);
			if (mailer == null) {
				DescribableList<Publisher,Descriptor<Publisher>> publishers = project.getPublishersList();
				publishers.add(new Mailer());
				return true;
			} else {
				return false;
			}
		}
		public boolean removeMailer(AbstractProject project) throws IOException {
			Mailer mailer = getMailer(project);
			if (mailer != null) {
				DescribableList<Publisher,Descriptor<Publisher>> publishers = project.getPublishersList();
				publishers.remove(mailer);
				return true;
			} else {
				return false;
			}
		}
	}
	@SuppressWarnings("unchecked")
	private static class MavenEmailProjectHandler implements ProjectHandler {
		public static final MavenEmailProjectHandler INSTANCE = new MavenEmailProjectHandler();
		public String getRecipients(AbstractProject project) {
			MavenMailer mailer = getMailer(project);
			if (mailer != null) {
				return mailer.recipients;
			} else {
				return null;
			}
		}
		private MavenMailer getMailer(AbstractProject project) {
			MavenModuleSet mavenProject = (MavenModuleSet) project;
			DescribableList<MavenReporter,Descriptor<MavenReporter>> reporters = mavenProject.getReporters();
			Descriptor<MavenReporter> descriptor = Hudson.getInstance().getDescriptor(MavenMailer.class);
			MavenReporter emailReporter = reporters.get(descriptor);
			return (MavenMailer) emailReporter;
		}
		public boolean setRecipients(AbstractProject project, String value) {
			MavenMailer mailer = getMailer(project);
			if (!StringUtils.equals(value, mailer.recipients)) {
				mailer.recipients = value;
				return true;
			} else {
				return false;
			}
		}
		public boolean addMailer(AbstractProject project) throws IOException {
			MavenMailer mailer = getMailer(project);
			if (mailer == null) {
				MavenModuleSet mavenProject = (MavenModuleSet) project;
				DescribableList<MavenReporter,Descriptor<MavenReporter>> reporters = mavenProject.getReporters();
				reporters.add(new MavenMailer());
				return true;
			} else {
				return false;
			}
		}
		public boolean removeMailer(AbstractProject project) throws IOException {
			MavenMailer mailer = getMailer(project);
			if (mailer != null) {
				MavenModuleSet mavenProject = (MavenModuleSet) project;
				DescribableList<MavenReporter,Descriptor<MavenReporter>> reporters = mavenProject.getReporters();
				reporters.remove(mailer);
				return true;
			} else {
				return false;
			}
		}
	}
	
}
