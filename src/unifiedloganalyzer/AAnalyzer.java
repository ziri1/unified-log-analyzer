package unifiedloganalyzer;

import java.lang.IllegalArgumentException;
import java.util.ArrayList;
import java.util.List;

import trskop.ICallback;

import unifiedloganalyzer.CallbacksManager;
import unifiedloganalyzer.IAnalyzer;
import unifiedloganalyzer.IOutputMessage;
import unifiedloganalyzer.IParsedData;


/**
 * Abstrac implementation of IAnalyzer that simplifies implementation of
 * concrete analyzers.
 *
 * @author Peter Trsko
 */
public abstract class AAnalyzer implements IAnalyzer
{
    private CallbacksManager<IOutputMessage> _callbacksManager;

    // {{{ Constructors ///////////////////////////////////////////////////////

    public AAnalyzer()
    {
        _callbacksManager = new CallbacksManager<IOutputMessage>();
    }

    public AAnalyzer(ICallback<IOutputMessage> callback)
    {
        this();

        _callbacksManager.registerCallback(callback);
    }

    // }}} Constructors ///////////////////////////////////////////////////////

    // {{{ IAnalyzer implementation ///////////////////////////////////////////

    public void analyze(ParsedData data)
    {
        if (data == null)
        {
            throw new IllegalArgumentException("null");
        }

        doAnalysis(data.getType(), data.getData());
    }

    public void registerCallback(ICallback<IOutputMessage> callback)
    {
        _callbacksManager.registerCallback(callback);
    }

    // }}} IAnalyzer implementation ///////////////////////////////////////////

    protected void runCallbacks(IOutputMessage message)
    {
        _callbacksManager.runCallbacks(message);
    }

    protected abstract void doAnalysis(ParsedData.Type dataType,
        IParsedData data);
}
