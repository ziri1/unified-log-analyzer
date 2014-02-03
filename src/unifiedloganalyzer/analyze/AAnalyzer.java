package unifiedloganalyzer.analyze;

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
        _callbacksManager = new CallbacksManager<>();
    }

    public AAnalyzer(ICallback<IOutputMessage> callback)
    {
        this();

        _callbacksManager.registerCallback(callback);
    }

    // }}} Constructors ///////////////////////////////////////////////////////

    // {{{ IAnalyzer implementation ///////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
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

    /**
     * Register object that should be notified when analysis result will be
     * available.
     * 
     * @param callback
     *   Object to be notified when analysis result is available.
     */
    @Override
    public void registerCallback(ICallback<IOutputMessage> callback)
    {
        _callbacksManager.registerCallback(callback);
    }

    // }}} IAnalyzer implementation ///////////////////////////////////////////

    /**
     * Notify registered objects with analysis results.
     * 
     * @param message
     *   Analysis result passed to resitered callback(s).
     */
    protected void runCallbacks(IOutputMessage message)
    {
        _callbacksManager.runCallbacks(message);
    }

    /**
     * Process data from ParsedData that are tagged as EMPTY_MESSAGE.
     * 
     * @param parsedData
     *   <code>null</code> or some specific message, depending on the protocol
     *   that between this analyzer and object that sent this message.
     */
    protected abstract void processEmptyMessage(IParsedData parsedData);
    
    /**
     * Process data from ParsedData that are tagged as PARSED_MESSAGE.
     * 
     * @param parsedData
     *   Parsed message as produced by object that sent it. It's not allowed to
     *   be <code>null</code>.
     */
    protected abstract void processParsedMessage(IParsedData parsedData);
    
    /**
     * Process data from ParsedData that are tagged as PARSE_ERROR.
     * 
     * @param parsedData
     *   Message that describes parse error or at least provides original
     *   (unparsed) message. It's not allowed to be <code>null</code>.
     */
    protected abstract void processParseError(IParsedData parsedData);
}
