package unifiedloganalyzer.parse.strace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import trskop.ICallback;
import trskop.container.Pair;

import unifiedloganalyzer.IParsedData;
import unifiedloganalyzer.IParser;
import unifiedloganalyzer.ParsedData;
import unifiedloganalyzer.parse.ParseError;
import unifiedloganalyzer.parse.strace.StraceProcessStatusChangedParsedData;
import unifiedloganalyzer.parse.strace.StraceSignalParsedData;
import unifiedloganalyzer.parse.strace.StraceSyscallParsedData;
import unifiedloganalyzer.utils.CallbacksManager;
import unifiedloganalyzer.utils.IHasPid;


/**
 * Parser for strace messages.
 *
 * @author Peter Trsko
 */
public class StraceParser implements IParser
{
    // {{{ Private final attributes ///////////////////////////////////////////
    //
    // Regular expressions in string and compiled form.

    /**
     * Parse PID if available.
     *
     * PID prefix is optional, since its present only with certain options
     * passed to strace command. Note that this regex supports two formats for
     * it.
     *
     * This regex doesn't handle interupted calls nor signals.
     */
    private static final String _TOP_LEVEL_REGEX =
        "^((([0-9]+)|\\[pid ([0-9]+)\\]) +)?(.*)$";

    /**
     * Parse syscall as reported by strace.
     */
    private static final String _SYSCALL_REGEX =
        // Syscall name and arguments.
        "^([^\\(]+)\\((.*)\\) += +"
        // Syscall return value.
        + "((-?[0-9]+)|(0x[0-9a-f]+)|\\?)"
        // Symbolic value of errno and description.
        + "( +(E[^ ]+) +\\((.*)\\))?$";

    /**
     * Parse unfinished syscall as reported by strace.
     */
    private static final String _SYSCALL_UNFINISHED_REGEX =
        // Syscall name and arguments.
        "^([^\\(]+)\\((.*),? +<unfinished ...>";

    /**
     * Parse resumed syscall as reported by strace.
     */
    private static final String _SYSCALL_RESUMED_REGEX =
        "^<... +([^ ]+) +resumed> *(.*)\\) += +"
        // Syscall return value.
        + "((-?[0-9]+)|(0x[0-9a-f]+)|\\?)"
        // Symbolic value of errno and description.
        + "( +(E[^ ]+) +\\((.*)\\))?$";

    /**
     * Parse signal message as reported by strace.
     */
    private static final String _SIGNAL_REGEX =
        "^-{3} +([A-Z0-9]+) +\\(([^\\)]+)\\).*$";

    // From https://en.wikipedia.org/wiki/Escape_sequences_in_C we get:
    //
    // \\a               07      Alarm (Beep, Bell)
    // \\b               08      Backspace
    // \\f               0C      Formfeed
    // \\n               0A      Newline (Line Feed)
    // \\r               0D      Carriage Return
    // \\t               09      Horizontal Tab
    // \\v               0B      Vertical Tab
    // \\\               5C      Backslash
    // \\'               27      Single quotation mark
    // \\"               22      Double quotation mark
    // \\?               3F      Question mark
    // \\0               00      Null (string terminator)
    // \\ and a newline          Macro continuation: ignore the \ and the newline
    // \\nnn             0–FF    Octal representation
    // \\xhh             0–FF    Hexadecimal representation
    // \\uhhhh           0–FFFF  Unicode character

    /**
     * Parse string and keep the rest.
     */
    private static final String _STRING_ARGUMENT_REGEX =
        "^\"(([^\"]*|\\\\[0abfnrtv\\\\'\"\\?]|\\\\[1-7][0-7]{2}|"
        + "\\\\x[1-9a-fA-F][0-9a-fA-F]|\\\\u[1-9a-fA-F][0-9a-fA-F]{3})*)\""
        // Another argument may follow or it's last syscall argument or last
        // element of array. Three dots may occurre if strace internal buffer
        // for strings reached its upper bound.
        + "(\\.{3})?(, (.*)|(\\].*))?$";

    private static final String _PROCESS_STATUS_CHANGED_REGEX_CORE =
        "Process +([0-9]+) +(attached|detached|resumed|suspended)$";

