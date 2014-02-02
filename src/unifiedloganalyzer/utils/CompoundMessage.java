package unifiedloganalyzer.utils;

import trskop.utils.AppendableLike;
import java.io.IOException;

import unifiedloganalyzer.IOutputMessage;
import unifiedloganalyzer.IParsedData;
import unifiedloganalyzer.utils.ICompoundMessage;


/**
 * Message that combines IOutputMessage and IParsedData in to one.
 *
 * @author Peter Trsko
 */
public class CompoundMessage<O extends IOutputMessage, P extends IParsedData>
    implements ICompoundMessage<O, P>
{
    private O _outputMessage = null;
    private P _parsedData = null;

    public CompoundMessage(O outputMessage, P parsedData)
    {
        _outputMessage = outputMessage;
        _parsedData = parsedData;
    }

    // {{{ ICompoundMessage interface implementation ///////////////////////////

    public O getOutputMessage()
    {
        return _outputMessage;
    }

    public P getParsedData()
    {
        return _parsedData;
    }

    public String getOriginalMessage()
    {
        if (_parsedData == null)
        {
            return null;
        }

        return _parsedData.getOriginalMessage();
    }

    public void appendTo(Appendable origBuff) throws IOException
    {
        (new AppendableLike(origBuff))
            .append("{ outputMessage = ")
            .append(_outputMessage)
            .append("\n, parsedData = ")
            .append(_parsedData)
            .append("\n}");
    }

    public boolean messageEquals(ICompoundMessage<?, ?> message)
    {
        if (message == null)
        {
            return false;
        }

        IOutputMessage ourOutputMessage = getOutputMessage();
        IOutputMessage theirOutputMessage = message.getOutputMessage();

        return (ourOutputMessage == null && theirOutputMessage == null)
            || (ourOutputMessage != null
                && ourOutputMessage.equals(theirOutputMessage));
    }

    public boolean messageEquals(IOutputMessage message)
    {
        IOutputMessage ourOutputMessage = getOutputMessage();

        return (ourOutputMessage != null && ourOutputMessage.equals(message));
    }

    // }}} ICompoundMessage interface implementation //////////////////////////
}
