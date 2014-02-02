package unifiedloganalyzer.utils;


/**
 * Utility interface for objects with associated (file) path.
 *
 * @author Peter Trsko
 */
public interface IHasPath
{
    /**
     * Get value of file path associated with this object.
     *
     * @return
     *   File path associated with this object.
     */
    String getPath();

    /**
     * Change value of file path that is associated with this object.
     *
     * @param path
     *   New value of file path that is relevant to this instance.
     */
    void setPath(String path);

    /**
     * Check if this object has associated file path with it.
     *
     * @return
     *   <code>true</code> if there is available file path associated with this
     *   object and <code>false</code> otherwise.
     */
    boolean hasPath();
}
