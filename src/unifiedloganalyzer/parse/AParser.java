package unifiedloganalyzer.parse;

import trskop.ICallback;

import unifiedloganalyzer.IParser;
import unifiedloganalyzer.ParsedData;
import unifiedloganalyzer.utils.CallbacksManager;


/**
 * Abstract parser that implements callback management.
 *
 * @author Peter Trsko
 */
public abstract class AParser implements IParser
{
    private CallbacksManager<ParsedData> _callbacksManager = null;

    // {{{ Constructors ///////////////////////////////////////////////////////

    public AParser()
    {
        _callbacksManager = new CallbacksManager<>();
    }

    public AParser(ICallback<ParsedData> callback)
    {
        this();

        _callbacksManager.registerCallback(callback);
    }

    // }}} Constructors ///////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerCallback(ICallback<ParsedData> callback)
    {
        _callbacksManager.registerCallback(callback);
    }

    /**
     * Run all registered callbacks with parsedData as an argument.
     *
     * @param parsedData
     *   Object passed to all registered callbacks when executed.
     */
    protected void runCallbacks(ParsedData parsedData)
    {
        _callbacksManager.runCallbacks(parsedData);
    }
}
