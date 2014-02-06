package unifiedloganalyzer.parse;

import java.io.IOException;


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

    public final String getError()
    {
        return _error;
    }

    protected final void setError(String error)
    {
        _error = error;
    }

    // }}} Getters and setters ////////////////////////////////////////////////

    // {{{ Concrete implementation of AParsedData /////////////////////////////

    @Override
    public void appendRestTo(Appendable buff) throws IOException
    {
        buff.append(", error = ")
            .append(getError())
            .append('\n');
    }

    // }}} Concrete implementation of AParsedData /////////////////////////////
}
