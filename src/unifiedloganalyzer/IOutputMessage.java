package unifiedloganalyzer;

import trskop.IAppendTo;


/**
 * Interface for analysis result/output.
 *
 * @author Peter Trsko
 */
public interface IOutputMessage extends IAppendTo
{
    /**
     * Compares messages two instances of IOutputMessage.
     *
     * Implementations of this method might be quite weaker then equals.
     *
     * @param message
     *   Compare message to this.
     *
     * @return
     *   <code>true</code> if messages are considered equal and
     *   <code>false</code> otherwise.
     */
    public boolean messageEquals(IOutputMessage message);
}
