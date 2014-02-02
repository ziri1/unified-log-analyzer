package unifiedloganalyzer.main;


/**
 * Supported input log formats.
 *
 * This enum is used to select correct parser and limit what analyzers can be
 * used. Not every analyzer can understand all input formats.
 *
 * @author Peter Trsko
 */
public enum InputFormat
{
    /**
     * Not a real format, it corresponds with DummyParser which just passes
     * what it gets to analyzer wrapped inside DummyParsedData.
     */
    DUMMY,

    /**
     * Output of strace utility.
     *
     * In the future we might split this in to multiple formats since strace
     * can produce slightly different outputs depending on options used when
     * invoked.
     */
    STRACE,

    /**
     * Log format that is produced by syslog.
     *
     * Note that there might be slight variations to this and some
     * implementations allow users to define their own log format.
     */
    SYSLOG;

    public String toArgument()
    {
        return toString().toLowerCase().replace('_', '-');
    }
}
