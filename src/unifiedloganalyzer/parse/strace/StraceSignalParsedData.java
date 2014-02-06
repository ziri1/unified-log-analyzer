package unifiedloganalyzer.parse.strace;

import java.io.IOException;

import unifiedloganalyzer.parse.AParsedData;
import unifiedloganalyzer.utils.IHasPid;


/**
 *
 * @author Peter Trsko
 */
public class StraceSignalParsedData extends AParsedData implements IHasPid
{
    // {{{ Private attributes /////////////////////////////////////////////////

    private int _pid = -1;
    private String _signal = null;
    private String _signalDescription = null;

    // }}} Private attributes /////////////////////////////////////////////////

    // {{{ Constructors ///////////////////////////////////////////////////////

    public StraceSignalParsedData(
        String originalMessage,
        String signal,
        String signalDescription)
    {
         super(originalMessage);

         _signal = signal;
         _signalDescription = signalDescription;
    }

    // }}} Constructors ///////////////////////////////////////////////////////

    // {{{ Getters and setters ////////////////////////////////////////////////

    public String getSignal()
    {
        return _signal;
    }


    public String getSignalDescription()
    {
        return _signalDescription;
    }


    @Override
    public int getPid()
    {
        return _pid;
    }

    @Override
    public void setPid(int pid)
    {
        _pid = pid;
    }

    // }}} Getters and setters ////////////////////////////////////////////////

    // {{{ Predicates /////////////////////////////////////////////////////////

    @Override
    public boolean hasPid()
    {
        return _pid != -1;
    }

    // }}} Predicates /////////////////////////////////////////////////////////

    // {{{ Implementation of abstract methods /////////////////////////////////

    @Override
    public void appendRestTo(Appendable buff) throws IOException
    {
        buff.append(", signal = ")
            .append(_signal)
            .append('\n');

        buff.append(", pid = ")
            .append(Integer.toString(_pid))
            .append('\n');
    }

    // }}} Implementation of abstract methods /////////////////////////////////
}
