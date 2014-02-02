package unifiedloganalyzer.parse.strace;

import java.io.IOException;
import java.lang.Appendable;
import java.util.HashMap;
import java.util.Map;

import trskop.IAppendTo;

import unifiedloganalyzer.parse.AParsedData;
import unifiedloganalyzer.utils.IHasPath;
import unifiedloganalyzer.utils.IHasPid;


/**
 * Parsed strace syscall message.
 *
 * @author Peter Trsko
 */
public class StraceSyscallParsedData extends AParsedData
    implements IHasPid, IHasPath
{
    // {{{ Nested types ///////////////////////////////////////////////////////

    public static enum Syscall implements IAppendTo
    {
        ACCESS,
        CHDIR,
        EXEC,
        FORK,
        FSTAT,
        GETCWD,
        LSTAT,
        OPEN,
        READLINK,
        STAT,
        STATFS,
        UNKNOWN;    // Leave as last, sort the rest.

        public void appendTo(Appendable buff) throws IOException
        {
            buff.append(this.toString());
        }

        public static Syscall categorize(String name)
        {
            Syscall syscall = Syscall.UNKNOWN;

            switch (name)
            {
                case "access":
                    syscall = Syscall.ACCESS;
                    break;

                case "chdir":
                    syscall = Syscall.CHDIR;
                    break;

                case "execve":
                    syscall = Syscall.EXEC;
                    break;

                case "clone":   // Pass-through
                case "fork":    // Pass-through
                case "vfork":
                    syscall = Syscall.FORK;
                    break;

                case "fstat":   // Pass-through
                case "fstat64":
                    syscall = Syscall.FSTAT;
                    break;

                case "getcwd":
                    syscall = Syscall.GETCWD;
                    break;

                case "lstat":   // Pass-through
                case "lstat64":
                    syscall = Syscall.LSTAT;
                    break;

                case "open":
                    syscall = Syscall.OPEN;
                    break;

                case "readlink":
                    syscall = Syscall.READLINK;
                    break;

                case "stat":    // Pass-through
                case "stat64":
                    syscall = Syscall.STAT;
                    break;

                case "statfs":  // Pass-through
                case "statfs64":
                    syscall = Syscall.STATFS;
                    break;
            }

            return syscall;
        }
    }

    public static enum ResultType implements IAppendTo
    {
        INTEGER,
        POINTER,
        UNKNOWN;

        public void appendTo(Appendable buff) throws IOException
        {
            buff.append(this.toString());
        }
    }

    public static enum Flag implements IAppendTo
    {
        FULL_CALL,
        UNFINISHED_CALL,
        RESUMED_CALL;

        public void appendTo(Appendable buff) throws IOException
        {
            buff.append(this.toString());
        }
    }

    // }}} Nested types ///////////////////////////////////////////////////////

    // {{{ Private attributes /////////////////////////////////////////////////

    private Syscall _syscall = null;
    private Flag _flag = null;
    private String _syscallName = null;
    private int _pid = -1;
    private int _childPid = -1;
    private String _path = null;    // Lot of syscalls take path as an argument.
    private String _result = null;
    private ResultType _resultType = ResultType.UNKNOWN;
    private String _errno = null;
    private String _errnoDescription = null;
    private String _workingDirectory = null;
    private Map<String, String> _environment = null;
    private String[] _commandLineArgs = null;

    // }}} Private attributes /////////////////////////////////////////////////

    // {{{ Constructors ///////////////////////////////////////////////////////

    public StraceSyscallParsedData(
        String originalMessage,
        String syscallName,
        Flag flag)
    {
         super(originalMessage);

         _flag = flag;
         _syscall = Syscall.categorize(syscallName);
         _syscallName = syscallName;
    }

    public StraceSyscallParsedData(String originalMessage, String syscallName)
    {
        this(originalMessage, syscallName, Flag.FULL_CALL);
    }

    // }}} Constructors ///////////////////////////////////////////////////////

    // {{{ IHasPid interface implementation ///////////////////////////////////

    /**
     * {@inheritDoc}
     */
    public int getPid()
    {
        return _pid;
    }

    /**
     * {@inheritDoc}
     */
    public void setPid(int pid)
    {
        _pid = pid;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasPid()
    {
        return _pid >= 0;
    }

    // }}} IHasPid interface implementation ///////////////////////////////////

    // {{{ IHasPath interface implementation //////////////////////////////////

    /**
     * {@inheritDoc}
     */
    public String getPath()
    {
        return _path;
    }

    /**
     * {@inheritDoc}
     */
    public void setPath(String path)
    {
        _path = path;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasPath()
    {
        return _path != null;
    }

    // {{{ IHasPath interface implementation //////////////////////////////////

    // {{{ Getters and setters ////////////////////////////////////////////////

    public Flag getFlag()
    {
        return _flag;
    }


    public Syscall getSyscall()
    {
        return _syscall;
    }


    public String getSyscallName()
    {
        return _syscallName;
    }


    public int getChildPid()
    {
        return _childPid;
    }

    public void setChildPid(int childPid)
    {
        _childPid = childPid;
    }


    public String getWorkingDirectory()
    {
        return _workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory)
    {
        _workingDirectory = workingDirectory;
    }


    public ResultType getResultType()
    {
        return _resultType;
    }

    public void setResultType(ResultType resultType)
    {
        _resultType = resultType;
    }


    public String getResult()
    {
        return _result;
    }

    public void setResult(String result)
    {
        _result = result;
    }


    public String getErrno()
    {
        return _errno;
    }

    public void setErrno(String errno)
    {
        _errno = errno;
    }


    public String getErrnoDescription()
    {
        return _errnoDescription;
    }

    public void setErrnoDescription(String errnoDescription)
    {
        _errnoDescription = errnoDescription;
    }


    public String[] getCommandLineArgs()
    {
        return _commandLineArgs;
    }

    public void setCommandLineArgs(String[] commandLineArgs)
    {
        _commandLineArgs = commandLineArgs;
    }


    public String getEnvVar(String name)
    {
        if (_environment == null)
        {
            return null;
        }

        return _environment.get(name);
    }

    public void setEnvVar(String name, String value)
    {
        if (_environment == null)
        {
            _environment = new HashMap<String, String>();
        }

        _environment.put(name, value);
    }

    // }}} Getters and setters ////////////////////////////////////////////////

    // {{{ Predicates /////////////////////////////////////////////////////////

    public boolean hasChildPid()
    {
        return _childPid >= 0;
    }

    public boolean hasWorkingDirectory()
    {
        return _workingDirectory != null;
    }

    public boolean isResultKnown()
    {
        return _resultType != ResultType.UNKNOWN;
    }

    public boolean isResultInteger()
    {
        return _resultType == ResultType.INTEGER;
    }

    public boolean isResultPointer()
    {
        return _resultType == ResultType.POINTER;
    }

    public boolean hasErrno()
    {
        return _errno != null;
    }

    // }}} Predicates /////////////////////////////////////////////////////////

    // {{{ Implementation of abstract methods /////////////////////////////////

    public void appendRestTo(Appendable buff) throws IOException
    {
        buff.append(", syscall = ");
        _syscall.appendTo(buff);
        buff.append('\n');

        buff.append(", syscallName = ")
            .append(_syscallName)
            .append('\n');

        buff.append(", flag = ");
        _flag.appendTo(buff);
        buff.append('\n');

        buff.append(", pid = ")
            .append(Integer.toString(_pid))
            .append('\n');

        buff.append(", childPid = ")
            .append(Integer.toString(_childPid))
            .append('\n');

        buff.append(", path = ")
            .append(_path)
            .append('\n');

        buff.append(", result = ")
            .append(_result)
            .append('\n');

        buff.append(", resultType = ");
        _resultType.appendTo(buff);
        buff.append('\n');

        buff.append(", errno = ")
            .append(_errno)
            .append('\n');

        buff.append(", errnoDescription = ")
            .append(_errnoDescription)
            .append('\n');

        buff.append(", workingDirectory = ")
            .append(_workingDirectory)
            .append('\n');

        buff.append(", commandLineArguments =");
        if (_commandLineArgs == null || _commandLineArgs.length == 0)
        {
            buff.append(" []");
        }
        else
        {
            buff.append('\n');
            boolean firstArg = true;
            for (int i = 0; i < _commandLineArgs.length; i++)
            {
                buff.append("  ").append(firstArg ? "[ " : ", ")
                    .append(_commandLineArgs[i])
                    .append('\n');

                if (firstArg)
                {
                    firstArg = false;
                }
            }
            buff.append("  ]");
        }
        buff.append('\n');

        // TODO: Apply DRY!
        buff.append(", environment =");
        if (_environment == null)
        {
            buff.append(" []");
        }
        else
        {
            buff.append('\n');
            boolean firstEnvVar = true;
            for (String name : _environment.keySet())
            {
                buff.append("  ").append(firstEnvVar ? "[ " : ", ")
                    .append(name).append(" = ").append(_environment.get(name))
                    .append('\n');

                if (firstEnvVar)
                {
                    firstEnvVar = false;
                }
            }
            buff.append("  ]");
        }
        buff.append('\n');
    }

    // }}} Implementation of abstract methods /////////////////////////////////
}
