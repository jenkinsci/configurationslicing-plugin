package configurationslicing.executeshell;

import hudson.matrix.MatrixProject;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.model.Project;
import hudson.tasks.Builder;
import hudson.util.DescribableList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import configurationslicing.UnorderedStringSlicer;

/**
 * @author Jacob Robertson
 */
public abstract class AbstractBuildCommandSlicer<B extends Builder> extends UnorderedStringSlicer<AbstractProject<?,?>> {

    public AbstractBuildCommandSlicer(AbstractBuildCommandSliceSpec<B> spec) {
        super(spec);
    }

    public static abstract class AbstractBuildCommandSliceSpec<B extends Builder> extends UnorderedStringSlicerSpec<AbstractProject<?,?>> {

        public static final String NOTHING = "(nothing)";

        public String getDefaultValueString() {
            return NOTHING;
        }

        public String getName(AbstractProject<?, ?> item) {
            return item.getFullName();
        }

        @Override
        public boolean isIndexUsed(int count) {
        	return count > 1;
        }
        
        public List<String> getValues(AbstractProject<?, ?> item) {
            List<String> content = new ArrayList<String>();
            DescribableList<Builder,Descriptor<Builder>> buildersList = getBuildersList(item);

            List<B> builders = getConcreteBuildersList(buildersList);
            for (B builder: builders) {
                content.add(getCommand(builder));
            }
            if (content.isEmpty()) {
            	content.add(NOTHING);
            }

            return content;
        }
        public abstract List<B> getConcreteBuildersList(DescribableList<Builder,Descriptor<Builder>> buildersList);
        public abstract String getCommand(B builder);
        public abstract B[] createBuilderArray(int len);
        public abstract B createBuilder(String command, List<B> existingBuilders, B oldBuilder);

        @SuppressWarnings("unchecked")
        public List<AbstractProject<?, ?>> getWorkDomain() {
        	List<AbstractProject<?, ?>> list = new ArrayList<AbstractProject<?, ?>>();
        	List<AbstractProject> temp = Hudson.getInstance().getAllItems(AbstractProject.class);
        	for (AbstractProject p: temp) {
        		if (p instanceof Project || p instanceof MatrixProject) {
        			list.add(p);
        		}
        	}
        	return list;
        }
        
        @SuppressWarnings("unchecked")
		public static DescribableList<Builder,Descriptor<Builder>> getBuildersList(AbstractProject<?, ?> item) {
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
            List<B> builders = getConcreteBuildersList(buildersList);
            
            int maxLen = Math.max(list.size(), builders.size());
            B[] oldBuilders = createBuilderArray(maxLen);
            B[] newBuilders = createBuilderArray(maxLen);

            for (int i = 0; i < builders.size(); i++) {
	            oldBuilders[i] = builders.get(i);
            }

            for (int i = 0; i < list.size(); i++) {
	            String command = list.get(i);
	            if(!command.equals(NOTHING) && !command.equals("")) {
	            	if (oldBuilders[i] != null && getCommand(oldBuilders[i]).equals(command)) {
	            		newBuilders[i] = oldBuilders[i];
	            	} else {
	            		newBuilders[i] = createBuilder(command, builders, oldBuilders[i]);
	            	}
	            }
            }
            
            // perform any replacements
            for (int i = 0; i < maxLen; i++) {
				if (oldBuilders[i] != null && newBuilders[i] != null && oldBuilders[i] != newBuilders[i]) {
					replaceBuilder(buildersList, oldBuilders[i], newBuilders[i]);
				}
			}
            
            // add any new ones (should always add to the end, but might not if the original command was empty)
            for (int i = 0; i < maxLen; i++) {
				if (oldBuilders[i] == null && newBuilders[i] != null) {
	                try {
	                    buildersList.add(newBuilders[i]);
	                } catch(java.io.IOException e) {
	                    System.err.println("IOException Thrown add builder value");
	                    return false;
	                }
				}
			}
            
            // delete any old ones
            for (int i = 0; i < maxLen; i++) {
				if (oldBuilders[i] != null && newBuilders[i] == null) {
                    try {
	                	// the remove command will persist the project
	                    buildersList.remove(oldBuilders[i]);
	                } catch(java.io.IOException e) {
	                    System.err.println("IOException Thrown removing builder value");
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
        public static boolean replaceBuilder(DescribableList<Builder,Descriptor<Builder>> builders, Builder oldBuilder, Builder newBuilder) {
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

