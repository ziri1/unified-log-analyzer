package unifiedloganalyzer.utils;

import unifiedloganalyzer.IOutputMessage;
import unifiedloganalyzer.IParsedData;


/**
 * Interface for messages that combine IOutputMessage and IParsedData in to
 * one instance.
 *
 * @author Peter Trsko
 */
public interface ICompoundMessage<O extends IOutputMessage, P extends IParsedData>
    extends IOutputMessage, IParsedData
{
    /**
     * Get value of IOutputMessage instance.
     */
    O getOutputMessage();

    /**
     * Get value of IParsedData instance.
     */
    P getParsedData();
}
