package unifiedloganalyzer.analyze.path;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Regular expressions used in PathCategory enum.
 *
 * @author Peter Trsko
 */
final class Regex
{
    /**
     * Has overlap wit _SYSTEM_LIBRARY and therefore has to be first.
     *
     * E.g. on Debian some GCC specific includes can be found here:
     * <code>/usr/lib/gcc/x86_64-linux-gnu/&lt;gcc-version&gt;/include</code>
     */
    public static final
        String SYSTEM_INCLUDE = "^(/usr/include/.*|/usr/lib/.*\\.h)$";

    public static final
        String SYSTEM_LIBRARY = "^/(usr/(local/)?)?lib/.*$";

    public static final String SELINUX_FS = "^/(sys/fs/)?selinux(/.*)?$";

    public static final String PROC_FS = "^/proc/.*$";

    public static final String SYS_FS = "^/sys/.*$";

    public static final String DEVICE = "^/dev/.*$";

    public static final String SYSTEM_CONFIG = "^/etc/.*$";

    public static final
        String SYSTEM_EXECUTABLE = "^/(usr/(local/)?)?s?bin/.*$";

    public static final String SOURCE_CODE = "^.*\\.(c|h|C|cpp|cxx|java)$";

    public static final String BUILD_SYSTEM = "^makefile|.*\\.mk$";

    public static final String DATA_FILE = "^.*\\.xml";

    // TODO:
    //   - .sh - Shell scripts.
    //   - Executables, some specific build paths or just determined by the
    //     fact that exec() was involved.
}


/**
 *
 * @author Peter Trsko
 */
public enum PathCategory
{
    BUILD_SYSTEM(Regex.BUILD_SYSTEM),
    DATA_FILE(Regex.DATA_FILE),
    DEVICE(Regex.DEVICE),
    EXECUTABLE(null),
    PROC_FS(Regex.PROC_FS),
    SELINUX_FS(Regex.SELINUX_FS),
    SOURCE_CODE(Regex.SOURCE_CODE),
    SYS_FS(Regex.SYS_FS),
    SYSTEM_CONFIG(Regex.SYSTEM_CONFIG),
    SYSTEM_EXECUTABLE(Regex.SYSTEM_EXECUTABLE),
    SYSTEM_INCLUDE(Regex.SYSTEM_INCLUDE),
    SYSTEM_LIBRARY(Regex.SYSTEM_LIBRARY),

    OTHER();

    private boolean _isDefault = false;
    private Pattern _pattern;

    private PathCategory()
    {
        _isDefault = true;
        _pattern = null;
    }

    private PathCategory(String regex)
    {
        _isDefault = false;
        _pattern = regex == null ? null : Pattern.compile(regex);
    }

    public boolean isDefault()
    {
        return _isDefault;
    }

    public Pattern getPattern()
    {
        return _pattern;
    }

    public Matcher matcher(String str)
    {
        return _pattern.matcher(str);
    }
}
