package unifiedloganalyzer.utils.categorizer;

import unifiedloganalyzer.utils.IHasPath;


/**
 *
 * @author Peter Trsko
 */
public interface IPattern
{
    boolean match(IHasPath path);
}