    private static final String _PROCESS_STATUS_CHANGED_REGEX =
        "^" + _PROCESS_STATUS_CHANGED_REGEX_CORE;

    private static final String _INTERUPTED_BY_PROCESS_STATUS_REGEX =
        "^(.*)" + _PROCESS_STATUS_CHANGED_REGEX_CORE;

    /**
     * Parse environment variables (key=value pairs).
     */
    private static final String _ENVIRONMENT_VARIABLE_REGEX = "^([^=]+)=(.*)$";

    private static final Pattern _TOP_LEVEL_PATTERN =
        Pattern.compile(_TOP_LEVEL_REGEX);
    private static final Pattern _SYSCALL_PATTERN =
        Pattern.compile(_SYSCALL_REGEX);
    private static final Pattern _SYSCALL_UNFINISHED_PATTERN =
        Pattern.compile(_SYSCALL_UNFINISHED_REGEX);
    private static final Pattern _SYSCALL_RESUMED_PATTERN =
        Pattern.compile(_SYSCALL_RESUMED_REGEX);
    private static final Pattern _SIGNAL_PATTERN =
        Pattern.compile(_SIGNAL_REGEX);
    private static final Pattern _STRING_ARGUMENT_PATTERN =
        Pattern.compile(_STRING_ARGUMENT_REGEX);
    private static final Pattern _ENVIRONMENT_VARIABLE_PATTERN =
        Pattern.compile(_ENVIRONMENT_VARIABLE_REGEX);
    private static final Pattern _PROCESS_STATUS_CHANGED_PATTERN =
        Pattern.compile(_PROCESS_STATUS_CHANGED_REGEX);
    private static final Pattern _INTERUPTED_BY_PROCESS_STATUS_PATTERN =
        Pattern.compile(_INTERUPTED_BY_PROCESS_STATUS_REGEX);

    // }}} Private final attributes ///////////////////////////////////////////

    // {{{ Nested types ///////////////////////////////////////////////////////

    private class Backlog
    {
        private StringBuffer _buff = null;
        private boolean _shouldFinalize = false;

        public Backlog()
        {
            _buff = new StringBuffer();
            _shouldFinalize = false;
        }

        public void append(String str)
        {
            _buff.append(str);
        }

        public boolean isDirty()
        {
            return _buff.length() > 0;
        }

        public String doFinalize()
        {
            String ret = null;

            if (!_shouldFinalize)
            {
                throw new IllegalStateException("Can not finalize backlog"
                    + " that is not marked for finalization.");
            }

            ret = _buff.toString();
            _buff.setLength(0);
            _shouldFinalize = false;

            return ret;
        }

        public boolean shouldFinalize()
        {
            return _shouldFinalize;
        }

        public void markForFinalization()
        {
            if (!isDirty())
            {
                throw new IllegalStateException("Can not mark clean backlog"
                    + " for finalization.");
            }

            _shouldFinalize = true;
        }

        // XXX
        @Override
        public String toString()
        {
            return _buff.toString();
        }
    }

    // }}} Nested types ///////////////////////////////////////////////////////

    private Backlog _backlog = null;
    private CallbacksManager<ParsedData> _callbacksManager = null;

    // {{{ Constructors ///////////////////////////////////////////////////////

    public StraceParser()
    {
        _backlog = new Backlog();
        _callbacksManager = new CallbacksManager<ParsedData>();
    }

    public StraceParser(ICallback<ParsedData> callback)
    {
        this();

        _callbacksManager.registerCallback(callback);
    }

    // }}} Constructors ///////////////////////////////////////////////////////

    // {{{ IParser implementation /////////////////////////////////////////////

    public void parse(String data)
    {
        ParsedData parsedMessage = parseTopLevel(data, _backlog);

        // Backlog may contains message that was generated before currently
        // processed one, therefore we need to process it first.
        if (_backlog.shouldFinalize())
        {
            String backlogMessage = _backlog.doFinalize();

            // Passing null to parseTopLevel indicates that we are already
            // processing backlog.
            ParsedData parsedBacklogMessage =
                parseTopLevel(backlogMessage, null);

            if (parsedBacklogMessage != null)
            {
                _callbacksManager.runCallbacks(parsedBacklogMessage);
            }
        }

        if (parsedMessage != null)
        {
            _callbacksManager.runCallbacks(parsedMessage);
        }
    }

