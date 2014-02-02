package unifiedloganalyzer.analyze;

import java.lang.IllegalArgumentException;
import java.util.ArrayList;
import java.util.List;

import trskop.ICallback;

import unifiedloganalyzer.IAnalyzer;
import unifiedloganalyzer.IOutputMessage;
import unifiedloganalyzer.IParsedData;
import unifiedloganalyzer.ParsedData;
import unifiedloganalyzer.utils.CallbacksManager;


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

    public void analyze(ParsedData parsedData)
    {
        if (parsedData == null)
        {
            throw new IllegalArgumentException("null");
        }

        switch (parsedData.getType())
        {
            case EMPTY_MESSAGE:
                processEmptyMessage(parsedData.getData());
                break;

            case PARSED_MESSAGE:
                processParsedMessage(parsedData.getData());
                break;

            case PARSE_ERROR:
                processParseError(parsedData.getData());
                break;
        }
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

    protected abstract void processEmptyMessage(IParsedData parsedData);
    protected abstract void processParsedMessage(IParsedData parsedData);
    protected abstract void processParseError(IParsedData parsedData);
}
