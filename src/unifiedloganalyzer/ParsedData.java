package unifiedloganalyzer;

import java.io.IOException;
import java.lang.Appendable;
import java.lang.IllegalArgumentException;

import trskop.IAppendTo;

import unifiedloganalyzer.IParsedData;


/**
 *
 * @author Peter Trsko
 */
public class ParsedData implements IParsedData
{
    public enum Type
    {
        PARSED_MESSAGE,
        PARSE_ERROR,
        EMPTY_MESSAGE;
    }

    private Type _dataType;
    private IParsedData _data;

    public ParsedData(Type dataType, IParsedData data)
    {
        if (dataType != Type.EMPTY_MESSAGE && data == null)
        {
            throw new IllegalArgumentException("null");
        }

        _dataType = dataType;
        _data = data;
    }

    public Type getType()
    {
        return _dataType;
    }

    public IParsedData getData()
    {
        return _data;
    }

    public String getOriginalMessage()
    {
        return _data.getOriginalMessage();
    }

    public void appendTo(Appendable buff) throws IOException
    {
        _data.appendTo(buff);
    }
}
