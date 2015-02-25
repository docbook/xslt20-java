package org.docbook.extensions.xslt20;

import net.sf.saxon.Platform;
import net.sf.saxon.Configuration;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.expr.StaticProperty;
import net.sf.saxon.expr.StaticContext;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.value.Int64Value;
import net.sf.saxon.value.StringValue;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.tree.iter.SingletonIterator;
import net.sf.saxon.tree.iter.EmptyIterator;
import net.sf.saxon.tree.iter.ArrayIterator;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.type.BuiltInAtomicType;
import net.sf.saxon.s9api.Axis;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmSequenceIterator;
import net.sf.saxon.s9api.SaxonApiException;

import com.nwalsh.annotations.SaxonExtensionFunction;
import javax.xml.transform.sax.SAXSource;
import java.io.StringReader;
import org.xml.sax.InputSource;

import org.docbook.extensions.xslt20.jython.PygmenterFactory;
import org.docbook.extensions.xslt20.jython.PygmenterType;

/**
 * Saxon extension to call the Pygments syntax highlighter through Jython.
 *
 * <p>This class provides a
 * <a href="http://saxonica.com/">Saxon</a>
 * extension to highlight source code listings.
 *
 * <p>As support for Jython seems to have faded, so has the utility of this extension
 * function. You might very well be better off with one of the JavaScript syntax
 * highlighters at this point.
 *
 * <p>Copyright &copy; 2011-2015 Norman Walsh.
 *
 * @author Norman Walsh
 * <a href="mailto:ndw@nwalsh.com">ndw@nwalsh.com</a>
 */
@SaxonExtensionFunction(warnLevel="TRACE")
public class Pygmenter extends ExtensionFunctionDefinition {
    private static final StructuredQName qName =
        new StructuredQName("", "http://docbook.org/extensions/xslt20", "highlight");
    private static final QName h_pre =
        new QName("", "http://www.w3.org/1999/xhtml", "pre");

    private static Processor processor = null;
    private static final PygmenterFactory factory = new PygmenterFactory();

    @Override
    public StructuredQName getFunctionQName() {
        return qName;
    }

    @Override
    public int getMinimumNumberOfArguments() {
        return 1;
    }

    @Override
    public int getMaximumNumberOfArguments() {
        return 2;
    }

    @Override
    public SequenceType[] getArgumentTypes() {
        return new SequenceType[]{SequenceType.ATOMIC_SEQUENCE};
    }

    @Override
    public SequenceType getResultType(SequenceType[] suppliedArgumentTypes) {
        return SequenceType.NODE_SEQUENCE;
    }

    public ExtensionFunctionCall makeCallExpression() {
        return new HighlightCall();
    }

    private class HighlightCall extends ExtensionFunctionCall {
        protected StaticContext staticContext = null;

        public void supplyStaticContext(StaticContext context, int locationId,
                                        Expression[] arguments) throws XPathException {
            staticContext = context;
        }

        public void copyLocalData(ExtensionFunctionCall dest) {
            ((HighlightCall) dest).staticContext = staticContext;
        }

        public SequenceIterator call(SequenceIterator[] arguments, XPathContext context) throws XPathException {
            String code = ((StringValue) arguments[0].next()).getStringValue();
            String language = "";

            if (arguments.length > 1) {
                language = ((StringValue) arguments[1].next()).getStringValue();
            }

            if (processor == null) {
                processor = new Processor(context.getConfiguration());
            }

            DocumentBuilder builder = processor.newDocumentBuilder();

            PygmenterType pygmenter = factory.create();
            String result = pygmenter.format(code, language);

            XdmNode doc = null;
            try {
                // Wrap a div with the right namespace around the string
                String parse = "<div xmlns='http://www.w3.org/1999/xhtml'>" + result + "</div>";
                SAXSource source = new SAXSource(new InputSource(new StringReader(parse)));
                doc = builder.build(source);
            } catch (SaxonApiException sae) {
                // I don't ever expect this to happen
                throw new UnsupportedOperationException(sae);
            }

            XdmNode pre = null;
            XdmSequenceIterator preIter = doc.axisIterator(Axis.DESCENDANT, h_pre);
            while (pre == null && preIter.hasNext()) {
                pre = (XdmNode) preIter.next();
            }

            return pre.getUnderlyingNode().iterateAxis(net.sf.saxon.om.Axis.CHILD);
        }
    }
}
