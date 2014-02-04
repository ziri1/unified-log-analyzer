package unifiedloganalyzer.analyze.path.strace;

import java.io.IOException;

import unifiedloganalyzer.IOutputMessage;


/**
 *
 * @author Peter Trsko
 */
class Statistics implements IOutputMessage
{ 
    public static enum Event
    {
        SYSCALL,
        IGNORED_SYSCALL,
        IGNORED_RESUMED_SYSCALL,
        SIGNAL,
        PROCESS_STATUS_CHANGE,
        FORK_SYSCALL,
        EXEC_SYSCALL,
        OPEN_SYSCALL,
        GETCWD_SYSCALL,
        CHDIR_SYSCALL,
        UNKNOWN_MESSAGE,

        // Misses

        GET_PROCESS_MISS,
        REMOVE_PROCESS_MISS,
        WORKING_DIRECTORY_MISS,
        NO_PWD_ENV_VAR,

        PARSE_ERROR,
        UNREPORTED_PARSING_FAILURE;
    }

    private int _messageCount = 0;
    private int _syscallCount = 0;
    private int _ignoredSyscallCount = 0;
    private int _ignoredResumedSyscallCount = 0;
    private int _signalCount = 0;
    private int _statusChangeCount = 0;
    private int _forkCount = 0;
    private int _execCount = 0;
    private int _openCount = 0;
    private int _getcwdCount = 0;
    private int _chdirCount = 0;
    private int _unknownMessageCount = 0;

    private int _getProcessMissesCount = 0;
    private int _removeProcessMissesCount = 0;
    private int _wdMissesCount = 0;
    private int _pwdEnvVarMissesCount = 0;

    private int _parseErrorCount = 0;
    private int _unreportedParsingFailureCount = 0;

    public void update(Event event)
    {
        switch (event)
        {
            case SYSCALL:
                _syscallCount++;
                _messageCount++;
                break;
            case SIGNAL:
                _signalCount++;
                _messageCount++;
                break;
            case PROCESS_STATUS_CHANGE:
                _statusChangeCount++;
                _messageCount++;
                break;
            case IGNORED_SYSCALL:
                _ignoredSyscallCount++;
                break;
            case IGNORED_RESUMED_SYSCALL:
                _ignoredResumedSyscallCount++;
                break;
            case FORK_SYSCALL:
                _forkCount++;
                break;
            case EXEC_SYSCALL:
                _execCount++;
                break;
            case OPEN_SYSCALL:
                _openCount++;
                break;
            case GETCWD_SYSCALL:
                _getcwdCount++;
                break;
            case CHDIR_SYSCALL:
                _chdirCount++;
                break;
            case UNKNOWN_MESSAGE:
                _unknownMessageCount++;
                break;
            case GET_PROCESS_MISS:
                _getProcessMissesCount++;
                break;
            case REMOVE_PROCESS_MISS:
                this._removeProcessMissesCount++;
                break;
            case WORKING_DIRECTORY_MISS:
                _wdMissesCount++;
                break;
            case NO_PWD_ENV_VAR:
                _pwdEnvVarMissesCount++;
                break;
            case PARSE_ERROR:
                _parseErrorCount++;
                break;
            case UNREPORTED_PARSING_FAILURE:
                _unreportedParsingFailureCount++;
                break;
        }
    }

    public int getSyscallCount()
    {
        return _syscallCount;
    }

    public void appendTo(Appendable buff) throws IOException
    {
        buff.append(this.getClass().getName()).append(':')
            .append("\n{ processedMessages =")
                .append("\n  { all = ")
                    .append(Integer.toString(_messageCount))
                .append("\n  , syscalls = ")
                    .append("\n    { all = ")
                        .append(Integer.toString(_syscallCount))
                    .append("\n    , ignored = ")
                        .append(Integer.toString(_ignoredSyscallCount))
                    .append("\n    , ignoredResumed = ")
                        .append(Integer.toString(_ignoredResumedSyscallCount))
                    .append("\n    , fork = ")
                        .append(Integer.toString(_forkCount))
                    .append("\n    , exec = ")
                        .append(Integer.toString(_execCount))
                    .append("\n    , open = ")
                        .append(Integer.toString(_openCount))
                    .append("\n    , getcwd = ")
                        .append(Integer.toString(_getcwdCount))
                    .append("\n    , chdir = ")
                        .append(Integer.toString(_chdirCount))
                    .append("\n    }")
                .append("\n  , signals = ")
                    .append(Integer.toString(_signalCount))
                .append("\n  , processStatusChanges = ")
                    .append(Integer.toString(_statusChangeCount))
                .append("\n  , unknown = ")
                    .append(Integer.toString(_unknownMessageCount))
                .append("\n  }")
            .append("\n, getProcessMisses = ")
                .append(Integer.toString(_getProcessMissesCount))
            .append("\n, removeProcessMisses = ")
                .append(Integer.toString(_removeProcessMissesCount))
            .append("\n, workingDirectoryMisses = ")
                .append(Integer.toString(_wdMissesCount))
            .append("\n, pwdEnvVarMisses = ")
                .append(Integer.toString(_pwdEnvVarMissesCount))
            .append("\n, parseErrors = ")
                .append(Integer.toString(_parseErrorCount))
            .append("\n, unreportedParsingFailures = ")
                .append(Integer.toString(_unreportedParsingFailureCount))
            .append("\n}");
    }

    public boolean messageEquals(IOutputMessage message)
    {
        // Statistics are never equal!
        return false;
    }
}
