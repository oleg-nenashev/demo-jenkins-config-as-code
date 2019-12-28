import org.codehaus.groovy.control.CompilerConfiguration
import static java.util.logging.Level.INFO
import static java.util.logging.Level.WARNING
import java.util.logging.Logger
import javax.annotation.Nonnull
import javax.servlet.ServletContext
import jenkins.model.Jenkins

/**
 * see: https://github.com/oleg-nenashev/demo-jenkins-config-as-code.git
 * Bootstraps the standard Jenkins initialization logic.
 * The bootstrap adds support of Groovy classes and propagates execution failures.
 * The started scripts will be debuggable in IDE via Remote Debugger inside a running instance.
 */
class GroovyInitBootstrap {
    private final Binding bindings = new Binding()
    private final ServletContext servletContext
    private final File home
    private final GroovyClassLoader groovyClassloader
    private final CompilerConfiguration compilerConfiguration = CompilerConfiguration.DEFAULT

    private GroovyInitBootstrap(Jenkins j) {
        this(j.servletContext, j.rootDir, j.pluginManager.uberClassLoader)
    }

    GroovyInitBootstrap(@Nonnull ServletContext servletContext, @Nonnull File home, @Nonnull ClassLoader groovyClassloader) {
        this.servletContext = servletContext
        this.home = home
        compilerConfiguration.classpath = new File(home, "init.groovy.d/").absolutePath
        this.groovyClassloader = new GroovyClassLoader(groovyClassloader, compilerConfiguration, true)
    }

    GroovyBootstrap bind(String name, Object o) {
        bindings.setProperty(name,o)
        return this
    }

    /**
     * Runs the bootstrap and executes all scripts in {@code init.groovy.d/scripts}.
     * @throws Error Execution failed, should be considered as fatal.
     */
    void run() {
        File scriptsDir = new File(home, "init.groovy.d/scripts")
        if (scriptsDir.isDirectory()) {
            File[] scripts = scriptsDir.listFiles(new FileFilter() {
                boolean accept(File f) {
                    return f.name.endsWith(".groovy")
                }
            })
            if (scripts!=null) {
                // sort to run them in an alphabetic order
                scripts.sort().each {
                    execute(it)
                }
            }
        }
    }

    /**
     * Executes the specified Groovy file.
     * @param f Groovy file
     * @throws Error Execution failed, should be considered as fatal.
     */
    protected void execute(@Nonnull File f) {
        if (f.exists()) {
            LOGGER.log(INFO, "Executing {0}", f)
            try {
                GroovyCodeSource codeSource = new GroovyCodeSource(f)
                new GroovyShell(groovyClassloader, bindings).evaluate(codeSource)
            } catch (IOException e) {
                LOGGER.log(WARNING, "Failed to execute " + f, e);
                throw new Error("Failed to execute " + f, e)
            }
        }
    }

    private static final Logger LOGGER = Logger.getLogger(GroovyBootstrap.class.getName());
}

GroovyInitBootstrap bootstrap = new GroovyInitBootstrap(Jenkins.getInstanceOrNull())
bootstrap.run()
