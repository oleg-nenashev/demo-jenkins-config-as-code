/*
 * The MIT License
 *
 * Copyright (c) 2017 Oleg Nenashev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import org.codehaus.groovy.control.CompilerConfiguration
import static java.util.logging.Level.INFO
import static java.util.logging.Level.WARNING;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.servlet.ServletContext;
import jenkins.model.Jenkins;

/**
 * Bootstraps the standard Jenkins initialization logic.
 * The bootstrap adds support of Groovy classes and propagates execution failures.
 * The started scripts will be debuggable in IDE via Remote Debugger inside a running instance.
 *
 * @author Oleg Nenashev
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

GroovyInitBootstrap bootstrap = new GroovyInitBootstrap(Jenkins.instance)
bootstrap.run()
