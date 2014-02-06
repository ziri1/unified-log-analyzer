package unifiedloganalyzer.analyze.path.strace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;
//import java.util.Stack;

import trskop.IAppendTo;
import trskop.utils.AppendableLike;

import unifiedloganalyzer.IParsedData;
import unifiedloganalyzer.analyze.AAnalyzer;
import unifiedloganalyzer.analyze.path.PathCompoundMessage;
import unifiedloganalyzer.analyze.path.PathOutputMessage;
import unifiedloganalyzer.parse.strace.StraceProcessStatusChangedParsedData;
import unifiedloganalyzer.parse.strace.StraceSignalParsedData;
import unifiedloganalyzer.parse.strace.StraceSyscallParsedData;
import unifiedloganalyzer.utils.IHasPid;


/**
 * Analyse strace output to produce list of opened files.
 *
 * This class models process hierarchy as it existed during execution to
 * produce absolute paths instead of just stupidly printing path arguments to
 * open calls.
 *
 * @author Peter Trsko
 */
public class StracePathAnalyzer extends AAnalyzer
{
    // {{{ Process model //////////////////////////////////////////////////////

    private static class Process implements IHasPid, IAppendTo
    {
        private static class OpenedFile
        {
            public String path = null;
            public int openCount = 0;
            public int closeCount = 0;
            public int failedOpenCount = 0;
            // TODO: FDs

            public OpenedFile(String path)
            {
                this.path = path;
            }

            public boolean isClosed()
            {
                return this.openCount == this.closeCount;
            }
        }

        /**
         * Any negative value can be used for its value.
         */
        public static final int NO_PID = -2;

        /**
         * Exit code is non-negative integer, so negative value indicate that
         * it's not present.
         */
        public static final int NO_EXIT_CODE = -1;

        private int _pid = NO_PID;
        private int _parentPid = NO_PID;
        private String _executable = null;
        private String _workingDirectory = null;
        private int _exitCode = NO_EXIT_CODE;
        private Map<String, OpenedFile> _openedFiles = null;

        // TODO: Queue of calls waiting to be resumend.

        // {{{ Constructors ///////////////////////////////////////////////////

        /**
         * Generic form of Process construction.
         *
         * @param pid
         *   PID (system process ID) of this process.
         *
         * @param parentPid
         *   Parent PID (system process ID) of this process.
         *
         * @param workingDirectory
         *   Working directory of this process or <code>null</code> if unknown.
         */
        public Process(int pid, int parentPid, String workingDirectory)
        {
            _pid = pid;
            _parentPid = parentPid;
            _workingDirectory = workingDirectory;
            _openedFiles = new HashMap<>();
        }

        /**
         * Construct process with unknown parent PID.
         *
         * @param pid
         *   PID (system process ID) of this process.
         *
         * @param workingDirectory
         *   Parent PID (system process ID) of this process.
         */
        public Process(int pid, String workingDirectory)
        {
            this(pid, NO_PID, workingDirectory);
        }

        /**
         * Construct process with unknown parent PID and its working directory.
         *
         * @param pid
         *   PID (system process ID) of this process.
         */
        public Process(int pid)
        {
            this(pid, NO_PID, null);
        }

        // }}} Constructors ///////////////////////////////////////////////////

        // {{{ IHasPid interface implementation ///////////////////////////////

