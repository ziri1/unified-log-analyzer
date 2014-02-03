package unifiedloganalyzer.adapter;

import java.io.IOException;
import java.lang.Appendable;
import java.util.logging.Level;
import java.util.logging.Logger;

import trskop.ICallback;

import unifiedloganalyzer.IOutputMessage;
import unifiedloganalyzer.IWriter;


/**
 * Adapts IWriter interface to ICallback&lt;IOutputMessage&gt; while also
 * proxying IWriter interface.
 *
 * @author Peter Trsko
 */
public class WriterCallback implements IWriter, ICallback<IOutputMessage>
{
    private final IWriter _writer;

    public WriterCallback(IWriter writer)
    {
        _writer = writer;
    }

    // {{{ IWriter interface implementation ///////////////////////////////////

    /**
     * {@inheritDoc}
     * 
     * @throws IOException
     *   If an I/O error occurs.
     */
    @Override
    public void write(IOutputMessage message) throws IOException
    {
        _writer.write(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flush() throws IOException
    {
        _writer.flush();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException
    {
        _writer.close();
    }

    // }}} IWriter interface implementation ///////////////////////////////////

    // {{{ ICallback<IOutputMessage> interface implementation /////////////////

    /**
     * Write analysis result.
     * 
     * @param message
     *   Analysis result to be written.
     */
    @Override
    public void runCallback(IOutputMessage message)
    {
        try
        {
            write(message);
        }
        catch (IOException ex)
        {
            Logger.getLogger(WriterCallback.class.getName())
                .log(Level.SEVERE, null, ex);
        }
    }

    // }}} ICallback<IOutputMessage> interface implementation /////////////////
}
