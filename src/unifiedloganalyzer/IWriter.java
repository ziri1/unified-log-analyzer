package unifiedloganalyzer;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;


/**
 * Interface for various output backends (file, pipe, etc.).
 *
 * @author Kamil Cupr
 */
public interface IWriter extends Closeable, Flushable
{
    /**
     * Write message to backend (file, pipe, etc.).
     *
     * @param message
     *   Message to write.
     * 
     * @throws IOException
     *   If an I/O error occurs.
     */
    void write(IOutputMessage message) throws IOException;
}
