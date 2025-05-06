package configurationslicing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jenkins.model.Jenkins;
import org.htmlunit.FailingHttpStatusCodeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.MockAuthorizationStrategy;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class ConfigurationSlicingTest {

    private JenkinsRule j;

    @BeforeEach
    void setUp(JenkinsRule rule) {
        j = rule;
    }

    @Test
    void ensureManagementLinkRequiresAdministerPermission() throws Exception {
        j.jenkins.setSecurityRealm(j.createDummySecurityRealm());
        j.jenkins.setAuthorizationStrategy(new MockAuthorizationStrategy()
                .grant(Jenkins.READ)
                .everywhere()
                .toAuthenticated()
                .grant(Jenkins.ADMINISTER)
                .everywhere()
                .to("root"));

        FailingHttpStatusCodeException ex = assertThrows(
                FailingHttpStatusCodeException.class,
                () -> j.createWebClient().login("user").goTo("slicing"));
        assertEquals(403, ex.getStatusCode());

        j.createWebClient().login("root").goTo("slicing");
    }
}
