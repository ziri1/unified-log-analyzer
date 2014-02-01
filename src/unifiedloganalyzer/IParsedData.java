package unifiedloganalyzer;

import java.lang.Appendable;

import trskop.IAppendTo;


/**
 * Interface for parser output.
 *
 * @author Peter Trsko
 */
public interface IParsedData extends IAppendTo
{
    /**
     * Get original (unparsed) message.
     *
     * @return
     *   Unparsed message as received by parser.
     */
    String getOriginalMessage();
}
