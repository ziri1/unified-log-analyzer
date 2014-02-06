package unifiedloganalyzer.parse;

import trskop.ICallback;

import unifiedloganalyzer.ParsedData;


/**
 * Dummy parser implementation.
 *
 * @author Peter Trsko
 */
public class DummyParser extends AParser
{
    // {{{ Constructors ///////////////////////////////////////////////////////

    public DummyParser()
    {
        super();
    }

    public DummyParser(ICallback<ParsedData> callback)
    {
        super(callback);
    }

    // }}} Constructors ///////////////////////////////////////////////////////

    @Override
    public void parse(String message)
    {
        runCallbacks(new ParsedData(new DummyParsedData(message)));
    }

    @Override
    public void eof()
    {
        runCallbacks(ParsedData.emptyMessage());
    }
}