    public void registerCallback(ICallback<ParsedData> callback)
    {
        _callbacksManager.registerCallback(callback);
    }

    // {{{ IParser implementation: Details ////////////////////////////////////

    /**
     * Parse strace message and produce ParsedMessage or <code>null</code> if
     * parsing was postponed due to incomplete message.
     *
     * @param str
     *   Message as produced by strace.
     *
     * @param backlog
     *   Buffer for storing partial messages that were interupted by process
     *   status message(s) or <code>null</code> in case when this method is
     *   currently parsing message from backlog.
     *
     * @return
     *   ParsedData (that may contain parsed message or parse error) or
     *   <code>null</code> if the parsing was postponed since only partial data
     *   are currently available.
     */
    private static ParsedData parseTopLevel(String str, Backlog backlog)
    {
        Matcher topLevelMatcher = _TOP_LEVEL_PATTERN.matcher(str);
        ParsedData.Type dataType = ParsedData.Type.PARSED_MESSAGE;
        String rest = null;
        IParsedData parsedData = null;  // Parse error.

        // _TOP_LEVEL_REGEX is constructed to always succeed and provide group(5), but you newer know.
        if (topLevelMatcher.find() && topLevelMatcher.group(5) != null)
        {
            boolean isProcessStatusChange = false;
            // PID can be provided in two different formats or not at all.
            String pid = topLevelMatcher.group(3);
            if (pid == null)
            {
                pid = topLevelMatcher.group(4);
            }

            rest = topLevelMatcher.group(5);

            parsedData = parseProcessStatusChanged(str, rest);

            // If backlog is null then we are already processing it.
            if (parsedData == null && backlog != null)
            {
                // Parsed process status changed message and interupted message.
                // Original message will be parsed to preserve PID.
                Pair<String, IParsedData> result = parseInteruptedMessage(str);

                if (result != null)
                {
                    if (result.first != null)
                    {
                        // Put to backlog that part of the message that is not
                        // a process status change.
                        backlog.append(result.first);
                    }
                    parsedData = result.second;
                }
            }

            // Either parseProcessStatusChanged or parseInteruptedMessage
            // succeeded.
            if (parsedData != null)
            {
                isProcessStatusChange = true;
            }

            if (parsedData == null)
            {
                parsedData = parseSyscall(str, pid, rest);
            }

            if (parsedData == null)
            {
                parsedData = parseUnfinishedSyscall(str, pid, rest);
            }

            if (parsedData == null)
            {
                parsedData = parseResumedSyscall(str, pid, rest);
            }

            if (parsedData == null)
            {
                parsedData = parseSignal(str, pid, rest);
            }

            if (backlog != null && backlog.isDirty())
            {
                if (parsedData == null)
                {
                    // If message is not recognized till now then it may be a
                    // continuation of interupted message. If so, then backlog
                    // has to be dirty, i.e. there is already a part of the
                    // message.
                    backlog.append(str);

                    // Not a parsing error; just skip result of this function
                    // since its going to backlog whole.
                    return null;
                }
                else if (!isProcessStatusChange)
                {
                    // Message other then process status change occurred is
                    // means that interupted message has already finished.
                    // Marking backlog as finished will force it's processing.
                    backlog.markForFinalization();
                }
            }
        }

        if (parsedData == null)
        {
            // Sometimes we get parsing error for system calls interleaved with
            // process status change message. This is due to the fact that
            // strace sometimes doesn't continue syscall message and prints
            // another (unfinished) syscall message.
            dataType = ParsedData.Type.PARSE_ERROR;
            parsedData = new ParseError(str, "Unknown type of strace message.");
        }

        return new ParsedData(dataType, parsedData);
    }

