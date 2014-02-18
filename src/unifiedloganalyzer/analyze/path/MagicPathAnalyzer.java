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
        try
        {
            Map<Check, String> magicResult = new MagicFile(file)
                .characterize(Arrays.asList(Check.values()));

            for (Check check : magicResult.keySet())
            {
                tags.add(newMagicTag(check, magicResult.get(check)));
            }
        }
        catch (IOException e)
        {
            tags.add(newPathInfoTag("magic-exception", e.toString()));
        }
    }

    private static List<Tag> analyzePath(String path)
    {
        File file = new File(path);
        List<Tag> tags = new ArrayList<>();

        if (file.exists())
        {
            tags.add(newPathInfoTag("exists", "true"));
            analyzePathWithMagic(file, tags);   // Tags are modified.

            try
            {
                tags.add(newPathInfoTag("canonical-path",
                    // This is what may throw exception:
                    file.getCanonicalPath()));
            }
            catch (IOException e)
            {
                tags.add(newPathInfoTag("canonical-path-exception",
                    e.toString()));
            }
        }
        else
        {
            tags.add(newPathInfoTag("exists", "false"));
        }

        return tags;
    }

    // {{{ Construct result ///////////////////////////////////////////////////

    private static IOutputMessage constructNewResult(
        String path,
        List<Tag> theirsTags,
        List<Tag> ourTags)
    {
        List<Tag> tags = new ArrayList<>(theirsTags);

        tags.addAll(ourTags);

        return new PathOutputMessage(path, tags);
    }

    private static IOutputMessage constructNewResult(
        String path,
        List<Tag> tags)
    {
        return new PathOutputMessage(path, tags);
    }

    private static IOutputMessage constructResult(
        IParsedData parsedData,
        List<Tag> tags,
        String path)
    {
        if (parsedData instanceof IHasTags)
        {
            if (parsedData instanceof IOutputMessage)
            {
                // We are reusing object we received for output as well.
                // TODO: In some cases this might not be desirable, create
                //       option for changing this behaviour.
                ((IHasTags)parsedData).addTags(tags);

                return (IOutputMessage)parsedData;
            }

            // Preserve original metadata, since we aren't goint to reuse
            // received message.
            return constructNewResult(path,
                ((IHasTags)parsedData).getTags(), tags);
        }

        // TODO: Handle instanceof ICompoundMessage when its IOutputMessage
        //       implements IHasTags (should be similar to the above).

        return constructNewResult(path, tags);
    }

    // }}} Construct result ///////////////////////////////////////////////////

    /**
     * Gets path from received message (parsed data).
     *
     * @param parsedData
     *   Received message.
     * @return
     *   Path contained in received message or <code>null</code> if it doesn't
     *   contain any.
     */
    private static String getPath(IParsedData parsedData)
    {
        if (parsedData instanceof IHasPath)
        {
            return ((IHasPath)parsedData).getPath();
        }

        if (parsedData instanceof ICompoundMessage)
        {
            // This is what we get wen we are in AnalysisChain and previous
            // analyzer sent us pure IOutputMessage.
            IOutputMessage message =
                ((ICompoundMessage)parsedData).getOutputMessage();

            if (message instanceof IHasPath)
            {
                return ((IHasPath)message).getPath();
            }
        }

        // Dummy message is quite a special case, we might want to make a
        // subclass (of it) for our purpose in the future.
        if (parsedData instanceof DummyParsedData)
        {
            return parsedData.getOriginalMessage();
        }

        return null;
    }

    // {{{ AAnalyzer, implementation of abstract methods //////////////////////

    @Override
    protected void processEmptyMessage(IParsedData parsedData)
    {
        // Empty implementation.
    }

    @Override
    protected void processParsedMessage(IParsedData parsedData)
    {
        String path = getPath(parsedData);

        if (path != null)
        {
            this.runCallbacks(
                // We need to pass ParsedData to preserve metadata or even
                // whole message in some cases.
                constructResult(parsedData, analyzePath(path), path));
        }

        // TODO: We might also want to do other things then just ignore other
        //       messages. Like resend them if they are also instances of
        //       IOutputMessage or throw an exception, etc. This should be
        //       configurable or subject to template method pattern.
    }

    @Override
    protected void processParseError(IParsedData parsedData)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // }}} AAnalyzer, implementation of abstract methods //////////////////////
}
