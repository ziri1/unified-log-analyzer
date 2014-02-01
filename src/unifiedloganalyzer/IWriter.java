package unifiedloganalyzer;

import java.io.IOException;

import unifiedloganalyzer.IOutputMessage;


/**
 *
 * @author Kamil Cupr
 */
public interface IWriter
{
    /**
     * Write message.
     *
     * @param message
     *   Message to write.
     */
    void write(IOutputMessage message) throws IOException;

    /**
     * Flush writer's internal buffers, may also perform flush on its backend.
     */
    void flush() throws IOException;

    /**
     * Notify writer that no further messages will be received.
     *
     * This has to write all remaining data, and flush and close writer's
     * backend. Receiving message after eof is considered unrecoverable error.
     */
    void eof() throws IOException;
}
