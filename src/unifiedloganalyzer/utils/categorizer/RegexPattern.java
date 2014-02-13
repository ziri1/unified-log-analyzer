package unifiedloganalyzer.utils.categorizer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import unifiedloganalyzer.utils.IHasPath;


/**
 *
 * @author Peter Trsko
 */
public class RegexPattern implements IPattern
{
    private final Pattern _pattern;
    private Matcher _matcher = null;

    public RegexPattern(String regex)
    {
        this(Pattern.compile(regex));
    }

    public RegexPattern(Pattern pattern)
    {
        _pattern = pattern;
    }

    // }}} Getters and setters ////////////////////////////////////////////////

    public Matcher getMatcher()
    {
        return _matcher;
    }

    // }}} Getters and setters ////////////////////////////////////////////////

    // {{{ IPattern interface implementation //////////////////////////////////

    @Override
    public boolean match(IHasPath path)
    {
        _matcher = _pattern.matcher(path.getPath());

        return _matcher.matches();
    }

    // }}} IPattern interface implementation //////////////////////////////////
}
