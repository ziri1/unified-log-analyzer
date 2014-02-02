package unifiedloganalyzer.analyze;

import java.io.IOException;

import unifiedloganalyzer.IParsedData;
import unifiedloganalyzer.IOutputMessage;


/**
 * Instance of <code>IOutputMessage</code> that only wraps
 * <code>IParsedData</code> instance.
 *
 * @author Peter Trsko
 */
public class DummyOutputMessage implements IOutputMessage
{
    private IParsedData _parsedData;

    public DummyOutputMessage(IParsedData parsedData)
    {
        _parsedData = parsedData;
    }

    // {{{ IOutputMessage implementation //////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    public void appendTo(Appendable buff) throws IOException
    {
        if (_parsedData == null)
        {
            buff.append("null");
        }
        else
        {
            buff.append(_parsedData.getClass().getName())
                .append(":\n");
            _parsedData.appendTo(buff);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean messageEquals(IOutputMessage message)
    {
        // Method equals might be overloaded, therefore its just sensible to
        // make sure that message is instance of this class.
        return message instanceof DummyOutputMessage
            && this.equals(message);
    }

    // }}} IOutputMessage implementation //////////////////////////////////////
}
