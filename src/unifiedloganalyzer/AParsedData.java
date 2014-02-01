package unifiedloganalyzer;

import java.io.IOException;
import java.lang.Appendable;
import java.lang.IllegalArgumentException;

import unifiedloganalyzer.IParsedData;


/**
 * Abstract implementation of IParsedData for boilerplate reduction of its
 * concrete implementations.
 *
 * @author Peter Trsko
 */
public abstract class AParsedData implements IParsedData
{
    private String _originalMessage;

    // {{{ Constructors ///////////////////////////////////////////////////////

    public AParsedData(String originalMessage)
    {
        if (originalMessage == null)
        {
            throw new IllegalArgumentException("null");
        }

        setOriginalMessage(originalMessage);
    }

    // }}} Constructors ///////////////////////////////////////////////////////

    // {{{ Getters and setters ////////////////////////////////////////////////

    public String getOriginalMessage()
    {
        return _originalMessage;
    }

    protected void setOriginalMessage(String originalMessage)
    {
        _originalMessage = originalMessage;
    }

    // }}} Getters and setters ////////////////////////////////////////////////

    protected abstract void appendRestTo(Appendable buff) throws IOException;

    public void appendTo(Appendable buff) throws IOException
    {
        buff.append("{ originalMessage = ")
            .append(_originalMessage)
            .append('\n');
        appendRestTo(buff);
        buff.append("}\n");
    }
}
