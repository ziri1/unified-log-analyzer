package unifiedloganalyzer.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;

import unifiedloganalyzer.ISource;


/**
 *
 * @author Kamil Cupr
 */
public class FileSource implements ISource
{
    private File _file = null;
    private BufferedReader _buffer = null;
    private String _currentLine = null;

    public FileSource(String file, boolean isGzipped)
        throws FileNotFoundException, IOException
    {
        _file = new File(file);
        _buffer = new BufferedReader(new FileReader(_file));
        _currentLine = _buffer.readLine();
    }

    public FileSource(String file) throws FileNotFoundException, IOException
    {
        this(file, false);
    }

    @Override
    public boolean hasNext()
    {
        return _currentLine != null;
    }

    @Override
    public String next() throws NoSuchElementException, IOException
    {
        String ret = _currentLine;

        if (!hasNext())
        {
            throw new NoSuchElementException();
        }

        // Returns null if EOF is reached.
        _currentLine = _buffer.readLine();

        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException
    {
        _buffer.close();
    }
}
