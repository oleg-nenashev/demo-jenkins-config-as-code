package hudson.plugins.locale;

import jenkins.model.Jenkins;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.recipes.LocalData;

import static org.junit.Assert.assertEquals;

/**
 * Tests data loading from Locale Plugin 1.3.
 * @author Oleg Nenashev
 */
public class MigrationTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @LocalData
    @Test
    public void dataMigration_13() {
        PluginImpl plugin = (PluginImpl) Jenkins.getActiveInstance().getPlugin("locale");
        assertEquals("en-US", plugin.getSystemLocale());
        assertEquals(true, plugin.isIgnoreAcceptLanguage());
    }
}
