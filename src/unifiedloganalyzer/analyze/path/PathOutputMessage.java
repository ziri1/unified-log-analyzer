package unifiedloganalyzer.analyze.path;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import unifiedloganalyzer.IOutputMessage;
import unifiedloganalyzer.utils.IHasPath;
import unifiedloganalyzer.utils.IHasTags;
import unifiedloganalyzer.utils.Tag;


/**
 * Analysis result that has as its main purpose pass file path allong.
 *
 * @author Peter Trsko
 */
public class PathOutputMessage implements IOutputMessage, IHasPath, IHasTags
{
    private String _path;
    private List<Tag> _tags;

    // {{{ Constructors ///////////////////////////////////////////////////////

    /**
     * Construct PathOutputMessage with list of tags associated with it.
     *
     * @param path
     *   File path.
     * @param tags
     *   List of tags associated with this object. If tags are
     *   <code>null</code> then it is constructed with empty list of tags.
     */
    public PathOutputMessage(String path, List<Tag> tags)
    {
        _path = path;

        if (tags == null)
        {
            _tags = new ArrayList<>();
        }
        else
        {
            _tags = tags;
        }
    }

    /**
     * Construct PathOutputMessage without any tags associated with it.
     *
     * @param path
     *   File path.
     */
    public PathOutputMessage(String path)
    {
        this(path, null);
    }

    /**
     * Produce empty instance where path is set to <code>null</code>.
     *
     * @return
     *   New PathOutputMessage instance with path set to <code>null</code>.
     */
    public static PathOutputMessage empty()
    {
        return new PathOutputMessage(null);
    }

    // }}} Constructors ///////////////////////////////////////////////////////

    // {{{ IHasPath interface implementation //////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPath()
    {
        return _path;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPath(String path)
    {
        _path = path;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasPath()
    {
        return _path != null;
    }

    // }}} IHasPath interface implementation //////////////////////////////////

    // {{{ IOutputMessage interface implementation ////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public void appendTo(Appendable buff) throws IOException
    {
        buff.append("{path=\"")
            .append(_path)
            .append("\", tags=[");

        boolean isFirstTag = true;
        for (Tag tag : _tags)
        {
            if (isFirstTag)
            {
                isFirstTag = false;
            }
            else
            {
                buff.append(", ");
            }
            tag.appendTo(buff);
        }

        buff.append("]}");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean messageEquals(IOutputMessage message)
    {
        if (message != null && message instanceof PathOutputMessage)
        {
            String theirPath = ((PathOutputMessage)message).getPath();
            String ourPath = this.getPath();

            return (theirPath == null && ourPath == null)
                // Short-circuiting before calling equals on null.
                || (ourPath != null && ourPath.equals(theirPath));
        }

        return false;
    }

    // }}} IOutputMessage interface implementation ////////////////////////////

    // {{{ IHasTags interface implementation //////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasTags()
    {
        return !_tags.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tag> getTags()
    {
        return _tags;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addTag(Tag tag)
    {
        if (tag == null)
        {
            throw new IllegalArgumentException("Tag has to be specified.");
        }

        _tags.add(tag);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addTags(Tag[] tags)
    {
        if (tags == null)
        {
            throw new IllegalArgumentException("Tags has to be specified.");
        }

        for (Tag tag : tags)
        {
            if (tag == null)
            {
                throw new IllegalArgumentException(
                    "All tags has to be non null");
            }

            _tags.add(tag);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addTags(Iterable<Tag> tags)
    {
        if (tags == null)
        {
            throw new IllegalArgumentException("Tags has to be specified.");
        }

        for (Tag tag : tags)
        {
            if (tag == null)
            {
                throw new IllegalArgumentException(
                    "All tags has to be non null");
            }

            _tags.add(tag);
        }
    }

    // }}} IHasTags interface implementation //////////////////////////////////
}
