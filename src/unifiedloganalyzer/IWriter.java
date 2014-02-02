package unifiedloganalyzer;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;

import unifiedloganalyzer.IOutputMessage;


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
     */
    void write(IOutputMessage message) throws IOException;
}
