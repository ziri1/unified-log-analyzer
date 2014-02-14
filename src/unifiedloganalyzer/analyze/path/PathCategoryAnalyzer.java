package unifiedloganalyzer.analyze.path;

import unifiedloganalyzer.IOutputMessage;
import unifiedloganalyzer.IParsedData;
import unifiedloganalyzer.analyze.AAnalyzer;
import unifiedloganalyzer.utils.IHasPath;
import unifiedloganalyzer.utils.IHasTags;
import unifiedloganalyzer.utils.categorizer.IPathCategorizer;


/**
 *
 *
 * @author Peter Trsko
 */
public class PathCategoryAnalyzer extends AAnalyzer
{
    private final IPathCategorizer _categorizer;

    public PathCategoryAnalyzer(IPathCategorizer categorizer)
    {
        if (categorizer == null)
        {
            throw new IllegalArgumentException("Categorizer can't be null.");
        }
        _categorizer = categorizer;
    }

    @Override
    protected void processEmptyMessage(IParsedData parsedData)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private IOutputMessage categorize(
        IHasPath path,
        IHasTags useAsTags,
        IOutputMessage useAsResult)
    {
        IHasTags tags = useAsTags;
        IOutputMessage result = useAsResult;

        if (useAsResult == null)
        {
            PathOutputMessage msg;

            if (useAsTags == null)
            {
                msg = new PathOutputMessage(path.getPath());
            }
            else
            {
                msg = new PathOutputMessage(path.getPath(),
                    useAsTags.getTags());
            }

            tags = msg;
            result = msg;
        }

        _categorizer.categorize(path, tags);

        return result;
    }

    @Override
    protected void processParsedMessage(IParsedData parsedData)
    {
        IOutputMessage result = null;

        if (parsedData instanceof IOutputMessage)
        {
            result = (IOutputMessage)parsedData;
        }

        if (parsedData instanceof IHasPath)
        {
            IHasPath path = (IHasPath)parsedData;
            IHasTags tags = null;

            if (parsedData instanceof IHasTags)
            {
                tags = (IHasTags)parsedData;
            }

            categorize(path, tags, result);
        }

        if (result != null)
        {
            // TODO: Compound message when requested.
            runCallbacks(result);
        }
    }

    @Override
    protected void processParseError(IParsedData parsedData)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
