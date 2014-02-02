package unifiedloganalyzer.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import unifiedloganalyzer.IOutputMessage;
import unifiedloganalyzer.IWriter;


/**
 *
 * @author Kamil Cupr
 */
public class FileWriter implements IWriter
{
    private File myFile = null;
    private BufferedWriter myWriter = null;
    private IOutputMessage previous = null;
    private Integer counter = 0;
    
    public FileWriter(String sFile) throws IOException
    {
        myFile = new File(sFile);
        myWriter = new BufferedWriter(new java.io.FileWriter(myFile));
    }

    private void doWrite() throws IOException
    {
        if (previous != null)
        {
            StringBuffer buff = new StringBuffer();

            buff.append(counter.toString());
            buff.append(' ');
            previous.appendTo(buff);
            buff.append('\n');

            myWriter.write(buff.toString());

            previous = null;
            counter = 0;
        }
    }

    public void write(IOutputMessage data) throws IOException
    {
        if (previous == null || !previous.messageEquals(data))
        {
            doWrite();
            previous = data;
            counter = 1;
        }
        else if (previous != null)
        {
            counter++;
        }
    }

    public void flush() throws IOException
    {
        if (previous != null)
        {
            StringBuffer buff = new StringBuffer();

            buff.append(counter.toString());
            buff.append(' ');
            previous.appendTo(buff);
            buff.append('\n');

            myWriter.write(buff.toString());
            myWriter.flush();
            previous = null;
            counter = 0;
        }
    }

    public void close() throws IOException
    {
        doWrite();
        flush();
        myWriter.close();
    }
}
