package unifiedloganalyzer.utils;

import java.util.List;


/**
 * Interface for objects that can be tagged with hierarchically structured
 * meta-data in key=value format.
 *
 * @author Peter Trsko
 */
public interface IHasTags
{
    /**
     * Returns <code>true</code> if there are any tags associated with this
     * object.
     *
     * @return
     *   <code>true</code> when there are tags associated with this object and
     *   <code>false</code> otherwise.
     */
    boolean hasTags();

    /**
     * Gets tags associated with this object.
     *
     * @return
     *   List of tags associated with this object.
     */
    List<Tag> getTags();
}
