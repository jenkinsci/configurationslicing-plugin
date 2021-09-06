[[configurationslicing-plugin]]
= configurationslicing-plugin
:toc: macro
:toc-title:
ifdef::env-github[]
:tip-caption: :bulb:
:note-caption: :information_source:
:important-caption: :heavy_exclamation_mark:
:caution-caption: :fire:
:warning-caption: :warning:
endif::[]

image:https://img.shields.io/jenkins/plugin/v/configurationslicing.svg[Jenkins Plugin,link=https://plugins.jenkins.io/configurationslicing]
image:https://img.shields.io/github/release/jenkinsci/configurationslicing-plugin.svg?label=release[GitHub release,link=https://github.com/jenkinsci/configurationslicing-plugin/releases/latest]
image:https://img.shields.io/jenkins/plugin/i/configurationslicing.svg?color=blue[Jenkins Plugin Installs,link=https://plugins.jenkins.io/configurationslicing]
image:https://ci.jenkins.io/job/Plugins/job/configurationslicing-plugin/job/devel/badge/icon[Build Status,link=https://ci.jenkins.io/job/Plugins/job/configurationslicing-plugin/job/devel/]

toc::[]

== Plugin information

View Configuration Slicing https://plugins.jenkins.io/configurationslicing[on the plugin site] for more information.

Perform mass configuration of select project properties, including
email, timer, discard old builds, and Maven configuration.It has a
framework to make it very easy to add a configuration page for a new
property.  At present, two types of data can be mass-configured:
booleans and strings.

The plugin shows up in Jenkins' UI on the Manage Jenkins page - seen
here near the bottom of the page:  +
image:docs/images/manage_jenkins.png[]

The main page of the configuration slicing plugin shows all the
properties that can be sliced - select one and you are presented with a
screen showing how that value is set across the entire Jenkins instance.
Many properties on Jenkins projects are useful to set this way, but the
configuration slicing plugin can handle properties on any collection,
such as slaves, or builds of a project.

*The following functions are supported*

* https://plugins.jenkins.io/ant/[Ant version per project]
* Block Build when Downstream Building Slicer (bool)
* Block Build when Upstream Building Slicer (bool)
* https://plugins.jenkins.io/build-timeout/[Build Timeout]
(does not support all features)
* Custom Workspace Slicer (Advanced Project Options > Use custom
workspace)
* Discard Old Builds Slicer - Days to keep artifacts
* Discard Old Builds Slicer - Days to keep builds
* Discard Old Builds Slicer - Max # of builds to keep
* Discard Old Builds Slicer - Max # of builds to keep with artifacts
* E-mail Notification
* https://plugins.jenkins.io/email-ext/[Editable Email Notification]
(https://issues.jenkins-ci.org/browse/JENKINS-11774[recipient list only])
* https://plugins.jenkins.io/jython/[Execute Jython script]
* https://plugins.jenkins.io/python/[Execute Python script]
* Execute shell slicer
* Execute Windows batch command slicer
* https://plugins.jenkins.io/gradle/[Gradle version per project]
* https://plugins.jenkins.io/groovy/[Groovy version per project]
* JDK per project
* Job Disabled Build Slicer (bool)
* Job Disabled Build Slicer (String)
* https://plugins.jenkins.io/PrioritySorter/[Job Priority Slicer]
* https://plugins.jenkins.io/logfilesizechecker/[Build log file size checker Plugin]
* Maven "top-level" targets
* Maven Goals and Options (Maven project)
* Maven Version (Maven Projects)
* MAVEN_OPTS per Maven project
* Parameters
* Quiet period
* SCM Timer Trigger Slicer
* Tied Label Slicer
* Timer Trigger Slicer
* https://plugins.jenkins.io/timestamper/[Timestamper Slicer]
* https://plugins.jenkins.io/configurationslicing/[Configuration Slicer]

=== Boolean slicing

In the case of booleans, the plugin presents a set of checkboxes and
names.  The user can then adjust that property and save the changes.

image::docs/images/job_disabled_slice.png[]

=== String slicing

Most of the slicing uses a GUI much like the following example.  You can
move the Item Names (i.e. Jobs) around within the boxes on the right to
change which jobs have different settings.  You can also alter the
values on the left to change how jobs are configured.  There will always
be a blank set of boxes added to the bottom to allow you to create a new
setting when you need it.  For most of these screens, a value of
"(Disabled)" will indicate that those jobs do not use this configuration
at all.  To disable jobs (e.g. for SCM Polling), move those job names
into that "(Disabled)" box.

image::docs/images/scm_timer_slice.png[]

=== String slicing multiple values

Some slicers allow you to configure multiple values at a time. In those
cases, the values are separated by a comma, and follow the given
example.

image::docs/images/string_multiple_values.png[]

=== String slicing multiple Builders

For the Windows batch builder, Shell builders, and "Top-level Maven
targets", a job can have multiple builders of each type. To configure
jobs like this, you will be presented with an index next to the jobs
names like "MyJob[0]" and "MyJob[1]". The index indicates which
instance of the builder you are configuring.

image::docs/images/string_slicing_multiple_builders.png[]

This is available under these links

* Execute shell slicer
* Execute Windows batch command slicer
* Maven "top-level" targets

=== Configuring parameters across multiple jobs

Job Parameters (aka "This build is parameterized") can be configured
across multiple jobs at one time through the "Parameters" link. To
indicate which parameter you are configuring, note the
"JobName[ParameterName]" syntax.

image:docs/images/parameters_slicing_items.png[]
image:docs/images/parameters_slicing_string_parameters.png[]

=== Slicing by View

If you have many jobs, it can be difficult to perform the configuration
slicing. To make it more granular, you can configure just the jobs
within one view. Assuming you have organized your Jenkins installation
to have useful views, this will allow you to configure jobs at the right
granularity. To use this feature, first select the type of configuration
(in this example "Custom Workspace") and then you will be given a list
of views to choose from. You don't have to choose a view, as the default
is to show all jobs. If you select one of the views on the left, your
list of jobs is filtered down to just the jobs in that view.

image::docs/images/views.png[]

=== Email Notifications and https://plugins.jenkins.io/email-ext/[Editable Email Notifications] (from 1.41 on)

When editing recipient lists, Email notifications are only completely
disabled when set to (Disabled).

Setting a empty recipient list leaves existing email notifications to
committers (Checkbox "Notify individuals who broke the build") in place.
