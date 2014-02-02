package unifiedloganalyzer.parse;

import trskop.ICallback;

import unifiedloganalyzer.ParsedData;
import unifiedloganalyzer.parse.AParser;
import unifiedloganalyzer.parse.DummyParsedData;


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

    public void parse(String message)
    {
        runCallbacks(new ParsedData(new DummyParsedData(message)));
    }

    public void eof()
    {
        runCallbacks(ParsedData.emptyMessage());
    }
}
