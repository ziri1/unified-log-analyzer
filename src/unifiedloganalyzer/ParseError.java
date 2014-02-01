package unifiedloganalyzer;

import java.io.IOException;
import java.lang.Appendable;

import unifiedloganalyzer.AParsedData;


/**
 * Parsing failed.
 *
 * @author Peter Trsko
 */
public class ParseError extends AParsedData
{
    private String _error;

    // {{{ Constructors ///////////////////////////////////////////////////////

    public ParseError(String originalMessage, String error)
    {
        super(originalMessage);

        setError(error);
    }

    // }}} Constructors ///////////////////////////////////////////////////////

    // {{{ Getters and setters ////////////////////////////////////////////////

    public String getError()
    {
        return _error;
    }

    protected void setError(String error)
    {
        _error = error;
    }

    // }}} Getters and setters ////////////////////////////////////////////////

    // {{{ Concrete implementation of AParsedData /////////////////////////////

    public void appendRestTo(Appendable buff) throws IOException
    {
        buff.append(", error = ")
            .append(getError())
            .append('\n');
    }

    // }}} Concrete implementation of AParsedData /////////////////////////////
}
