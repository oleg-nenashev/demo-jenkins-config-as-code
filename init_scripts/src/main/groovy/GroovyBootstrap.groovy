import org.codehaus.groovy.control.CompilerConfiguration

import static java.util.logging.Level.WARNING;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.servlet.ServletContext;
import jenkins.model.Jenkins;

/**
 * Bootstraps standard Jenkins initialization logic.
 * @author Oleg Nenashev
 */
class GroovyInitBootstrap {
    private final Binding bindings = new Binding();
    private final ServletContext servletContext;
    private final File home;
    private final GroovyClassLoader loader;
    private final CompilerConfiguration compilerConfiguration = CompilerConfiguration.DEFAULT

    private GroovyInitBootstrap(Jenkins j) {
        this(j.servletContext, j.getRootDir(), j.getPluginManager().uberClassLoader);
    }

    public GroovyInitBootstrap(@Nonnull ServletContext servletContext, @Nonnull File home, @Nonnull ClassLoader loader) {
        this.servletContext = servletContext;
        this.home = home;
        compilerConfiguration.classpath = new File(home, "init.groovy.d/").absolutePath
        this.loader = new GroovyClassLoader(loader, compilerConfiguration, true);
    }

    public GroovyBootstrap bind(String name, Object o) {
        bindings.setProperty(name,o);
        return this;
    }

    public Binding getBindings() {
        return bindings;
    }

    public void run() {

        File scriptD = new File(home, "init.groovy.d/scripts");
        if (scriptD.isDirectory()) {
            File[] scripts = scriptD.listFiles(new FileFilter() {
                public boolean accept(File f) {
                    return f.getName().endsWith(".groovy");
                }
            });
            if (scripts!=null) {
                // sort to run them in a deterministic order
                Arrays.sort(scripts);
                for (File f : scripts) {
                    execute(f);
                }
            }
        }
    }

    protected void execute(URL bundled) throws IOException {
        if (bundled!=null) {
            LOGGER.info("Executing bundled script: "+bundled);
            execute(new GroovyCodeSource(bundled));
        }
    }

    protected void execute(File f) {
        if (f.exists()) {
            LOGGER.info("Executing "+f);
            try {
                execute(new GroovyCodeSource(f));
            } catch (IOException e) {
                LOGGER.log(WARNING, "Failed to execute " + f, e);
                throw new Error("Failed to execute " + f, e)
            }
        }
    }

    protected void execute(GroovyCodeSource s) {
        try {
            createShell().evaluate(s);
        } catch (RuntimeException x) {
            LOGGER.log(WARNING, "Failed to run script " + s.getName(), x);
            throw new Error("Failed to execute " + s.getName(), x)
        }
    }

    /**
     * Can be used to customize the environment in which the script runs.
     */
    protected GroovyShell createShell() {
        return new GroovyShell(loader, bindings);
    }

    private static final Logger LOGGER = Logger.getLogger(GroovyBootstrap.class.getName());
}

GroovyInitBootstrap bootstrap = new GroovyInitBootstrap(Jenkins.instance)
bootstrap.run()
