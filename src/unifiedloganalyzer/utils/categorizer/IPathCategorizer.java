package unifiedloganalyzer.utils.categorizer;

import unifiedloganalyzer.utils.IHasPath;
import unifiedloganalyzer.utils.IHasTags;


/**
 *
 *
 * @author Peter Trsko
 */
public interface IPathCategorizer
{
    /**
     * Categorise path and modify specified tags accordingly.
     *
     * @param path
     *   Path to categorise.
     * @param tags
     *   Tags to be modified.
     */
    void categorize(IHasPath path, IHasTags tags);
}
