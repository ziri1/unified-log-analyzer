package unifiedloganalyzer.io;

import java.io.IOException;

import unifiedloganalyzer.IOutputMessage;
import unifiedloganalyzer.ISink;


/**
 *
 * @author Peter Trsko
 */
public class StdoutSink implements ISink
{
    private IOutputMessage _previousMessage = null;
    private int _counter = 0;

    // {{{ Private methods ////////////////////////////////////////////////////

    private void setPrevious(IOutputMessage message)
    {
        _previousMessage = message;
        _counter = message == null ? 0 : 1;
    }

    private void clearPrevious()
    {
        setPrevious(null);
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

            buff.append(_counter).append(' ');
            _previousMessage.appendTo(buff);

            System.out.println(buff.toString());

            clearPrevious();
        }
    }

    private void incCounter()
    {
        _counter++;
    }

    // }}} Private methods ////////////////////////////////////////////////////

    // {{{ ISink implementation ///////////////////////////////////////////////

    @Override
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

    @Override
    public void flush() throws IOException
    {
        doWrite();
        System.out.flush();
    }

    @Override
    public void close() throws IOException
    {
        flush();
        System.out.close();     // XXX
    }

    // }}} ISink implementation ///////////////////////////////////////////////
}
