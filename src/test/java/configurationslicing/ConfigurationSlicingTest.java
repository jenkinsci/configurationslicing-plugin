package configurationslicing;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import jenkins.model.Jenkins;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.MockAuthorizationStrategy;

import static org.junit.Assert.*;

public class ConfigurationSlicingTest {

    public @Rule JenkinsRule j = new JenkinsRule();

    @Test
    public void ensureManagementLinkRequiresAdministerPermission() throws Exception {
        j.jenkins.setSecurityRealm(j.createDummySecurityRealm());
        j.jenkins.setAuthorizationStrategy(new MockAuthorizationStrategy()
                .grant(Jenkins.READ).everywhere().toAuthenticated()
                .grant(Jenkins.ADMINISTER).everywhere().to("root")
        );

        assertEquals(403, assertThrows(FailingHttpStatusCodeException.class, () -> j.createWebClient().login("user").goTo("slicing")).getStatusCode());
        j.createWebClient().login("root").goTo("slicing");
    }
}