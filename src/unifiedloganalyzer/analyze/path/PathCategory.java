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

    public static final String BUILD_SYSTEM = "^[Mm]akefile|.*\\.mk$";

    public static final String DATA_FILE = "^.*\\.xml$";

    public static final String SHELL_SCRIPT = "^.*\\.sh$";

    public static final String BACKUP_FILE = "^.*(~|\\.bak)$";

    public static final String WINDOWS_EXECUTABLE = "^.*\\.exe";

    public static final String WINDOWS_BATCH_FILE = "^.*\\.(bat|cmd)$";

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
    private Pattern _pattern = null;

    /**
     * Constructor for default case only.
     */
    private PathCategory()
    {
        _isDefault = true;
    }

    /**
     * Constructor for non default cases.
     *
     * @param regex
     *   Regular expression for this file category. It may be <code>null</code>
     *   for such cases when file category can not be determined from just file
     *   path.
     */
    private PathCategory(String regex)
    {
        _pattern = regex == null ? null : Pattern.compile(regex);
    }

    /**
     * Predicate that is <code>true</code> when this instance is a default case.
     *
     * @return
     *   <code>true</code> if this instance is default case and
     *   <code>false</code> otherwise.
     */
    public boolean isDefault()
    {
        return _isDefault;
    }

    /**
     * Gets Pattern compiled from regular expression associated with this
     * instance/case.
     *
     * @return
     *   Compiled regular expression corresponding to this case.
     */
    public Pattern getPattern()
    {
        return _pattern;
    }

    /**
     * Same as <code>getPattern().matcher(str)</code>, but it handles the case
     * when getPattern returns <code>null</code> correctly.
     *
     * @param str
     *   String to be matched against regular expression corresponding to this
     *   file category.
     *
     * @return
     *   Matcher object for <code>str</code> and pattern for this case, or
     *   <code>null</code> when getPattern would also return <code>null</code>.
     */
    public Matcher matcher(String str)
    {
        return _pattern == null ? null : _pattern.matcher(str);
    }
}