    private static IParsedData parseSyscall(
        String originalMessage,
        String pid,
        String str)
    {
        Matcher matcher = _SYSCALL_PATTERN.matcher(str);
        IParsedData ret = null;

        if (matcher.find())
        {
            ret = parseSyscallDetails(originalMessage, pid,
                matcher.group(1),   // System call name.
                matcher.group(2),   // System call arguments.
                matcher.group(4),   // Return value if it's integer.
                matcher.group(5),   // Return value if it's pointer.
                matcher.group(7),   // Errno symbolic value.
                matcher.group(8));  // Error message coresponding to errno.
        }

        return ret;
    }

    private static IParsedData parseUnfinishedSyscall(
        String originalMessage,
        String pid,
        String str)
    {
        Matcher matcher = _SYSCALL_UNFINISHED_PATTERN.matcher(str);
        IParsedData ret = null;

        if (matcher.find())
        {
            ret = parseUnfinishedSyscallDetails(originalMessage, pid,
                matcher.group(1),   // System call name.
                matcher.group(2));  // Argument provided to the call.
        }

        return ret;
    }

    private static IParsedData parseResumedSyscall(
        String originalMessage,
        String pid,
        String str)
    {
        Matcher matcher = _SYSCALL_RESUMED_PATTERN.matcher(str);
        IParsedData ret = null;

        if (matcher.find())
        {
            ret = parseResumedSyscallDetails(originalMessage, pid,
                matcher.group(1),   // System call name.
                matcher.group(2),   // Rest of the arguments.
                matcher.group(4),   // Return value if it's integer.
                matcher.group(5),   // Return value if it's pointer.
                matcher.group(7),   // Errno symbolic value.
                matcher.group(8));  // Error message coresponding to errno.
        }

        return ret;
    }

    private static IParsedData parseSignal(
        String originalMessage,
        String pid,
        String str)
    {
        Matcher matcher = _SIGNAL_PATTERN.matcher(str);
        IParsedData ret = null;

        if (matcher.find())
        {
            ret = parseSignalDetails(originalMessage, pid,
                matcher.group(1),   // Signal symbolic value.
                matcher.group(2));  // Signal description.
        }

        return ret;
    }

    private static IParsedData parseProcessStatusChanged(
        String originalMessage,
        String str)
    {
        Matcher matcher = _PROCESS_STATUS_CHANGED_PATTERN.matcher(str);
        IParsedData ret = null;

        if (matcher.find())
        {
            ret = parseProcessStatusChangedDetails(originalMessage,
                // Status changed for process with this PID.
                matcher.group(1),
                // Type of process status change (attached, detached, resumed
                // or suspended).
                matcher.group(2));
        }

        return ret;
    }

    private static Pair<String, IParsedData> parseInteruptedMessage(
        String originalMessage)
    {
        // Parsing originalMessage so that PID will be preserved.
        Matcher matcher =
            _INTERUPTED_BY_PROCESS_STATUS_PATTERN.matcher(originalMessage);
        Pair<String, IParsedData> ret = null;

        if (matcher.find())
        {
            ret = new Pair<String, IParsedData>(
                matcher.group(1),
                parseProcessStatusChangedDetails(originalMessage,
                    // Status changed for process with this PID.
                    matcher.group(2),
                    // Type of process status change (attached, detached,
                    // resumed or suspended).
                    matcher.group(3)));
        }

        return ret;
    }

    /**
     * Parse syscall strace message further.
     *
     * @param originalMessage
     *   Message as produced by strace.
     *
     * @param pid
     *   PID of process that made the syscall or <code>null</code> if not provided.
     *
     * @param name
     *   Name of the syscall performed.
     *
     * @param args
     *   Arguments of syscall.
     *
     * @param intRet
     *   Return value of syscall if it was an integer or <code>null</code>
     *   otherwise.
     *
     * @param ptrRet
     *   Return value of syscall if it was a pointer or <code>null</code>
     *   otherwise.
     *
     * @param errno
     *   Symbolic value of errno.
     *
     * @param errnoDescription
     *   Description of errno as provided by strace.
     *
     * @return
     *   Parsed strace log message or <code>null</code> if parsing failed.
     */
    private static StraceSyscallParsedData parseSyscallDetails(
        String originalMessage,
        String pid,
        String name,
        String args,
        String intRet,
        String ptrRet,
        String errno,
        String errnoDescription)
    {
        StraceSyscallParsedData parsedData =
            new StraceSyscallParsedData(originalMessage, name);

        setPid(parsedData, pid);
        setSyscallResult(parsedData, intRet, ptrRet, errno, errnoDescription);
        parseFullAndUnfinishedSyscallArguments(parsedData, args);

        // TODO

        return parsedData;
    }

