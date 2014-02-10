package unifiedloganalyzer.analyze.path;

import java.util.List;

import unifiedloganalyzer.IParsedData;
import unifiedloganalyzer.utils.CompoundMessage;
import unifiedloganalyzer.utils.IHasPath;
import unifiedloganalyzer.utils.IHasTags;
import unifiedloganalyzer.utils.Tag;


/**
 * Compound of PathCompoundMessage and some IParsedData instance.
 *
 * Message that contains file path result on one hand and its origin in, parsed
 * and unparsed form, on the other.
 *
 * @author Peter Trsko
 */
public class PathCompoundMessage
    extends CompoundMessage<PathOutputMessage, IParsedData>
    implements IHasPath, IHasTags
{
    // {{{ Constructors ///////////////////////////////////////////////////////

    public PathCompoundMessage(String path, IParsedData parsedData)
    {
        super(new PathOutputMessage(path), parsedData);
    }

    public PathCompoundMessage(IParsedData parsedData)
    {
        super(PathOutputMessage.empty(), parsedData);
    }

    /**
     * Create new empty instance.
     *
     * @return
     *   New empty message.
     */
    public static PathCompoundMessage empty()
    {
        return new PathCompoundMessage(null);
    }

    // }}} Constructors ///////////////////////////////////////////////////////

    // {{{ IHasPath and IParsedData interface implementation //////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPath()
    {
        return getOutputMessage().getPath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPath(String path)
    {
        getOutputMessage().setPath(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasPath()
    {
        return getOutputMessage().hasPath();
    }

    // }}} IHasPath and IParsedData interface implementation //////////////////

    // {{{ IHasTags interface implementation //////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasTags()
    {
        return getOutputMessage().hasTags();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tag> getTags()
    {
        return getOutputMessage().getTags();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addTag(Tag tag)
    {
        getOutputMessage().addTag(tag);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addTags(Tag[] tags)
    {
        getOutputMessage().addTags(tags);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addTags(Iterable<Tag> tags)
    {
        getOutputMessage().addTags(tags);
    }

    // }}} IHasTags interface implementation //////////////////////////////////
}
