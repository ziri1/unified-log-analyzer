package unifiedloganalyzer.utils;

import unifiedloganalyzer.IOutputMessage;


/**
 *
 * @author Peter Trsko
 */
public class UnsupportedMessageException extends Exception
{
    public UnsupportedMessageException()
    {
        super();
    }

    public UnsupportedMessageException(String message)
    {
        super(message);
    }

    public UnsupportedMessageException(Class<? extends IOutputMessage> clazz)
    {
        super("`" + clazz.getCanonicalName() + "': Unsupported message type.");
    }
}
