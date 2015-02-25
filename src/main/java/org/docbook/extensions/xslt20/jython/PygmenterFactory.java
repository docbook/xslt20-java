package org.docbook.extensions.xslt20.jython;

import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

/**
 * This interface defines the PygmenterFactory.
 *
 * <p>Copyright &copy; 2011-2015 Norman Walsh.
 *
 * @author Norman Walsh
 * <a href="mailto:ndw@nwalsh.com">ndw@nwalsh.com</a>
 */
public class PygmenterFactory {
    private PyObject jyPygmenterClass;

    public PygmenterFactory() {
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.exec("from DocBookPygmenter import DocBookPygmenter");
        jyPygmenterClass = interpreter.get("DocBookPygmenter");
    }

    public PygmenterType create() {
        PyObject highlightObj = jyPygmenterClass.__call__();
        return (PygmenterType) highlightObj.__tojava__(PygmenterType.class);
    }
}
