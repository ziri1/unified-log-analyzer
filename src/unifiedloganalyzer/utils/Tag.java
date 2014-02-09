package unifiedloganalyzer.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import trskop.IAppendTo;


/**
 * Simple key=value pair, where key is hierarchical, used for storing meta-data
 * information element.
 *
 * @author Peter Trsko
 */
public class Tag implements IHasTags, IAppendTo
{
    private final String[] _name;
    private final String _value;

    // {{{ Constructors ///////////////////////////////////////////////////////

    /**
     * Construct tag in form name=value.
     *
     * @param name
     *   Hierarchical name of this tag. It neither may be <code>null</code> nor
     *   any of its elements can be <code>null</code> or empty strings.
     * @param value
     *   Value of this tag. It may not be <code>null</code>, but it may be an
     *   empty string.
     */
    public Tag(String[] name, String value)
    {
        if (name == null || name.length == 0)
        {
            throw new IllegalArgumentException("Tag name has to be provided.");
        }

        for (String elem : name)
        {
            if (elem == null || elem.isEmpty())
            {
                throw new IllegalArgumentException(
                    "All elements of Tag name has to be non-null and non-empty"
                    + " strings.");
            }
        }

        if (value == null)
        {
            throw new IllegalArgumentException(
                "Tag value has to be provided.");
        }

        _name = name;
        _value = value;
    }

    /**
     * Create tag with name split in to prefix and name (suffix).
     *
     * @param prefix
     *   Prefix of hierarchical name of this tag. It may not be
     *   <code>null</code>, but it may be without elements, i.e. of zero
     *   length. Any present element has to be non <code>null</code> and non
     *   empty string.
     * @param name
     *   Hierarchical name (suffix) of this tag. It neither may be
     *   <code>null</code> nor any of its elements can be <code>null</code> or
     *   empty strings.
     * @param value
     *   Value of this tag. It may not be <code>null</code>, but it may be an
     *   empty string.
     */
    public Tag(String[] prefix, String[] name, String value)
    {
        this(qualifiedName(prefix, name), value);
    }

    /**
     * Create qualified name from prefix and name (suffix).
     *
     * @param prefix
     *   Tag name prefix. Can not be <code>null</code> but may be of zero
     *   length.
     * @param name
     *   Tag name suffix. Can not be <code>null</code> and has to contain at
     *   least one element.
     * @return
     *   New array that is concatenation of both, prefix and name.
     */
    private static String[] qualifiedName(String[] prefix, String[] name)
    {
        String[] ret;
        int i;

        if (prefix == null || name == null)
        {
            throw new IllegalArgumentException(
                "Argument " + (prefix == null ? "prefix" : "name")
                + " has to be specified, i.e. not null.");
        }

        if (name.length == 0)
        {
            throw new IllegalArgumentException(
                "Argument name has to have at least one element.");
        }

        ret = new String[prefix.length + name.length];

        i = 0;
        for (String elem : prefix)
        {
            ret[i++] = elem;
        }
        for (String elem : name)
        {
            ret[i++] = elem;
        }

        return ret;
    }

    // }}} Constructors ///////////////////////////////////////////////////////

    /**
     * Gets name of this tag.
     *
     * @return
     *   Name of this tag.
     */
    public String[] getName()
    {
        return _name;
    }

    /**
     * Gets value of this tag.
     *
     * @return
     *   Value of this tag.
     */
    public String getValue()
    {
        return _value;
    }

    // {{{ IHasTags interface implementation //////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasTags()
    {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tag> getTags()
    {
        List<Tag> ret = new ArrayList<>();

        ret.add(new Tag(_name, _value));

        return ret;
    }

    // }}} IHasTags interface implementation //////////////////////////////////

    // {{{ IAppendTo interface implementation /////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public void appendTo(Appendable buff) throws IOException
    {
        buff.append(_name[0]);
        for (String elem : Arrays.copyOfRange(_name, 1, _name.length))
        {
            buff.append('.').append(elem);
        }
        // TODO: Handle escaping.
        buff.append("=\"").append(_value).append('"');
    }

    // }}} IAppendTo interface implementation /////////////////////////////////
}
