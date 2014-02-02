package unifiedloganalyzer.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import unifiedloganalyzer.IOutputMessage;
import unifiedloganalyzer.IWriter;


/**
 *
 * @author Peter Trsko
 */
public class StdoutWriter implements IWriter
{
    private IOutputMessage _previousMessage = null;
    private int _counter = 0;

    // {{{ Private methods ////////////////////////////////////////////////////

    private void setPrevious(IOutputMessage message)
    {
        _previousMessage = message;
        _counter = message == null ? 0 : 1;
    }

    private boolean havePrevious()
    {
        return _previousMessage != null;
    }

    private boolean isNewMessage(IOutputMessage message)
    {
        return !havePrevious() || !_previousMessage.messageEquals(message);
    }

    private void doWrite() throws IOException
    {
        if (havePrevious())
        {
            StringBuffer buff = new StringBuffer();

            buff.append(Integer.toString(_counter));
            buff.append(' ');
            _previousMessage.appendTo(buff);
            buff.append('\n');

            System.out.println(buff.toString());

            setPrevious(null);
        }
    }

    private void incCounter()
    {
        _counter++;
    }

    // }}} Private methods ////////////////////////////////////////////////////

    // {{{ IWriter implementation /////////////////////////////////////////////

    public void write(IOutputMessage message) throws IOException
    {
        if (isNewMessage(message))
        {
            doWrite();
            setPrevious(message);
        }
        else
        {
            incCounter();
        }
    }

    public void flush() throws IOException
    {
        doWrite();
        System.out.flush();
    }

    public void close() throws IOException
    {
        flush();
        System.out.close();     // XXX
    }

    // }}} IWriter implementation /////////////////////////////////////////////
}
