package unifiedloganalyzer.utils.categorizer;

import unifiedloganalyzer.utils.IHasPath;
import unifiedloganalyzer.utils.IHasTags;


/**
 *
 * @author Peter Trsko
 */
public interface IAction
{
    public abstract IHasTags action(
            IPattern pattern,
            IHasPath path,
            IHasTags tags);
}
