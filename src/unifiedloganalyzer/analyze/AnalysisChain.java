package unifiedloganalyzer.analyze;

import trskop.ICallback;

import unifiedloganalyzer.IAnalyzer;
import unifiedloganalyzer.IParsedData;
import unifiedloganalyzer.IOutputMessage;
import unifiedloganalyzer.ParsedData;
import unifiedloganalyzer.utils.CompoundMessage;


/**
 * Chain two analyzers together.
 *
 * Note that either of them can be also AnalysisChain instance.
 *
 * @author Peter Trsko
 */
public class AnalysisChain implements IAnalyzer
{
    // first.analyze() -> transformer.runCallback() -> second.analyze()
    private static class Transformer implements ICallback<IOutputMessage>
    {
        IAnalyzer _analyzer = null;

        public Transformer(IAnalyzer analyzer)
        {
            _analyzer = analyzer;
        }

        public void runCallback(IOutputMessage message)
        {
            ParsedData parsedData = null;

            if (message instanceof ParsedData)
            {
                parsedData = (ParsedData)message;
            }
            else if (message instanceof IParsedData)
            {
                parsedData = new ParsedData((IParsedData)message);
            }
            else
            {
                parsedData = new ParsedData(
                    new CompoundMessage<IOutputMessage, IParsedData>(
                        message,
                        null));     // Not providing parsed data!
            }

            _analyzer.analyze(parsedData);
        }
    }

    private IAnalyzer _firstAnalyzer = null;
    private IAnalyzer _secondAnalyzer = null;

    /**
     * First analyzer passes its result to second analyzer.
     */
    public AnalysisChain(IAnalyzer first, IAnalyzer second)
    {
        if (first == null || second == null)
        {
            throw new IllegalArgumentException(
                (first == null ? "first" : second) + "=null");
        }

        _firstAnalyzer = first;
        _secondAnalyzer = second;

        first.registerCallback(new Transformer(second));
    }

    // {{{ IAnalyzer interface implementation /////////////////////////////////

    public void registerCallback(ICallback<IOutputMessage> callback)
    {
        _secondAnalyzer.registerCallback(callback);
    }

    public void analyze(ParsedData parsedData)
    {
        _firstAnalyzer.analyze(parsedData);
    }

    // }}} IAnalyzer interface implementation /////////////////////////////////
}
