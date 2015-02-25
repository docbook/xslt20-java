package docbook;

import net.sf.saxon.Configuration;
import net.sf.saxon.trans.XPathException;
import org.docbook.extensions.xslt20.Cwd;
import org.docbook.extensions.xslt20.ImageIntrinsics;
import org.docbook.extensions.xslt20.Pygmenter;

/**
 * Saxon intializer to allow Saxon HE to load DocBook extension functions
 *
 * This class provides a
 * <a href="http://saxonica.com/">Saxon</a>
 * Initializer to establish the DocBook extension functions.
 *
 * <p>Copyright &copy; 2011-2015 Norman Walsh.
 *
 * @author Norman Walsh
 * <a href="mailto:ndw@nwalsh.com">ndw@nwalsh.com</a>
 */
public class Initializer implements net.sf.saxon.lib.Initializer {

    public void initialize(Configuration config) {
        try {
            config.registerExtensionFunction(new Cwd());
            config.registerExtensionFunction(new ImageIntrinsics());

            try {
                config.registerExtensionFunction(new Pygmenter());
            } catch (NoClassDefFoundError ncdfe) {
                // Jython must not be on the classpath.
                // That's ok, just ignore this extension function.
            }
            // Exception instead of XPathException because Saxon 9.4 changed the API
        } catch (Exception xe) {
            System.err.println("Failed to register DocBook extension functions:");
            xe.printStackTrace();
        }
    }
}
