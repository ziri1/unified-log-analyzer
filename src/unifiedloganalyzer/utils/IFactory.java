package unifiedloganalyzer.utils;


/**
 * Factory for creating instances of T.
 *
 * @author Peter Trsko
 * @param <T>
 *   Type of object to be created by this factory.
 */
public interface IFactory<T>
{
    /**
     * Create instance of T.
     *
     * @return
     *   Instance of type/class T.
     */
    T create();
}
