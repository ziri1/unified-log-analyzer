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
        IHasPath receivedPath,
        IHasTags receivedTags,
        IOutputMessage receivedMessage)
    {
        IHasTags tags = receivedTags;
        IOutputMessage result = receivedMessage;

        // We can't reuse received message if it's not capable of storing tags.
        if (receivedMessage == null || receivedTags == null)
        {
            PathOutputMessage msg;

            if (receivedTags == null)
            {
                msg = new PathOutputMessage(receivedPath.getPath());
            }
            else
            {
                msg = new PathOutputMessage(receivedPath.getPath(),
                    receivedTags.getTags());
            }

            tags = msg;
            result = msg;
        }

        _categorizer.categorize(receivedPath, tags);

        return result;
    }

    @Override
    protected void processParsedMessage(IParsedData parsedData)
    {
        IOutputMessage result = null;

        if (parsedData instanceof IOutputMessage)
        {
            // Try to reuse received message also as output if it's possible.
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

            result = categorize(path, tags, result);
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
