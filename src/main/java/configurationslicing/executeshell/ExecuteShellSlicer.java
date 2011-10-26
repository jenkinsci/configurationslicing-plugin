package configurationslicing.executeshell;

import configurationslicing.ExecuteShellSlice;
import configurationslicing.UnorderedStringSlice;
import hudson.Extension;
import hudson.tasks.Shell;
import hudson.tasks.Builder;
import hudson.model.Hudson;
import hudson.model.Project;
import hudson.model.Descriptor;
import hudson.util.DescribableList;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

import configurationslicing.UnorderedStringSlicer;

/**
 * Slicer for the shell builder
 *
 * @author Victor Garcia <bravejolie@gmail.com> <victor@tuenti.com>
 */
@Extension
public class ExecuteShellSlicer extends UnorderedStringSlicer<Project<?,?>>{

    public ExecuteShellSlicer() {
        super(new ExecuteShellSliceSpec());
    }

    public static class ExecuteShellSliceSpec extends UnorderedStringSlicerSpec<Project<?,?>> {

        private static final String NOTHING = "(nothing)";

        public String getDefaultValueString() {
            return NOTHING;
        }

        public String getName() {
            return "Execute shell slicer";
        }

        public String getName(Project<?, ?> item) {
            return item.getName();
        }

        public String getUrl() {
            return "executeshellslicestring";
        }

        public List<String> getValues(Project<?, ?> item) {
            List<String> shellContent = new ArrayList<String>();
            DescribableList<Builder,Descriptor<Builder>> buildersList = item.getBuildersList();

            Shell shell = (Shell)buildersList.get(Shell.class);
            if(shell != null) {
                shellContent.add(shell.getCommand());
            } else {
                shellContent.add(NOTHING);
            }

            return shellContent;
        }

        @SuppressWarnings("unchecked")
        public List<Project<?, ?>> getWorkDomain() {
            return (List) Hudson.getInstance().getItems(Project.class);
        }

        public boolean setValues(Project<?, ?> item, Set<String> set) {
            DescribableList<Builder,Descriptor<Builder>> buildersList = item.getBuildersList();
            Shell shell = null;
            String command = set.iterator().next();
            
            // if the command is empty or NOTHING, remove the shell builder from the job
            if(command.equals(NOTHING) || command.equals("")) {
                shell = (Shell)buildersList.get(Shell.class);
                if(shell != null) {
                    try {
                        buildersList.remove(shell.getDescriptor());
                    } catch(java.io.IOException e) {
                        System.err.println("IOException Thrown removing shell value");
                        return false;
                    }
                }
            } else {
                shell = new Shell(command);
                try {
                    buildersList.replace(shell);
                } catch(java.io.IOException e) {
                    System.err.println("IOException Thrown replacing shell value");
                    return false;
                }
            }
            return true;
        }
    }
}

