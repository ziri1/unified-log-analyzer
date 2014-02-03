package unifiedloganalyzer;

import trskop.IAppendTo;


/**
 * Interface for parser output.
 *
 * @author Peter Trsko
 */
public interface IParsedData extends IAppendTo
{
    /**
     * Gets original (unparsed) message.
     *
     * @return
     *   Unparsed message as received by parser.
     */
    String getOriginalMessage();
}
