package unifiedloganalyzer;


/**
 * Generic interface for objects capable of parsing messages.
 *
 * @author Kamil Cupr
 */
public interface IParser extends IRegisterCallbacks<ParsedData>
{
    /**
     * Parses specified message.
     * 
     * Registered callbacks might not be notified immediately, because
     * producing valid ParsedData might require more then one message to be
     * processed.
     *
     * @param message
     *   Message to be parsed.
     * 
     * @see ParsedData
     */
    void parse(String message);

    /**
     * Notify parser that file/stream ended.
     *
     * Sending another message for parsing after this is not an error. It just
     * indicates to the parser that any state it kept has to be discarded,
     * because another file/stream might be sent for parsing.
     */
    void eof();
}
