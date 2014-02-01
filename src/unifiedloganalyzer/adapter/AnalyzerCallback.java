package unifiedloganalyzer.adapter;

import trskop.ICallback;

import unifiedloganalyzer.IOutputMessage;
import unifiedloganalyzer.IAnalyzer;
import unifiedloganalyzer.ParsedData;


/**
 *
 * @author TP000001 (Peter Trsko)
 */
public class AnalyzerCallback implements IAnalyzer, ICallback<ParsedData>
{
    private IAnalyzer _analyzer;

    // {{{ Constructors ///////////////////////////////////////////////////////

    public AnalyzerCallback(IAnalyzer analyzer)
    {
        _analyzer = analyzer;
    }

    // }}} Constructors ///////////////////////////////////////////////////////

    // {{{ IAnalyzer implementation ///////////////////////////////////////////

    public void runCallback(ParsedData message)
    {
        analyze(message);
    }

    // }}} IAnalyzer implementation ///////////////////////////////////////////

    // {{{ ICallback<IOutputMessage> implementation ///////////////////////////

    public void analyze(ParsedData data)
    {
        _analyzer.analyze(data);
    }

    public void registerCallback(ICallback<IOutputMessage> callback)
    {
        _analyzer.registerCallback(callback);
    }

    // }}} ICallback<IOutputMessage> implementation ///////////////////////////
}
