package unifiedloganalyzer.analyze;

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
    protected void doAnalysis(ParsedData.Type dataType, IParsedData data)
    {
        runCallbacks(new DummyOutputMessage(data));
    }
}
