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

        public static final String NOTHING = "(nothing)";

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

        @Override
        public boolean isMultipleItemsAllowed() {
        	return true;
        }
        
        public List<String> getValues(AbstractProject<?, ?> item) {
            List<String> shellContent = new ArrayList<String>();
            DescribableList<Builder,Descriptor<Builder>> buildersList = getBuildersList(item);

            List<Shell> shells = buildersList.getAll(Shell.class);
            for (Shell shell: shells) {
                shellContent.add(shell.getCommand());
            }
            if (shellContent.isEmpty()) {
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

        public boolean setValues(AbstractProject<?, ?> item, List<String> list) {
            DescribableList<Builder,Descriptor<Builder>> buildersList = getBuildersList(item);
            List<Shell> shells = buildersList.getAll(Shell.class);
            
            int maxLen = Math.max(list.size(), shells.size());
            Shell[] oldShells = new Shell[maxLen];
            Shell[] newShells = new Shell[maxLen];

            for (int i = 0; i < shells.size(); i++) {
	            oldShells[i] = shells.get(i);
            }

            for (int i = 0; i < list.size(); i++) {
	            String command = list.get(i);
	            if(!command.equals(NOTHING) && !command.equals("")) {
	            	if (oldShells[i] != null && oldShells[i].getCommand().equals(command)) {
	            		newShells[i] = oldShells[i];
	            	} else {
	            		newShells[i] = new Shell(command);
	            	}
	            }
            }
            
            // perform any replacements
            for (int i = 0; i < maxLen; i++) {
				if (oldShells[i] != null && newShells[i] != null && oldShells[i] != newShells[i]) {
					replaceBuilder(buildersList, oldShells[i], newShells[i]);
				}
			}
            
            // add any new ones (should always add to the end, but might not if the original command was empty)
            for (int i = 0; i < maxLen; i++) {
				if (oldShells[i] == null && newShells[i] != null) {
	                try {
	                    buildersList.add(newShells[i]);
	                } catch(java.io.IOException e) {
	                    System.err.println("IOException Thrown add builder value");
	                    return false;
	                }
				}
			}
            
            // delete any old ones
            for (int i = 0; i < maxLen; i++) {
				if (oldShells[i] != null && newShells[i] == null) {
                    try {
	                	// the remove command will persist the project
	                    buildersList.remove(oldShells[i]);
	                } catch(java.io.IOException e) {
	                    System.err.println("IOException Thrown removing shell value");
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

