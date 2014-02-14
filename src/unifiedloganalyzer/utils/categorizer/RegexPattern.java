package unifiedloganalyzer.utils.categorizer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import unifiedloganalyzer.utils.IHasPath;


/**
 *
 *
 * @author Peter Trsko
 */
public class RegexPattern implements IPattern
{
    private final Pattern _pattern;
    private Matcher _matcher = null;

    public RegexPattern(String regex)
    {
        if (regex == null)
        {
            throw new IllegalArgumentException("Regular expression has to be"
                + " provided, i.e. not null.");
        }
        _pattern = Pattern.compile(regex);
    }

    public RegexPattern(Pattern pattern)
    {
        if (pattern == null)
        {
            throw new IllegalArgumentException("Pattern has to be provided,"
                + " i.e. not null.");
        }
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
