package unifiedloganalyzer.analyze;

import unifiedloganalyzer.IOutputMessage;
import unifiedloganalyzer.IParsedData;
import unifiedloganalyzer.ParsedData;
import unifiedloganalyzer.analyze.AAnalyzer;
import unifiedloganalyzer.analyze.DummyOutputMessage;


/**
 * Dummy analyzer doesn't perform any analysis, it just executes callbacks.
 *
 * @author Peter Trsko
 */
public class DummyAnalyzer extends AAnalyzer
{
    protected void processMessage(IParsedData data)
    {
        runCallbacks(new DummyOutputMessage(data));
    }

    // {{{ AAnalyzer, implementation of abstract methods //////////////////////

    protected void processEmptyMessage(IParsedData parsedData)
    {
        processMessage(parsedData);
    }

    protected void processParsedMessage(IParsedData parsedData)
    {
        processMessage(parsedData);
    }

    protected void processParseError(IParsedData parsedData)
    {
        processMessage(parsedData);
    }

    // }}} AAnalyzer, implementation of abstract methods //////////////////////
}
