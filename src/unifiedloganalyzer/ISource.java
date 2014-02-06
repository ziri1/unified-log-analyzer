package unifiedloganalyzer;

import java.io.Closeable;
import java.io.IOException;
import java.util.NoSuchElementException;


/**
 * Interface for objects that provide input data that will be later processed
 * by IParser objects. Basically it's a re-implementation of specialized
 * Iterator interface, only this one is little more I/O friendly.
 *
 * @author Kamil Cupr
 * @author Peter Trsko
 */
public interface ISource extends Closeable
{
    /**
     * Returns <code>true</code> if there is another message available and
     * <code>false</code> otherwise.
     *
     * @return
     *   <code>true</code> if another message is available and
     *   <code>false</code> otherwise.
     */
    boolean hasNext();

    /**
     * Returns next message.
     *
     * @return
     *   Next message from this source.
     *
     * @throws NoSuchElementException
     *   If there is no more messages available.
     * @throws IOException
     *   If the lower layer failed due to I/O error.
     */
    String next() throws NoSuchElementException, IOException;
}
