package unifiedloganalyzer.utils.categorizer;

import java.util.HashMap;
import java.util.Map;

import unifiedloganalyzer.utils.IHasPath;
import unifiedloganalyzer.utils.IHasTags;


/**
 *
 * @author Peter Trsko
 */
public class PathCategorizer implements IPathCategorizer
{
    private final Map<IPattern, IAction> _patternsAndActions;

    public PathCategorizer()
    {
        _patternsAndActions = new HashMap<>();
    }

    public void registerAction(IPattern pattern, IAction action)
    {
        _patternsAndActions.put(pattern, action);
    }

    @Override
    public void categorize(IHasPath path, IHasTags tags)
    {
        for (IPattern pattern : _patternsAndActions.keySet())
        {
            if (pattern.match(path))
            {
                _patternsAndActions.get(pattern).action(pattern, path, tags);
            }
        }
    }
}
