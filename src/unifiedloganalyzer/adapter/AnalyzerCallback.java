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
    private final IAnalyzer _analyzer;

    // {{{ Constructors ///////////////////////////////////////////////////////

    public AnalyzerCallback(IAnalyzer analyzer)
    {
        _analyzer = analyzer;
    }

    // }}} Constructors ///////////////////////////////////////////////////////

    // {{{ IAnalyzer implementation ///////////////////////////////////////////

    /**
     * Analyze parsed message.
     * 
     * This method is invoked by the object to which this instance is
     * registered to.
     * 
     * @param message
     *   Parsed message to be analyzed.
     * 
     * @see unifiedloganalyzer.IRegisterCallbacks
     */
    @Override
    public void runCallback(ParsedData message)
    {
        analyze(message);
    }

    // }}} IAnalyzer implementation ///////////////////////////////////////////

    // {{{ ICallback<IOutputMessage> implementation ///////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public void analyze(ParsedData parsedData)
    {
        _analyzer.analyze(parsedData);
    }

    /**
     * Register object that should be notified when analysis result(s) will be
     * available.
     * 
     * @param callback
     *   Object to be notified when analysis result(s) will be available.
     */
    @Override
    public void registerCallback(ICallback<IOutputMessage> callback)
    {
        _analyzer.registerCallback(callback);
    }

    // }}} ICallback<IOutputMessage> implementation ///////////////////////////
}
