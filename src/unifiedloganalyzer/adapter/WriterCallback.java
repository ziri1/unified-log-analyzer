package unifiedloganalyzer.adapter;

import java.io.IOException;
import java.lang.Appendable;
import java.util.logging.Level;
import java.util.logging.Logger;

import trskop.ICallback;

import unifiedloganalyzer.IOutputMessage;
import unifiedloganalyzer.IWriter;


/**
 *
 * @author TP000001 (Peter Trsko)
 */
public class WriterCallback implements IWriter, ICallback<IOutputMessage>
{
    private IWriter _writer;

    public WriterCallback(IWriter writer)
    {
        _writer = writer;
    }

    public void write(IOutputMessage message) throws IOException
    {
        _writer.write(message);
    }

    public void flush() throws IOException
    {
        _writer.flush();
    }

    public void eof() throws IOException
    {
        _writer.eof();
    }

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
}