    private static StraceSyscallParsedData parseUnfinishedSyscallDetails(
        String originalMessage,
        String pid,
        String name,
        String args)
    {
        StraceSyscallParsedData parsedData = new StraceSyscallParsedData(
            originalMessage, name,
            StraceSyscallParsedData.Flag.UNFINISHED_CALL);

        setPid(parsedData, pid);
        parseFullAndUnfinishedSyscallArguments(parsedData, args);

        // TODO

        return parsedData;
    }

    private static StraceSyscallParsedData parseResumedSyscallDetails(
        String originalMessage,
        String pid,
        String name,
        String args,
        String intRet,
        String ptrRet,
        String errno,
        String errnoDescription)
    {
        StraceSyscallParsedData ret = new StraceSyscallParsedData(
            originalMessage, name, StraceSyscallParsedData.Flag.RESUMED_CALL);

        setPid(ret, pid);
        setSyscallResult(ret, intRet, ptrRet, errno, errnoDescription);

        // TODO

        return ret;
    }

    private static StraceSignalParsedData parseSignalDetails(
        String originalMessage,
        String pid,
        String signal,
        String signalDescription)
    {
        StraceSignalParsedData ret = new StraceSignalParsedData(
            originalMessage, signal, signalDescription);

        setPid(ret, pid);

        return ret;
    }

    private static
        StraceProcessStatusChangedParsedData parseProcessStatusChangedDetails(
            String originalMessage,
            String pid,
            String typeOfProcessStatusChange)
    {
        StraceProcessStatusChangedParsedData ret =
            new StraceProcessStatusChangedParsedData(originalMessage,
                typeOfProcessStatusChange);

        setPid(ret, pid);

        return ret;
    }

    private static void setPid(IHasPid parsedData, String pid)
    {
        if (pid != null)
        {
            // Regex already validated pid to be an integer.
            parsedData.setPid(Integer.parseInt(pid));
        }
    }

    private static void setSyscallResult(
        StraceSyscallParsedData parsedData,
        String intRet,
        String ptrRet,
        String errno,
        String errnoDescription)
    {
        if (intRet != null)
        {
            parsedData.setResultType(StraceSyscallParsedData.ResultType.INTEGER);
            parsedData.setResult(intRet);
        }
        else if (ptrRet != null)
        {
            parsedData.setResultType(StraceSyscallParsedData.ResultType.POINTER);
            parsedData.setResult(ptrRet);
        }
        // Else branch would set same values as StraceSyscallParsedData uses as
        // defaults.

        // Numeric value of errno is system dependent, therefore its better to
        // keep using symbolic value. Default value for both of these is null,
        // so setting it to null again won't be a problem.
        parsedData.setErrno(errno);
        parsedData.setErrnoDescription(errnoDescription);
    }

    private static void parseFullAndUnfinishedSyscallArguments(
        StraceSyscallParsedData parsedData,
        String args)
    {
        // TODO: Clean-up.
        StraceSyscallParsedData.Syscall syscall = parsedData.getSyscall();
        Pair<String, String> fileArgumentAndRest = null;

        switch (syscall)
        {
            // Process only syscalls that have path as their first argument.
            case ACCESS:    // Pass-through
            case CHDIR:     // Pass-through
            case EXEC:      // Pass-through
            case GETCWD:    // Pass-through
            case LSTAT:     // Pass-through
            case OPEN:      // Pass-through
            case READLINK:  // Pass-through
            case STAT:      // Pass-through
            case STATFS:
                fileArgumentAndRest = parseStringArgument(args);
                break;

            // Set child PID when doing fork/vfork/clone.
            case FORK:
                if (parsedData.isResultInteger())
                {
                    // Call might not be finished and then child pid won't be
                    // known.
                    parsedData.setChildPid(new Integer(parsedData.getResult()));
                }
                break;

            default:
                break;
        }

        if (fileArgumentAndRest != null)
        {
            // Setting to null is OK, since it's a default value.
            parsedData.setPath(fileArgumentAndRest.first);

            switch (syscall)
            {
                case EXEC:
                    Pair<List<String>, List<Pair<String, String>>> argsAndEnv =
                        parseExecSpecifics(fileArgumentAndRest.second);

                    if (argsAndEnv.first != null)
                    {
                        parsedData.setCommandLineArgs(argsAndEnv.first.toArray(
                            new String[0]));
                    }

                    if (argsAndEnv.second != null)
                    {
                        for (Pair<String, String> var : argsAndEnv.second)
                        {
                            parsedData.setEnvVar(var.first, var.second);
                        }
                    }

                    break;

                case CHDIR:
                    // Pass-through
                case GETCWD:
                    parsedData.setWorkingDirectory(fileArgumentAndRest.first);
                    break;
            }
        }

        // TODO
    }

