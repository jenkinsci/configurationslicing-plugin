package configurationslicing.executeshell;

import hudson.Extension;
import hudson.matrix.MatrixProject;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.model.Project;
import hudson.tasks.Builder;
import hudson.tasks.Shell;
import hudson.util.DescribableList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import configurationslicing.UnorderedStringSlicer;

/**
 * Slicer for the shell builder
 *
 * @author Victor Garcia <bravejolie@gmail.com> <victor@tuenti.com>
 */
@Extension
public class ExecuteShellSlicer extends UnorderedStringSlicer<AbstractProject<?,?>>{

    public ExecuteShellSlicer() {
        super(new ExecuteShellSliceSpec());
    }

    public static class ExecuteShellSliceSpec extends UnorderedStringSlicerSpec<AbstractProject<?,?>> {

        private static final String NOTHING = "(nothing)";

        public String getDefaultValueString() {
            return NOTHING;
        }

        public String getName() {
            return "Execute shell slicer";
        }

        public String getName(AbstractProject<?, ?> item) {
            return item.getName();
        }

        public String getUrl() {
            return "executeshellslicestring";
        }

        public List<String> getValues(AbstractProject<?, ?> item) {
            List<String> shellContent = new ArrayList<String>();
            DescribableList<Builder,Descriptor<Builder>> buildersList = getBuildersList(item);

            Shell shell = (Shell)buildersList.get(Shell.class);
            if(shell != null) {
                shellContent.add(shell.getCommand());
            } else {
                shellContent.add(NOTHING);
            }

            return shellContent;
        }

        @SuppressWarnings("unchecked")
        public List<AbstractProject<?, ?>> getWorkDomain() {
        	List<AbstractProject<?, ?>> list = new ArrayList<AbstractProject<?, ?>>();
        	List<AbstractProject> temp = Hudson.getInstance().getItems(AbstractProject.class);
        	for (AbstractProject p: temp) {
        		if (p instanceof Project || p instanceof MatrixProject) {
        			list.add(p);
        		}
        	}
        	return list;
        }
        
        @SuppressWarnings("unchecked")
		private DescribableList<Builder,Descriptor<Builder>> getBuildersList(AbstractProject<?, ?> item) {
        	if (item instanceof Project) {
        		return ((Project) item).getBuildersList();
        	} else if (item instanceof MatrixProject) {
        		return ((MatrixProject) item).getBuildersList();
        	} else {
        		return null;
        	}
            
        }

        public boolean setValues(AbstractProject<?, ?> item, List<String> set) {
            DescribableList<Builder,Descriptor<Builder>> buildersList = getBuildersList(item);
            String command = set.iterator().next();
            
            // if the command is empty or NOTHING, remove the shell builder from the job
            if(command.equals(NOTHING) || command.equals("")) {
                Shell shell = (Shell) buildersList.get(Shell.class);
                if(shell != null) {
                    try {
                    	// the remove command will persist the project
                        buildersList.remove(shell.getDescriptor());
                    } catch(java.io.IOException e) {
                        System.err.println("IOException Thrown removing shell value");
                        return false;
                    }
                }
            } else {
        		Shell newShell = new Shell(command);
            	// check to see if we need to replace the shell command.  This prevents persisting
            	// an empty change, which is important for keeping audit trails clean.
            	Shell currentShell = buildersList.get(Shell.class);
            	if (currentShell != null) {
            		String oldCommand = currentShell.getCommand();
            		if (!command.equals(oldCommand)) {
            			return replaceBuilder(buildersList, currentShell, newShell);
            		}
            	} else {
	                try {
	                    buildersList.add(newShell);
	                } catch(java.io.IOException e) {
	                    System.err.println("IOException Thrown add shell value");
	                    return false;
	                }
            	}
            }
			return true;
        }
        /**
         * If we do other builders, publishers, etc - this should be the pattern to use.
         * @throws IOException 
         */
        private boolean replaceBuilder(DescribableList<Builder,Descriptor<Builder>> builders, Builder oldBuilder, Builder newBuilder) {
        	List<Builder> newList = new ArrayList<Builder>(builders.toList());
        	for (int i = 0; i < newList.size(); i++) {
    			Builder b = newList.get(i);
    			if (b == oldBuilder) {
    				newList.set(i, newBuilder);
    			}
    		}
        	try {
        		builders.replaceBy(newList);
        		return true;
            } catch(java.io.IOException e) {
            	System.err.println("IOException Thrown replacing builder list");
            	return false;
            }
        }
    }
}

