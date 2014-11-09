package configurationslicing;

import hudson.model.TopLevelItem;
import hudson.model.AbstractProject;
import hudson.model.Hudson;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
/**
 * Helper class to provide all top level items configured in Jenkins, excluding other items
 * held in folders, such as maven modules
 *
 */
public class TopLevelItemSelector {
    
    // private constructor since we don't expect this class to be instantiated
    private TopLevelItemSelector() {
        
    }
    
    /**
     * Provide all top level items configured in Jenkins 
     * @param clazz the type to search the ItemGroup for
     * @return all items in the Jenkins ItemGroup tree which are of type TopLevelItem
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static List<AbstractProject<?,?>> getAllTopLevelItems(Class clazz) {
        List<AbstractProject<?,?>> list =  (List)Hudson.getInstance().getAllItems(clazz);
        CollectionUtils.filter(list, new Predicate() {
            public boolean evaluate(Object object) {
                // exclude MatrixConfiguration, MavenModule, etc
                return object instanceof TopLevelItem;
            }
        });
        return list;
    }

}
