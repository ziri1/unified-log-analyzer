package unifiedloganalyzer.utils;

import unifiedloganalyzer.IOutputMessage;
import unifiedloganalyzer.IParsedData;


/**
 * Interface for messages that combine IOutputMessage and IParsedData in to
 * one instance.
 *
 * @author Peter Trsko
 * @param <O>
 *   Type of output message.
 * @param <P>
 *   Type of parsed data message.
 */
public interface ICompoundMessage<O extends IOutputMessage, P extends IParsedData>
    extends IOutputMessage, IParsedData
{
    /**
     * Get value of IOutputMessage instance.
     *
     * @return
     *   Output message.
     */
    O getOutputMessage();

    /**
     * Get value of IParsedData instance.
     *
     * @return
     *   Parsed data message.
     */
    P getParsedData();
}
