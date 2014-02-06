package unifiedloganalyzer.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import unifiedloganalyzer.IOutputMessage;
import unifiedloganalyzer.ISink;


/**
 *
 * @author Kamil Cupr
 */
public class FileSink implements ISink
{
    private File _file = null;
    private BufferedWriter _writer = null;
    private IOutputMessage _previousMessage = null;
    private boolean _countMessages = true;
    private int _messageCounter = 0;

    // {{{ Constructors ///////////////////////////////////////////////////////

    public FileSink(String file, boolean isGzipped, boolean countMessages)
        throws IOException
    {
        _file = new File(file);
        _writer = new BufferedWriter(new FileWriter(_file));
        _countMessages = countMessages;
    }

    public FileSink(String file, boolean isGzipped) throws IOException
    {
        this(file, isGzipped, true);
    }

    public FileSink(String file) throws IOException
    {
        this(file, false, true);
    }

    // }}} Constructors ///////////////////////////////////////////////////////

    private void doWrite() throws IOException
    {
        if (_previousMessage != null)
        {
            StringBuffer buff = new StringBuffer();

            // We do actually count messages all the time, but we print it only
            // when requested.
            if (_countMessages)
            {
                buff.append(_messageCounter).append(' ');
            }
            _previousMessage.appendTo(buff);
            _writer.write(buff.append('\n').toString());

            _previousMessage = null;
            _messageCounter = 0;
        }
    }

    // {{{ ISink implementation ///////////////////////////////////////////////

    @Override
    public void write(IOutputMessage data) throws IOException
    {
        if (_previousMessage == null || !_previousMessage.messageEquals(data))
        {
            doWrite();
            _previousMessage = data;
            _messageCounter = 1;
        }
        else if (_previousMessage != null)
        {
            _messageCounter++;
        }
    }

    @Override
    public void flush() throws IOException
    {
        doWrite();
        _writer.flush();
    }

    @Override
    public void close() throws IOException
    {
        flush();
        _writer.close();
    }

    // }}} ISink implementation ///////////////////////////////////////////////
}
