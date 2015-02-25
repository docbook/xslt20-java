package org.docbook.extensions.xslt20.jython;

/**
 * This interface defines the PygmenterType.
 *
 * <p>Copyright &copy; 2011-2015 Norman Walsh.
 *
 * @author Norman Walsh
 * <a href="mailto:ndw@nwalsh.com">ndw@nwalsh.com</a>
 */
public interface PygmenterType {
    public String format(String code, String language);
}
