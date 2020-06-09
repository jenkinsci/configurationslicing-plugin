Configuration Slicing plugin for Jenkins
========================================

[![Jenkins Plugin](https://img.shields.io/jenkins/plugin/v/configurationslicing.svg)](https://plugins.jenkins.io/configurationslicing)
[![GitHub release](https://img.shields.io/github/release/jenkinsci/configurationslicing-plugin.svg?label=release)](https://github.com/jenkinsci/configurationslicing-plugin/releases/latest)
[![Jenkins Plugin Installs](https://img.shields.io/jenkins/plugin/i/configurationslicing.svg?color=blue)](https://plugins.jenkins.io/configurationslicing)
| Plugin Information                                                                                                     |
|------------------------------------------------------------------------------------------------------------------------|
| View Configuration Slicing [on the plugin site](https://plugins.jenkins.io/configurationslicing) for more information. |

Perform mass configuration of select project properties, including
email, timer, discard old builds, and Maven configuration.It has a
framework to make it very easy to add a configuration page for a new
property.  At present, two types of data can be mass-configured:
booleans and strings.  

The plugin shows up in Jenkins' UI on the Manage Jenkins page - seen
here near the bottom of the page:    
![](https://wiki.jenkins.io/download/attachments/38142123/ConfigurationSlicing%20-%20Manage%20Hudson.png?version=2&modificationDate=1310821741000&api=v2)

The main page of the configuration slicing plugin shows all the
properties that can be sliced - select one and you are presented with a
screen showing how that value is set across the entire Jenkins instance.
Many properties on Jenkins projects are useful to set this way, but the
configuration slicing plugin can handle properties on any collection,
such as slaves, or builds of a project.

**The following functions are supported**

-   [Ant version per
    project](https://wiki.jenkins.io/display/JENKINS/Ant+Plugin)
-   Block Build when Downstream Building Slicer (bool)
-   Block Build when Upstream Building Slicer (bool)
-   [Build
    Timeout](https://wiki.jenkins.io/display/JENKINS/Build-timeout+Plugin)
    (does not support all features)
-   Custom Workspace Slicer (Advanced Project Options \> Use custom
    workspace)
-   Discard Old Builds Slicer - Days to keep artifacts
-   Discard Old Builds Slicer - Days to keep builds
-   Discard Old Builds Slicer - Max \# of builds to keep
-   Discard Old Builds Slicer - Max \# of builds to keep with artifacts
-   E-mail Notification
-   [Editable Email
    Notification](https://wiki.jenkins.io/display/JENKINS/Email-ext+plugin)
    ([recipient list
    only](https://issues.jenkins-ci.org/browse/JENKINS-11774))
-   [Execute Jython
    script](https://wiki.jenkins.io/display/JENKINS/Jython+Plugin)
-   [Execute Python
    script](https://wiki.jenkins.io/display/JENKINS/Python+Plugin)
-   Execute shell slicer
-   Execute Windows batch command slicer
-   [Gradle version per
    project](https://wiki.jenkins.io/display/JENKINS/Gradle+Plugin)
-   [Groovy version per
    project](https://wiki.jenkins.io/display/JENKINS/Groovy+plugin)
-   JDK per project
-   Job Disabled Build Slicer (bool)
-   Job Disabled Build Slicer (String)
-   [Job Priority
    Slicer](https://wiki.jenkins.io/display/JENKINS/Priority+Sorter+Plugin)
-   [Logfilesizechecker
    Plugin](https://wiki.jenkins.io/display/JENKINS/Logfilesizechecker+Plugin)
-   Maven "top-level" targets
-   Maven Goals and Options (Maven project)
-   Maven Version (Maven Projects)
-   MAVEN\_OPTS per Maven project
-   Parameters
-   Quiet period
-   SCM Timer Trigger Slicer
-   Tied Label Slicer
-   Timer Trigger Slicer
-   [Timestamper
    Slicer](https://wiki.jenkins.io/display/JENKINS/Timestamper)
-   [Claim
    Slicer](https://wiki.jenkins.io/display/JENKINS/Configuration+Slicing+Plugin)

### Boolean slicing

In the case of booleans, the plugin presents a set of checkboxes and
names.  The user can then adjust that property and save the changes.

![](https://wiki.jenkins.io/download/attachments/38142123/ConfigurationSlicing%20-%20Job%20Disabled%20Slice.png?version=2&modificationDate=1310822184000&api=v2)

### String slicing

Most of the slicing uses a GUI much like the following example.  You can
move the Item Names (i.e. Jobs) around within the boxes on the right to
change which jobs have different settings.  You can also alter the
values on the left to change how jobs are configured.  There will always
be a blank set of boxes added to the bottom to allow you to create a new
setting when you need it.  For most of these screens, a value of
"(Disabled)" will indicate that those jobs do not use this configuration
at all.  To disable jobs (e.g. for SCM Polling), move those job names
into that "(Disabled)" box.

![](https://wiki.jenkins.io/download/attachments/38142123/ConfigurationSlicing%20-%20SCM%20Timer%20Slice.png?version=2&modificationDate=1310822734000&api=v2)

### String slicing multiple values

Some slicers allow you to configure multiple values at a time. In those
cases, the values are separated by a comma, and follow the given
example.

![](https://wiki.jenkins.io/download/attachments/38142123/ConfigurationSlicing%20-%20string-multiple-values.png?version=1&modificationDate=1322526234000&api=v2)

### String slicing multiple Builders

For the Windows batch builder, Shell builders, and "Top-level Maven
targets", a job can have multiple builders of each type. To configure
jobs like this, you will be presented with an index next to the jobs
names like "MyJob\[0\]" and "MyJob\[1\]". The index indicates which
instance of the builder you are configuring.

![](https://wiki.jenkins.io/download/attachments/38142123/StringSlicingMultipleBuilders.png?version=1&modificationDate=1337433359000&api=v2)

This is available under these links

-   Execute shell slicer
-   Execute Windows batch command slicer
-   Maven "top-level" targets

### Configuring parameters across multiple jobs

Job Parameters (aka "This build is parameterized") can be configured
across multiple jobs at one time through the "Parameters" link. To
indicate which parameter you are configuring, note the
"JobName\[ParameterName\]" syntax.

![](https://wiki.jenkins.io/download/attachments/38142123/ParametersSlicingItems.png?version=1&modificationDate=1337733717000&api=v2)
![](https://wiki.jenkins.io/download/attachments/38142123/ParametersSlicingStringParameter.png?version=1&modificationDate=1337733736000&api=v2)

### Slicing by View

If you have many jobs, it can be difficult to perform the configuration
slicing. To make it more granular, you can configure just the jobs
within one view. Assuming you have organized your Jenkins installation
to have useful views, this will allow you to configure jobs at the right
granularity. To use this feature, first select the type of configuration
(in this example "Custom Workspace") and then you will be given a list
of views to choose from. You don't have to choose a view, as the default
is to show all jobs. If you select one of the views on the left, your
list of jobs is filtered down to just the jobs in that view.

![](https://wiki.jenkins.io/download/attachments/38142123/ConfigurationSlicing%20-%20Views.png?version=1&modificationDate=1318517242000&api=v2)

### Email Notifications and [Editable Email Notifications](https://wiki.jenkins.io/display/JENKINS/Email-ext+plugin) (from 1.41 on)

When editing recipient lists, Email notifications are only completely
disabled when set to (Disabled).

Setting a empty recipient list leaves existing email notifications to
committers (Checkbox "Notify individuals who broke the build") in place.

### Changelog

#### Version 1.41 - May 31, 2015

-   [JENKINS-20319](https://issues.jenkins-ci.org/browse/JENKINS-20319) -
    E-Mail-Configurations are only completely disabled when set to
    (Disabled). Setting a empty recipient leaves e-mail notifications to
    committers in place.
-   Requires jenkins 1.509.3 and email-ext 2.37
-   [JENKINS-21225](https://issues.jenkins-ci.org/browse/JENKINS-21225) -
    Updated Editable email notification to work with email-ext 2.37
-   [JENKINS-21445](https://issues.jenkins-ci.org/browse/JENKINS-21445) -
    bug fix in job disabled build slicer
-   [JENKINS-25964](https://issues.jenkins-ci.org/browse/JENKINS-25964) -
    NPE in some cases when trying to slice parameters

#### Version 1.40 - November 15, 2014

Fix bugs related to selection of maven modules or matrix configurations
instead of top level project items:

-   [JENKINS-18455](https://issues.jenkins-ci.org/browse/JENKINS-18455)
-   [JENKINS-20082](https://issues.jenkins-ci.org/browse/JENKINS-20082)
-   [JENKINS-21556](https://issues.jenkins-ci.org/browse/JENKINS-21556)
-   [JENKINS-23776](https://issues.jenkins-ci.org/browse/JENKINS-23776)

#### Version 1.39 - October 4, 2014

-   [JENKINS-16855](https://issues.jenkins-ci.org/browse/JENKINS-16855) -
    Add support for Claim Slicing
-   [JENKINS-16869](https://issues.jenkins-ci.org/browse/JENKINS-16869) -
    Do not show the TimeStamper slicer if the TimeStamper plugin is not
    installed
-   [JENKINS-23169](https://issues.jenkins-ci.org/browse/JENKINS-23169) -
    NullPointerException with newer Jenkins versions

#### Version 1.38.3 - October 3, 2013

-   [JENKINS-16305](https://issues.jenkins-ci.org/browse/JENKINS-16305) -
    Add support for Quiet period configuration slicing
-   [JENKINS-16828](https://issues.jenkins-ci.org/browse/JENKINS-16828) -
    NullPointerException in BooleanSlice.get for "Block Build" when
    slicing by view
-   [JENKINS-18060](https://issues.jenkins-ci.org/browse/JENKINS-18060) -
    Add support for logfilesizechecker
-   [JENKINS-19858](https://issues.jenkins-ci.org/browse/JENKINS-19858) -
    Visual spacing has degraded dramatically with newer versions of
    Jenkins (noticed in ver. 1.533)
-   [JENKINS-14521](https://issues.jenkins-ci.org/browse/JENKINS-14521) -
    Configuration Slicing should temporarily disable the Auto Refresh
    plugin
-   [JENKINS-19855](https://issues.jenkins-ci.org/browse/JENKINS-19855) -
    Parameters slicer trims default values - including space at the
    beginning of a Choice Parameter
-   Removed support for Workspace Cleanup Plugin (became too complex to
    perform configuration slicing)

#### Version 1.37.1 - Sept 30, 2013

-   fix regression introduced by folder support
    ([JENKINS-18455](https://issues.jenkins-ci.org/browse/JENKINS-18455))

#### Version 1.37 - June 4, 2013 

-   add support for folders
-   moved to github

#### Version 1.36 - October 28, 2012

-   [JENKINS-14456](https://issues.jenkins-ci.org/browse/JENKINS-14456) -
    Add support for Workspace Cleanup Plugin

#### Version 1.35 - October 27, 2012

-   Fixed issues with the newer version of the build timeout plugin
-   [JENKINS-15528](https://issues.jenkins-ci.org/browse/JENKINS-15528) -
    java.lang.ClassNotFoundException: hudson.plugins.python.Python
-   [JENKINS-15447](https://issues.jenkins-ci.org/browse/JENKINS-15447) -
    Warnings with Configuration Slicing plugin

#### Version 1.33 - Sept 19, 2012

-   [JENKINS-15152](https://issues.jenkins-ci.org/browse/JENKINS-15152) -
    Add support for configure 'Block build when upstream project is
    building' and 'Block build when downstream project is building'
-   [JENKINS-14784](https://issues.jenkins-ci.org/browse/JENKINS-14784) -
    Add support for the "execute python script" slicer

#### Version 1.32 - July 14, 2012

-   [JENKINS-14417](https://issues.jenkins-ci.org/browse/JENKINS-14417) -
    Add Timestamper slicer
-   [JENKINS-11463](https://issues.jenkins-ci.org/browse/JENKINS-11463) -
    Add configuration slicer for build priority sorter
-   [JENKINS-13834](https://issues.jenkins-ci.org/browse/JENKINS-13834) -
    E-mail Notification Slicer issues
-   [JENKINS-14428](https://issues.jenkins-ci.org/browse/JENKINS-14428) -
    Provide group operations for "Disable build slicer"
-   [JENKINS-12294](https://issues.jenkins-ci.org/browse/JENKINS-12294) -
    "Save" button in slicers should float, just like in the new job
    config pages

#### Version 1.31 - May 22, 2012

-   [JENKINS-13866](https://issues.jenkins-ci.org/browse/JENKINS-13866) -
    Configuration slicing for parameters
-   [JENKINS-13867](https://issues.jenkins-ci.org/browse/JENKINS-13867) -
    Configuration slicing for Maven versions should apply to Free style
    projects

#### Version 1.30 - May 20, 2012

-   [JENKINS-12515](https://issues.jenkins-ci.org/browse/JENKINS-12515) -
    Add configuration slicing for use of Ant, Gradle and Groovy
    installations
-   [JENKINS-13839](https://issues.jenkins-ci.org/browse/JENKINS-13839) -
    Configure Maven goals for multiple jobs and multiple builders
-   [JENKINS-12922](https://issues.jenkins-ci.org/browse/JENKINS-12922) -
    Support changing maven version to multiple jobs at once using
    configuration slicing plugin

#### Version 1.29 - May 18, 2012

-   [JENKINS-13830](https://issues.jenkins-ci.org/browse/JENKINS-13830) -
    Add windows batch command
-   [JENKINS-11941](https://issues.jenkins-ci.org/browse/JENKINS-11941) -
    Configuration Slicing Plugin's "Execute shell slicer" lists first
    execute shell step only

#### Version 1.28.1 - December 24, 2011

-   [JENKINS-12215](https://issues.jenkins-ci.org/browse/JENKINS-12215) -
    Configuration slicer changes step order

#### Version 1.27 - November 28, 2011

-   [JENKINS-11868](https://issues.jenkins-ci.org/browse/JENKINS-11868) -
    Add configuration slicer for Jenkins build timeout plugin

#### Version 1.26 - November 24, 2011

-   [JENKINS-11781](https://issues.jenkins-ci.org/browse/JENKINS-11781) -
    Configuration Slicing Plugin's "Execute shell slicer" view doesn't
    support Matrix jobs

#### Version 1.25 - November 9, 2011

-   [JENKINS-11649](https://issues.jenkins-ci.org/browse/JENKINS-11649) -
    Add configuration slicers for Discard Old Builds with artifacts

#### Version 1.24 - October 26, 2011

-   [JENKINS-11500](https://issues.jenkins-ci.org/browse/JENKINS-11500) -
    Add execute shell option to the plugin

#### Version 1.23 - October 13, 2011

-   Added configuration by views - see this wiki page for details

#### Version 1.22 - October 6, 2011

-   Fixed
    [JENKINS-11242](https://issues.jenkins-ci.org/browse/JENKINS-11242) -
    Add configuration slicing for Email-ext plugin

#### Version 1.21 - September 22, 2011

-   Fixed
    [JENKINS-11094](https://issues.jenkins-ci.org/browse/JENKINS-11094) -
    Add Email configuration slicer

#### Version 1.20 - August 24, 2011

-   Fixed
    [JENKINS-8194](https://issues.jenkins-ci.org/browse/JENKINS-8194) -
    Discard Old Builds Slicer resets configuration of artifact keeping
-   Fixed
    [JENKINS-10797](https://issues.jenkins-ci.org/browse/JENKINS-10797) -
    Sort "(Disabled)" properly
-   improved navigation options

#### Version 1.19 - July 22, 2011

-   Fixed
    [JENKINS-10431](https://issues.jenkins-ci.org/browse/JENKINS-10431) -
    do not create changes when there are no actual changes
-   sort the index of slicers
-   add newline to job names list to make it easier to cut and paste

#### Version 1.18 - July 2011

-   Improved handling of chron specs with comments or multiple lines
-   Fixed null pointer problem with timer slicers (previously would
    require reboot to pick up new slicer changes)

#### Version 1.17 - July 2011

-   Complete redesign of the string slicer GUI (affects most slicers)
-   Added custom workspace slicer

#### Version 1.15 - January 22, 2010

-   Add Maven project slicing - MAVEN\_OPTS and 'Goals and Settings'
    ([report](http://n4.nabble.com/Use-e-option-in-all-maven-jobs-tp1101676p1101676.html))

#### Version 1.14 - January 16, 2010

-   Fix LogRotationSlicer to handle empty strings better
    ([5240](https://issues.jenkins-ci.org/browse/JENKINS-5240))

#### Version 1.13 - January 15, 2010

-   Add Jdk slicer
    ([report](http://n4.nabble.com/Globally-change-default-JDK-td387648.html))
-   Finally sort out release issues: The pom.xml for maven-hpi-plugin
    needed to point to a newer javanettasks

#### Version 1.12 - attempted January 15, 2010 - never released

#### Version 1.11 - attempted January 15, 2010 - never released

#### Version 1.10 - attempted November 2, 2009 and January 15, 2010 - never released

#### Version 1.9 - July 29, 2009

-   No changes. Release was done to figure out case sensitivity issue in
    javanettasks 1.23

#### Version 1.8 - attempted July 22, 2009 - never released

#### Version 1.7 - attempted July 22, 2009 - never released

#### Version 1.6 - attempted July 21, 2009 and July 22, 2009 - never released

#### Version 1.5 - July 21, 2009

-   Add log rotation slicers, for each of days and builds
    ([report](http://n4.nabble.com/Consumed-1-2-GB-of-memory-tp384274p384285.html))

&nbsp;

-   Add SCM polling schedule slicer

&nbsp;

-   Add slave label slicer

#### Version 1.4 - attempted April 9, 2009 - never released

#### Version 1.3 - attempted April 9, 2009 - never released

#### Version 1.2 - March 16, 2009

-   Initial release - supports slicing for timer trigger

#### Version 1.1 - attempted March 16, 2009 - never released

#### Version 1.0 - attempted March 16, 2009 - never released