        /**
         * {@inheritDoc}
         */
        @Override
        public int getPid()
        {
            return _pid;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setPid(int pid)
        {
            throw new UnsupportedOperationException(
                "PID of existing process can not be changed.");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasPid()
        {
            return _pid >= 0;
        }

        // }}} IHasPid interface implementation ///////////////////////////////

        // {{{ Getters and setters ////////////////////////////////////////////

        public int getParentPid()
        {
            return _parentPid;
        }


        public String getExecutable()
        {
            return _executable;
        }

        public void setExecutable(String executable)
        {
            _executable = executable;
        }


        public String getWorkingDirectory()
        {
            return _workingDirectory;
        }

        public void setWorkingDirectory(String workingDirectory)
        {
            _workingDirectory = workingDirectory;
        }

        public int getExitCode()
        {
            return _exitCode;
        }

        public void setExitCode(int exitCode)
        {
            _exitCode = exitCode;
        }

        // }}} Getters and setters ////////////////////////////////////////////

        // {{{ Predicates /////////////////////////////////////////////////////

        public boolean hasParentPid()
        {
            return _pid >= 0;
        }

        public boolean hasWorkingDirectory()
        {
            return _workingDirectory != null;
        }

        // }}} Predicates /////////////////////////////////////////////////////

        // {{{ Open files operations //////////////////////////////////////////

        public void openedFile(String path, int result)
        {
            OpenedFile file;

            if (_openedFiles.containsKey(path))
            {
                file = _openedFiles.put(path, new OpenedFile(path));
            }
            else
            {
                file = _openedFiles.get(path);
            }

            if (result > 0)
            {
                // Initial value of openCount is zero, so this is safe even for
                // the time when we create new entry.
                file.openCount++;
            }
            else
            {
                file.failedOpenCount++;
            }
        }

        public void closedFile(String path)
        {
            _openedFiles.get(path).closeCount++;
        }

        public Collection<OpenedFile> getOpenFiles()
        {
            return _openedFiles.values();
        }

        // }}} Open files operations //////////////////////////////////////////

        // {{{ IAppendTo //////////////////////////////////////////////////////

        /**
         * {@inheritDoc}
         */
        @Override
        public void appendTo(Appendable buff) throws IOException
        {
            (new AppendableLike(buff))
                .append("{ pid = ")
                .append(_pid)
                .append('\n')

                .append(", parentPid = ")
                .append(_parentPid)
                .append('\n')

                .append(", workingDirectory = ")
                .append(_workingDirectory)
                .append('\n')

                .append("}\n");
        }

        // }}} IAppendTo //////////////////////////////////////////////////////
    }

    private static class ProcessModel
    {
        /**
         * Reference to analysis statistics.
         *
         * It might be good idea to split analysis statistics in to more
         * classes where ProcessModelStatistics would be one of them.
         */
        private Statistics _statistics = null;

        /**
         * Map of running processes with PID as key (process ID) and its state
         * as value.
         */
        private Map<Integer, Process> _processes = null;

        /**
         * List of already terminated processes.
         *
         * This is kept for later analysis, but we can't use Map, because
         * PIDs can be reused.
         */
        private List<Process> _terminatedProcesses = null;

        public ProcessModel(Statistics statistics)
        {
            _statistics = statistics;
            _processes = new TreeMap<>();
            _terminatedProcesses = new ArrayList<>();
        }

        /**
         * Update statistics with specified event.
         *
         * @param event
         *   Event that occurred and statistics are kept for.
         */
        private void updateStatistics(Statistics.Event event)
        {
            _statistics.update(event);
        }

        private boolean processExists(int pid)
        {
            return _processes.containsKey(pid);
        }

        private Process createProcess(int pid)
        {
            return createProcess(Process.NO_PID, pid);
        }

        private Process createProcess(int parentPid, int pid)
        {
            if (processExists(pid))
            {
                throw new IllegalArgumentException("pid = " + pid);
            }

            Process parent = getProcess(parentPid);

            if (parent == null)
            {
                parent = new Process(pid == -1 ? Process.NO_PID : pid, null);
                _processes.put(new Integer(pid), parent);
            }

            Process child =
                new Process(pid, parentPid, parent.getWorkingDirectory());
            child.setWorkingDirectory(parent.getWorkingDirectory());

            _processes.put(new Integer(pid), child);

            return child;
        }

        /**
         * Remove process from the collection of running processes.
         *
         * @param pid
         *   ID (PID) of process that terminated.
         * @param exitCode
         *   Exit code of the process that terminated.
         */
        private void terminateProcess(int pid, int exitCode)
        {
            Process process = _processes.remove(pid);

            if (process == null)
            {
                updateStatistics(Statistics.Event.TERMINATE_PROCESS_MISS);
            }
            else
            {
                _terminatedProcesses.add(process);
                process.setExitCode(exitCode);
            }
        }

        /**
         * Gets running process from process model with specified PID.
         *
         * @param pid
         *   PID (system process ID) of running process.
         * @return
         *   Running process with specified PID, or <code>null</code> if there
         *   is no such running process.
         */
        private Process getProcess(int pid)
        {
            Process process = _processes.get(pid);

            if (process == null)
            {
                updateStatistics(Statistics.Event.GET_PROCESS_MISS);
            }

            return process;
        }

        /**
         * Gets process if it's already present in the model or adds it and
         * then returns it in case when it's not already present in this
         * process model.
         *
         * This method reports GET_PROCESS_MISS to statistics when process is
         * not found in this model. Therefore this method should be used only
         * in cases when nonexistence of such process in model would be
         * a failure.
         *
         * @param pid
         *   PID of the process to get or create if it is not present in this
         *   model.
         * @return
         *   Process with PID pid that is present (at this point) in the model.
         */
        private Process getOrCreateProcess(int pid)
        {
            Process process = getProcess(pid);

            if (process == null)
            {
                process = createProcess(pid);
            }

            return process;
        }
    }

    // {{{ Process model //////////////////////////////////////////////////////

    // {{{ Configuration //////////////////////////////////////////////////////

    public static class Configuration
    {
        private boolean _preserveParsedData = false;

        public static Configuration theDefault()
        {
            return new Configuration();
        }

        public static Configuration preserveParsedData(Configuration config)
        {
            config._preserveParsedData = true;

            return config;
        }

        public static Configuration preserveParsedData()
        {
            return preserveParsedData(theDefault());
        }

        public boolean shouldPreserveParsedData()
        {
            return _preserveParsedData;
        }
    }

    // }}} Configuration //////////////////////////////////////////////////////

    // {{{ Private attributes /////////////////////////////////////////////////

    private ProcessModel _model = null;

    private Statistics _statistics = null;

    private Configuration _config = null;

    // }}} Private attributes /////////////////////////////////////////////////

    // {{{ Constructors ///////////////////////////////////////////////////////

    public StracePathAnalyzer(Configuration config)
    {
        _statistics = new Statistics();
        _model = new ProcessModel(_statistics);
        _config = config == null ? Configuration.theDefault() : config;
    }

    /**
     * Construct StracePathAnalyzer with default configuration.
     */
    public StracePathAnalyzer()
    {
        this(null);
    }

    // }}} Constructors ///////////////////////////////////////////////////////

    /**
     * Update statistics with specified event.
     *
     * @param event
     *   Event that occurred and statistics are kept for.
     */
    private void updateStatistics(Statistics.Event event)
    {
        _statistics.update(event);
    }

    /**
     * Ask Statistics if we are processing first syscall.
     */
    private boolean isFirstSyscall()
    {
        return _statistics.getSyscallCount() == 1;
    }

    /**
     * Take working directory and file path and produce absolute file path if
     * it wasn't already absolute.
     *
     * @param workingDirectory
     *   Working directory of the process that did something with specified
     *   file.
     * @param file
     *   File path.
     *
     * @return
     *   Absolute path to file if it was possible to determine it, or original
     *   relative path if workingDirectory was null.
     */
    private String resolvePath(String wd, String file)
    {
        if (wd == null)
        {
            updateStatistics(Statistics.Event.WORKING_DIRECTORY_MISS);
        }

        if (file.charAt(0) != '/' && wd != null)
        {
            return wd + "/" + file;
        }

        return file;
    }

    // {{{ Syscall processing /////////////////////////////////////////////////

    private void processGetcwdAndChdirSyscalls(
        StraceSyscallParsedData.Flag flag,
        String path,
        int pid)
    {
        if (flag == StraceSyscallParsedData.Flag.RESUMED_CALL)
        {
            updateStatistics(Statistics.Event.IGNORED_RESUMED_SYSCALL);

            return;
        }

        if (path == null)
        {
            updateStatistics(Statistics.Event.UNREPORTED_PARSING_FAILURE);
        }

        _model.getOrCreateProcess(pid).setWorkingDirectory(path);
    }

    private void processExecSyscall(
        StraceSyscallParsedData.Flag flag,
        int pid,
        StraceSyscallParsedData parsedData)
    {
        if (flag == StraceSyscallParsedData.Flag.RESUMED_CALL)
        {
            updateStatistics(Statistics.Event.IGNORED_RESUMED_SYSCALL);

            return;
        }

        Process process = _model.getOrCreateProcess(pid);

        if (!process.hasWorkingDirectory())
        {
            // Guess working directory from PWD environment variable.
            String workingDirectory = parsedData.getEnvVar("PWD");

            if (workingDirectory == null)
            {
                updateStatistics(Statistics.Event.NO_PWD_ENV_VAR);
            }

            process.setWorkingDirectory(workingDirectory);
        }

        process.setExecutable(parsedData.getPath());
    }

    private String processOpenSyscall(
        StraceSyscallParsedData.Flag flag,
        String file,
        int pid)
    {
        if (flag == StraceSyscallParsedData.Flag.RESUMED_CALL)
        {
            updateStatistics(Statistics.Event.IGNORED_RESUMED_SYSCALL);

            return null;
        }

        return resolvePath(_model.getOrCreateProcess(pid).getWorkingDirectory(),
            file);
    }

    private void analyzeSyscall(StraceSyscallParsedData parsedData)
    {
        StraceSyscallParsedData.Syscall syscall = parsedData.getSyscall();
        int pid = parsedData.getPid();
        int childPid = parsedData.getChildPid();
        String file = parsedData.getPath();
        StraceSyscallParsedData.Flag flag = parsedData.getFlag();

        Process process = null;

        // With first call comes first process and it doesn't matter what
        // syscall it is, because model is empty right now.
        //
        // We also reduce unnecessary getProcess misses.
        if (isFirstSyscall())
        {
            _model.createProcess(pid);
        }

        switch (syscall)
        {
            case FORK:
                updateStatistics(Statistics.Event.FORK_SYSCALL);
                _model.createProcess(pid, childPid);
                break;

            case EXEC:
                updateStatistics(Statistics.Event.EXEC_SYSCALL);
                processExecSyscall(flag, pid, parsedData);
                break;

            case EXIT:
                updateStatistics(Statistics.Event.EXIT_SYSCALL);
                if (parsedData.hasExitCode())
                {
                    _model.terminateProcess(pid, parsedData.getExitCode());
                }
                // TODO: Else branch: Statistics that we weren't able to remove
                // process.
                break;

            case GETCWD:
                updateStatistics(Statistics.Event.GETCWD_SYSCALL);
                processGetcwdAndChdirSyscalls(flag, file, pid);
                break;

            case CHDIR:
                updateStatistics(Statistics.Event.CHDIR_SYSCALL);
                processGetcwdAndChdirSyscalls(flag, file, pid);
                break;

            case OPEN:
                updateStatistics(Statistics.Event.OPEN_SYSCALL);
                // Resolve absolute path if possible, returns null if we should
                // ignore the open() call.
                file = processOpenSyscall(flag, file, pid);
                if (file != null)
                {
                    runCallbacks(
                        _config.shouldPreserveParsedData()
                        ? new PathCompoundMessage(file, parsedData)
                        : new PathOutputMessage(file));
                }
                break;

            default:
                updateStatistics(Statistics.Event.IGNORED_SYSCALL);
        }
    }

    // }}} Syscall processing /////////////////////////////////////////////////

    // {{{ AAnalyzer, implementation of abstract methods //////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    protected void processEmptyMessage(IParsedData parsedData)
    {
        runCallbacks(_statistics);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void processParsedMessage(IParsedData parsedData)
    {
        if (parsedData instanceof StraceSyscallParsedData)
        {
            updateStatistics(Statistics.Event.SYSCALL);
            analyzeSyscall((StraceSyscallParsedData)parsedData);
        }
        else if (parsedData instanceof StraceSignalParsedData)
        {
            updateStatistics(Statistics.Event.SIGNAL);
        }
        else if (parsedData instanceof StraceProcessStatusChangedParsedData)
        {
            updateStatistics(Statistics.Event.PROCESS_STATUS_CHANGE);
        }
        else
        {
            updateStatistics(Statistics.Event.UNKNOWN_MESSAGE);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void processParseError(IParsedData parsedData)
    {
        updateStatistics(Statistics.Event.PARSE_ERROR);
    }

    // }}} AAnalyzer, implementation of abstract methods //////////////////////
}
