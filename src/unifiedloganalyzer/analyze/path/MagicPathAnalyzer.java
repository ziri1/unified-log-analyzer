package unifiedloganalyzer.analyze.path;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import nl.kb.magicfile.Check;
import nl.kb.magicfile.MagicFile;
import unifiedloganalyzer.IOutputMessage;

import unifiedloganalyzer.IParsedData;
import unifiedloganalyzer.analyze.AAnalyzer;
import unifiedloganalyzer.parse.DummyParsedData;
import unifiedloganalyzer.utils.ICompoundMessage;
import unifiedloganalyzer.utils.IHasPath;
import unifiedloganalyzer.utils.IHasTags;
import unifiedloganalyzer.utils.Tag;


/**
 * Analyzer that uses libmagic to tag paths with additional information.
 *
 * @author Peter Trsko
 */
public class MagicPathAnalyzer extends AAnalyzer
{
    // {{{ Constructing tags //////////////////////////////////////////////////

    private static final String[] _ROOT_TAG_NAME = new String[]{"path-info"};

    private static Tag newPathInfoTag(String[] name, String value)
    {
        return new Tag(_ROOT_TAG_NAME, name, value);
    }

    private static Tag newPathInfoTag(String name, String value)
    {
        return newPathInfoTag(new String[]{name}, value);
    }

    private static Tag newMagicTag(String name, String value)
    {
        return newPathInfoTag(new String[]{"magic", name}, value);
    }

    private static Tag newMagicTag(Check check, String result)
    {
        return newMagicTag(check.toString().toLowerCase(), result);
    }

    // }}} Constructing tags //////////////////////////////////////////////////

    private static void analyzePathWithMagic(File file, List<Tag> tags)
    {
        Map<Check, String> magicResult;

        try
        {
            magicResult = new MagicFile(file)
                .characterize(Arrays.asList(Check.values()));
        }
        catch (IOException e)
        {
            magicResult = null;
            tags.add(newPathInfoTag("magic-exception", e.toString()));
        }

        if (magicResult != null)
        {
            for (Check check : magicResult.keySet())
            {
                tags.add(newMagicTag(check, magicResult.get(check)));
            }
        }
    }

    private static List<Tag> analyzePath(String path)
    {
        File file = new File(path);
        List<Tag> tags = new ArrayList<>();

        if (file.exists())
        {
            String canonicalPath;

            tags.add(newPathInfoTag("exists", "true"));
            analyzePathWithMagic(file, tags);

            try
            {
                canonicalPath = file.getCanonicalPath();
            }
            catch (IOException e)
            {
                canonicalPath = null;
                tags.add(newPathInfoTag("canonical-path-exception",
                    e.toString()));
            }

            if (canonicalPath != null)
            {
                tags.add(newPathInfoTag("canonical-path", canonicalPath));
            }
        }
        else
        {
            tags.add(newPathInfoTag("exists", "false"));
        }

        return tags;
    }

    // {{{ Construct result ///////////////////////////////////////////////////

    private static IOutputMessage constructResult(
        String path,
        List<Tag> theirsTags,
        List<Tag> ourTags)
    {
        List<Tag> tags = new ArrayList<>(theirsTags);

        tags.addAll(ourTags);

        return new PathOutputMessage(path, tags);
    }

    private static IOutputMessage constructResult(String path, List<Tag> tags)
    {
        return new PathOutputMessage(path, tags);
    }

    // }}} Construct result ///////////////////////////////////////////////////

    // {{{ AAnalyzer, implementation of abstract methods //////////////////////

    @Override
    protected void processEmptyMessage(IParsedData parsedData)
    {
        // Empty implementation.
    }

    private String getPathFromIHasPathOrDummyParsedData(IParsedData parsedData)
    {
        if (parsedData instanceof IHasPath)
        {
            return ((IHasPath)parsedData).getPath();
        }
        else if (parsedData instanceof DummyParsedData)
        {
            return parsedData.getOriginalMessage();
        }

        return null;
    }

    @Override
    protected void processParsedMessage(IParsedData parsedData)
    {
        String path = null;

        if (parsedData instanceof ICompoundMessage)
        {
            // This is what we get wen we are in AnalysisChain and previous
            // analyzer sent pure IOutputMessage.
            IOutputMessage message =
                ((ICompoundMessage)parsedData).getOutputMessage();

            if (message instanceof IHasPath)
            {
                path = ((IHasPath)message).getPath();
            }
        }
        else
        {
            path = getPathFromIHasPathOrDummyParsedData(parsedData);
        }

        if (path != null)
        {
            List<Tag> tags = analyzePath(path);
            IOutputMessage result;

            if (parsedData instanceof IHasTags)
            {
                if (parsedData instanceof IOutputMessage)
                {
                    ((IHasTags)parsedData).addTags(tags);
                    result = (IOutputMessage)parsedData;
                }
                else
                {
                    result = constructResult(path,
                        ((IHasTags)parsedData).getTags(), tags);
                }
            }
            // TODO: Handle instanceof ICompoundMessage when its IOutputMessage
            //       implements IHasTags (should be similar to the above).
            else
            {
                result = constructResult(path, tags);
            }

            this.runCallbacks(result);
        }
    }

    @Override
    protected void processParseError(IParsedData parsedData)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // }}} AAnalyzer, implementation of abstract methods //////////////////////
}
