package unifiedloganalyzer.parse;

import java.io.IOException;

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

    @Override
    public String getOriginalMessage()
    {
        return _originalMessage;
    }

    protected final void setOriginalMessage(String originalMessage)
    {
        _originalMessage = originalMessage;
    }

    // }}} Getters and setters ////////////////////////////////////////////////

    protected abstract void appendRestTo(Appendable buff) throws IOException;

    @Override
    public void appendTo(Appendable buff) throws IOException
    {
        buff.append("{ originalMessage = ")
            .append(_originalMessage)
            .append('\n');
        appendRestTo(buff);
        buff.append("}\n");
    }
}
