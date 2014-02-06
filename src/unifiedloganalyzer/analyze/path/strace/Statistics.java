package unifiedloganalyzer.analyze.path.strace;

import java.io.IOException;

import unifiedloganalyzer.IOutputMessage;


/**
 * Strace path analysis statistics.
 *
 * This provides a lot of information about how well the analysis went and how
 * reliable the results are.
 *
 * @author Peter Trsko
 */
class Statistics implements IOutputMessage
{
    public static enum Event
    {
        // {{{ Statistics of messages received by the analyzer. ///////////////

        /**
         * Analyzer received message with parsed syscall.
         */
        SYSCALL,

        /**
         * Analyzer received message with parsed signal.
         */
        SIGNAL,

        /**
         * Analyzer received message with parsed process status change.
         */
        PROCESS_STATUS_CHANGE,

        /**
         * Analyzer received parse error message.
         */
        PARSE_ERROR,

        /**
         * Analyzer received an unknown message type.
         */
        UNKNOWN_MESSAGE,

        // }}} Statistics of messages received by the analyzer. ///////////////

        // {{{ More detailed syscall statistics ///////////////////////////////

        /**
         * Syscall is irrelevant for process model and analysis purpose.
         */
        IGNORED_SYSCALL,

        /**
         * Resumed syscall ignored, i.e. all necessary data already present in
         * unfinished syscall.
         */
        IGNORED_RESUMED_SYSCALL,

        /**
         * Received fork() or clone() call.
         */
        FORK_SYSCALL,

        /**
         * Received exec() call.
         */
        EXEC_SYSCALL,

        /**
         * Received exit() or exit_group() call.
         */
        EXIT_SYSCALL,

        /**
         * Received open() or creat() call.
         */
        OPEN_OR_CREAT_SYSCALL,

        /**
         * Received getcwd() call.
         */
        GETCWD_SYSCALL,

        /**
         * Received chdir() call.
         */
        CHDIR_SYSCALL,

        // }}} More detailed syscall statistics ///////////////////////////////

        // {{{ Misses and model errors ////////////////////////////////////////

        /**
         * Process not found in model.
         */
        GET_PROCESS_MISS,

        /**
         * Process not found in model while trying to remove it.
         */
        TERMINATE_PROCESS_MISS,

        /**
         * Model doesn't know working directory of a process.
         */
        WORKING_DIRECTORY_MISS,

        // }}} Misses and model errors ////////////////////////////////////////

        // {{{ Parsing failures ///////////////////////////////////////////////

        /**
         * Unable to find PWD environment variable in parsed data.
         */
        NO_PWD_ENV_VAR,

        /**
         * Parser failed in some way, but hadn't produced PARSE_ERROR message.
         */
        UNREPORTED_PARSING_FAILURE;

        // }}} Parsing failures ///////////////////////////////////////////////
    }

    private int _messageCount = 0;
    private int _syscallCount = 0;
    private int _signalCount = 0;
    private int _statusChangeCount = 0;
    private int _parseErrorCount = 0;
    private int _unknownMessageCount = 0;

    private int _ignoredSyscallCount = 0;
    private int _ignoredResumedSyscallCount = 0;
    private int _forkCount = 0;
    private int _execCount = 0;
    private int _exitCount = 0;
    private int _openAndCreatCount = 0;
    private int _getcwdCount = 0;
    private int _chdirCount = 0;

    private int _getProcessMissesCount = 0;
    private int _terminateProcessMissesCount = 0;
    private int _wdMissesCount = 0;

    private int _pwdEnvVarMissesCount = 0;
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
            case EXIT_SYSCALL:
                _exitCount++;
                break;
            case OPEN_OR_CREAT_SYSCALL:
                _openAndCreatCount++;
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
            case TERMINATE_PROCESS_MISS:
                _terminateProcessMissesCount++;
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

    /**
     * {@inheritDoc}
     */
    @Override
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
                    .append("\n    , exit = ")
                        .append(Integer.toString(_exitCount))
                    .append("\n    , openAndCreat = ")
                        .append(Integer.toString(_openAndCreatCount))
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
                .append(Integer.toString(_terminateProcessMissesCount))
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean messageEquals(IOutputMessage message)
    {
        // Statistics are never equal!
        return false;
    }
}
