package unifiedloganalyzer;

import java.io.IOException;

import trskop.IAppendTo;

import unifiedloganalyzer.IOutputMessage;
import unifiedloganalyzer.IParsedData;


/**
 * Top-level wrapper for parsed data.
 *
 * @author Peter Trsko
 */
public class ParsedData implements IParsedData, IOutputMessage
{
    /**
     * Type of encapsulated data.
     */
    public static enum Type
    {
        PARSED_MESSAGE,
        PARSE_ERROR,
        EMPTY_MESSAGE;
    }

    private Type _dataType;
    private IParsedData _data;

    // {{{ Constructors ///////////////////////////////////////////////////////

    /**
     * Constructor that produces ParsedData instances with type PARSED_MESSAGE.
     *
     * @param parsedData
     *   IParsedData instance describing message in parsed form.
     */
    public ParsedData(IParsedData parsedData)
    {
        this(Type.PARSED_MESSAGE, parsedData);
    }

    /**
     * Generic constructor for ParsedData.
     *
     * @param dataType
     *   Type of message, it can be PARSED_MESSAGE, PARSE_ERROR or
     *   EMPTY_MESSAGE.
     *
     * @param parsedData
     *   IParsedData instance describing message in parsed form in case of
     *   <code>dataType=PARSED_MESSAGE</code>; data that describe parse error
     *   in case of <code>dataType=PARSE_ERROR</code>; other value or
     *   <code>null</code> in case of <code>dataType=EMPTY_MESSAGE</code>;
     */
    public ParsedData(Type dataType, IParsedData parsedData)
    {
        if (dataType != Type.EMPTY_MESSAGE && parsedData == null)
        {
            throw new IllegalArgumentException("null");
        }

        _dataType = dataType;
        _data = parsedData;
    }

    /**
     * Simple wrapper around ParsedData constructor that returns correctly
     * formated empty message.
     *
     * @return
     *   Empty ParsedData instance.
     */
    public static ParsedData emptyMessage()
    {
        return new ParsedData(Type.EMPTY_MESSAGE, null);
    }

    /**
     * Simple wrapper around ParsedData constructor that returns correctly
     * formated parse error message.
     *
     * @return
     *   ParsedData instance that describes parse error.
     */
    public static ParsedData parseError(IParsedData data)
    {
        return new ParsedData(Type.PARSE_ERROR, data);
    }

    // }}} Constructors ///////////////////////////////////////////////////////

    // {{{ Getters and setters ////////////////////////////////////////////////

    /**
     * Get type of message (parsed message, parse error or empty message).
     *
     * @return
     *   Type of message, it can be PARSED_MESSAGE, PARSE_ERROR or
     *   EMPTY_MESSAGE.
     */
    public Type getType()
    {
        return _dataType;
    }

    /**
     * Get parsed data/AST.
     *
     * @return
     *   IParsedData instance describing message in parsed form.
     */
    public IParsedData getData()
    {
        return _data;
    }

    /**
     * {@inheritDoc}
     */
    public String getOriginalMessage()
    {
        return _data.getOriginalMessage();
    }

    // }}} Getters and setters ////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    public void appendTo(Appendable buff) throws IOException
    {
        _data.appendTo(buff);
    }

    /**
     * {@inheritDoc}
     */
    public boolean messageEquals(IOutputMessage message)
    {
        if (message instanceof ParsedData)
        {
            ParsedData casted = (ParsedData)message;
            IParsedData ourData = getData();
            IParsedData theirData = casted.getData();

            return getType() == casted.getType()
                && getOriginalMessage() == casted.getOriginalMessage()
                && ((ourData == null && theirData == null)
                    // We have to make sure that we don't get
                    // NullPointerException when calling equals.
                    || (ourData != null && ourData.equals(theirData)));
        }

        return false;
    }
}
