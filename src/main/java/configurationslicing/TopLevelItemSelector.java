package configurationslicing;

import hudson.model.Item;
import hudson.model.TopLevelItem;
import java.util.List;
import jenkins.model.Jenkins;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

/**
 * Helper class to provide all top level items configured in Jenkins, excluding other items
 * held in folders, such as maven modules
 *
 */
public class TopLevelItemSelector {

    // private constructor since we don't expect this class to be instantiated
    private TopLevelItemSelector() {}

    /**
     * Provide all top level items configured in Jenkins
     * @param clazz the type to search the ItemGroup for
     * @return all items in the Jenkins ItemGroup tree which are of type TopLevelItem
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T extends Item> List<T> getAllTopLevelItems(Class<T> clazz) {
        List<T> list = Jenkins.get().getAllItems(clazz);
        CollectionUtils.filter(list, new Predicate() {
            public boolean evaluate(Object object) {
                // exclude MatrixConfiguration, MavenModule, etc
                return object instanceof TopLevelItem;
            }
        });
        return list;
    }
}
