package unifiedloganalyzer.strace;

import java.io.IOException;
import java.lang.Appendable;

import trskop.IAppendTo;

import unifiedloganalyzer.AParsedData;


/**
 * Parsed process status change message.
 *
 * @author Peter Trsko
 */
public class StraceProcessStatusChangedParsedData extends AParsedData
    implements IHasPid
{
    // {{{ Nested types ///////////////////////////////////////////////////////

    public enum Type implements IAppendTo
    {
        ATTACHED,
        DETACHED,
        RESUMED,
        SUSPENDED;

        public static Type fromString(String str)
        {
            switch (str)
            {
                case "attached":
                    return Type.ATTACHED;
                case "detached":
                    return Type.DETACHED;
                case "resumed":
                    return Type.RESUMED;
                case "suspended":
                    return Type.SUSPENDED;
            }

            return null;
        }

        public void appendTo(Appendable buff) throws IOException
        {
            buff.append(this.toString());
        }
    }

    // }}} Nested types ///////////////////////////////////////////////////////

    // {{{ Private attributes /////////////////////////////////////////////////

    private int _pid = -1;
    private Type _type;

    // }}} Private attributes /////////////////////////////////////////////////

    // {{{ Constructors ///////////////////////////////////////////////////////

    public StraceProcessStatusChangedParsedData(
        String originalMessage,
        String type)
    {
         super(originalMessage);

         _type = Type.fromString(type);

         if (_type == null)
         {
             throw new IllegalArgumentException(type);
         }
    }

    // }}} Constructors ///////////////////////////////////////////////////////

    // {{{ Getters and setters ////////////////////////////////////////////////

    public int getPid()
    {
        return _pid;
    }

    public void setPid(int pid)
    {
        _pid = pid;
    }


    public Type getType()
    {
        return _type;
    }

    // }}} Getters and setters ////////////////////////////////////////////////

    // {{{ Predicates /////////////////////////////////////////////////////////

    public boolean hasPid()
    {
        return _pid != -1;
    }

    // }}} Predicates /////////////////////////////////////////////////////////

    // {{{ Implementation of abstract methods /////////////////////////////////

    public void appendRestTo(Appendable buff) throws IOException
    {
        buff.append(", pid = ")
            .append(Integer.toString(_pid))
            .append('\n');

        buff.append(", type = ");
        _type.appendTo(buff);
        buff.append('\n');
    }

    // }}} Implementation of abstract methods /////////////////////////////////
}
