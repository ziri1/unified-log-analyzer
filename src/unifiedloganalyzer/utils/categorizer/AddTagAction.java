package unifiedloganalyzer.utils.categorizer;

import unifiedloganalyzer.utils.IHasPath;
import unifiedloganalyzer.utils.IHasTags;
import unifiedloganalyzer.utils.Tag;


/**
 *
 * @author Peter Trsko
 */
public class AddTagAction implements IAction
{
    private final String[] _tagPrefix;
    private final String[] _tagName;
    private final String _tagValue;

    // {{{ Constructors ///////////////////////////////////////////////////////

    public AddTagAction(String[] prefix, String[] name, String value)
    {
        _tagPrefix = prefix;
        _tagName = name;
        _tagValue = value;
    }

    public AddTagAction(String[] name, String value)
    {
        this(new String[0], name, value);
    }

    // }}} Constructors ///////////////////////////////////////////////////////

    @Override
    public void action(IPattern pattern, IHasPath path, IHasTags tags)
    {
        tags.addTag(new Tag(_tagPrefix, _tagName, _tagValue));
    }
}