    /**
     * Parse first argument as string literal (with C-style escape sequances)
     * and keep the rest as it is.
     *
     * @param argsStr  Unparsed syscall arguments.
     *
     * @return Pair
     *     consisting of parsed argument (<code>first</code>) and unparsed rest
     *     of arguments (<code>second</code>);
     */
    private static Pair<String, String> parseStringArgument(String argsStr)
    {
        Matcher m = _STRING_ARGUMENT_PATTERN.matcher(argsStr);
        Pair<String, String> ret = null;

        if (m.find())
        {
            String rest = m.group(5) != null ? m.group(5) : m.group(6);

            if (rest == null)
            {
                rest = "";  // Last argument.
            }

            ret = new Pair<String, String>(m.group(1), rest);
        }

        return ret;
    }

    private static Pair<List<String>, String> parseListOfStringsArgument(
        String argsStr)
    {
        String rest = null;
        List<String> items = null;

        if (argsStr.charAt(0) != '[')
        {
            return null;    // Parsing failed.
        }

        rest = argsStr.substring(1);    // Remove leading '[' character.
        items = new ArrayList<String>();
        while (rest.charAt(0) != ']')
        {
            Pair<String, String> strAndRest = parseStringArgument(rest);

            if (strAndRest == null
                || strAndRest.first == null
                || strAndRest.second == null
                // We should have reached end of list and that is indicated by
                // presence of ']' character.
                || strAndRest.second.isEmpty())
            {
                return null;    // Parsing failed.
            }

            items.add(strAndRest.first);
            rest = strAndRest.second;
        }

        return new Pair<List<String>, String>(items,
            rest.replaceFirst("](, *)?", ""));
    }

    private static Pair<String, String> parseEnvironmentVariable(String str)
    {
        Matcher m = _ENVIRONMENT_VARIABLE_PATTERN.matcher(str);

        if (m.find())
        {
            return new Pair<String, String>(m.group(1), m.group(2));
        }

        return null;
    }

    private static Pair<List<String>, List<Pair<String, String>>>
        parseExecSpecifics(String str)
    {
        Pair<List<String>, List<Pair<String, String>>> ret = new Pair(null, null);

        Pair<List<String>, String> argsAndRest = parseListOfStringsArgument(str);
        if (argsAndRest == null
            || argsAndRest.first == null
            || argsAndRest.second == null)
        {
            return ret;    // Parse error.
        }
        ret.first = argsAndRest.first;

        Pair<List<String>, String> envAndRest =
            parseListOfStringsArgument(argsAndRest.second);
        if (envAndRest == null
            || envAndRest.first == null
            || envAndRest.second == null)
        {
            return ret;    // Parse error.
        }

        List<Pair<String, String>> env = new ArrayList<Pair<String, String>>();
        for (String item : envAndRest.first)
        {
            Pair<String, String> keyAndValue = parseEnvironmentVariable(item);

            if (keyAndValue == null)
            {
                return ret;    // Parse error;
            }
            env.add(keyAndValue);
        }
        ret.second = env;

        return ret;
    }

    // }}} IParser implementation: Details ////////////////////////////////////

    // }}} IParser implementation /////////////////////////////////////////////
}
