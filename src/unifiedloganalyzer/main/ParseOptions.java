package unifiedloganalyzer.main;

import java.io.PrintStream;
import java.util.Arrays;


/**
 * Utility class for parsing command line options.
 *
 * @author Peter Trsko
 */
public final class ParseOptions
{
    // {{{ Nested types ///////////////////////////////////////////////////////

    private static StringBuffer usage(StringBuffer buff)
    {
        buff.append("Usage:\n\n")

            .append("  UnifiedLogAnalyzer")
               .append(" [--dummy|--strace|--syslog]")
               .append(" [{-o|--output} {FILE|-}] {FILE|-}")
               .append("\n\n")

            .append("  UnifiedLogAnalyzer")
                .append(" [{-a|--algorithm} ALGORITHM]")
                .append(" [{-i|--input-format} INPUT_FORMAT]")
                .append(" [{-o|--output} {FILE|-}] {FILE|-}")
                .append("\n\n")

            .append("  UnifiedLogAnalyzer")
                .append(" {--list-input-formats|--list-algorithms}")
                .append("\n\n")

            .append("  UnifiedLogAnalyzer {-h|--help}");

        return buff;
    }

    public static void printUsage(PrintStream stream)
    {
        stream.println(usage(new StringBuffer()).toString());
    }

    public static void usageError()
    {
        usageError(null);
    }

    public static void usageError(String where, String what)
    {
        usageError("`" + where + "': " + what);
    }

    public static void usageError(String str)
    {
        if (str != null)
        {
            System.err.println("Error: " + str);
        }
        printUsage(System.err);
        System.exit(1);
    }

    public static void unsupportedInputFormat(InputFormat inputFormat)
    {
        System.err.println("Error: `" + inputFormat.toArgument()
            + "': Unsupported input format.");
        System.exit(2);
    }

    public static void analysisAlgorithmNotAvailableForThisInputFormat(
        AnalysisAlgorithm analysisAlgorithm,
        InputFormat inputFormat)
    {
        System.err.println("Error: `"
            + analysisAlgorithm.toString()
            + "': Analysis algorithm is not available or is not compatible"
            + " with specified input format (in this case `"
            + inputFormat.toArgument()
            + "').");
        System.exit(2);
    }

    private static abstract class ProcessOption
    {
        public abstract
            boolean processOption(String[] args, Configuration config);




        private static class Help extends ProcessOption
        {
            @Override
            public boolean processOption(String[] args, Configuration config)
            {
                printUsage(System.out);
                System.exit(0);

                return true;
            }
        }

        private static class Dummy extends ProcessOption
        {
            @Override
            public boolean processOption(String[] args, Configuration config)
            {
                config.inputFormat = InputFormat.DUMMY;
                config.analysisAlgorithm = AnalysisAlgorithm.DUMMY;

                return true;
            }
        }

        private static class Strace extends ProcessOption
        {
            @Override
            public boolean processOption(String[] args, Configuration config)
            {
                config.inputFormat = InputFormat.STRACE;
                config.analysisAlgorithm = AnalysisAlgorithm.STRACE_PATH_ANALYSIS;

                return true;
            }
        }

        private static class Syslog extends ProcessOption
        {
            @Override
            public boolean processOption(String[] args, Configuration config)
            {
                config.inputFormat = InputFormat.SYSLOG;
                config.analysisAlgorithm = AnalysisAlgorithm.DUMMY;

                return true;
            }
        }

        private static class ProcessInputFormat extends ProcessOption
        {
            @Override
            public boolean processOption(String[] args, Configuration config)
            {
                if (args.length == 1)
                {
                    for (InputFormat format : InputFormat.values())
                    {
                        if (format.toString().toLowerCase().equals(args[0]))
                        {
                            config.inputFormat = format;
                            break;
                        }
                    }

                    return true;
                }

                return false;
            }
        }

        private static class ListInputFormats extends ProcessOption
        {
            @Override
            public boolean processOption(String[] args, Configuration config)
            {
                if (args.length != 0)
                {
                    return false;
                }

                StringBuilder builder = new StringBuilder();
                builder.append("Supported input formats:\n");

                for (InputFormat format : InputFormat.values())
                {
                    builder
                        .append("\n")
                        .append("  * ")
                        .append(format.toArgument());
                }

                System.out.println(builder.append('\n').toString());
                System.exit(0);

                return false;
            }
        }

        private static class ProcessAnalysisAlgorithm extends ProcessOption
        {
            @Override
            public boolean processOption(String[] args, Configuration config)
            {
                if (args.length == 1)
                {
                    for (AnalysisAlgorithm algorithm
                        : AnalysisAlgorithm.values())
                    {
                        if (algorithm.toArgument().equals(args[0]))
                        {
                            config.analysisAlgorithm = algorithm;
                            break;
                        }
                    }

                    return true;
                }

                return false;
            }
        }

        private static class ListAlgorithms extends ProcessOption
        {
            @Override
            public boolean processOption(String[] args, Configuration config)
            {
                if (args.length != 0)
                {
                    return false;
                }

                StringBuilder builder = new StringBuilder();
                builder.append("Supported analysis algorithms:\n");

                for (AnalysisAlgorithm algorithm
                    : AnalysisAlgorithm.values())
                {
                    builder
                        .append("\n")
                        .append("  * ")
                        .append(algorithm.toArgument())
                        .append(" (for input formats: ");
                        algorithm.listSupportedInputFormats(builder)
                        .append(')');
                }

                System.out.println(builder.append('\n').toString());
                System.exit(0);

                return false;
            }
        }

        private static class InputFile extends ProcessOption
        {
            @Override
            public boolean processOption(String[] args, Configuration config)
            {
                if (args.length == 1)
                {
                    if (config.inputFile != null)
                    {
                        usageError(args[0], "Too many options.");
                    }
                    config.inputFile = args[0].equals("-") ? null : args[0];

                    return true;
                }

                return false;
            }
        }

        private static class OutputFile extends ProcessOption
        {
            @Override
            public boolean processOption(String[] args, Configuration config)
            {
                if (args.length == 1)
                {
                    config.outputFile = args[0].equals("-") ? null : args[0];

                    return true;
                }

                return false;
            }
        }

        public static final ProcessOption help = new Help();
        public static final ProcessOption dummy = new Dummy();
        public static final ProcessOption strace = new Strace();
        public static final ProcessOption syslog = new Syslog();
        public static final ProcessOption inputFormat =
            new ProcessInputFormat();
        public static final ProcessOption listInputFormats =
            new ListInputFormats();
        public static final ProcessOption analysisAlgorithm =
            new ProcessAnalysisAlgorithm();
        public static final ProcessOption listAlgorithms =
            new ListAlgorithms();
        public static final ProcessOption inputFile = new InputFile();
        public static final ProcessOption outputFile = new OutputFile();
    }

    private static enum Option
    {
        /**
         * Print usage info and exit.
         */
        HELP("h", "help", 0, ProcessOption.help),

        /**
         * Select InputFormat.DUMMY and AnalysisAlgorithm.DUMMY.
         */
        DUMMY(null, "dummy", 0, ProcessOption.dummy),

        /**
         * Select InputFormat.STRACE and
         * AnalysisAlgorithm.STRACE_PATH_ANALYSIS.
         */
        STRACE(null, "strace", 0, ProcessOption.strace),

        /**
         * Currently not supported.
         */
        SYSLOG(null, "syslog", 0, ProcessOption.syslog),

        /**
         * Select specific input format.
         */
        INPUT_FORMAT("i", "input-format", 1, ProcessOption.inputFormat),

        /**
         * List supported input formats.
         */
        LIST_INPUT_FORMATS(null, "list-input-formats", 0,
            ProcessOption.listInputFormats),

        /**
         * Select specific algorithm for analysis.
         */
        ANALYSIS_ALGORITHM("a", "algorithm", 1,
            ProcessOption.analysisAlgorithm),

        /**
         * List available algorithms.
         */
        LIST_ALGORITHMS(null, "list-algorithms", 0,
            ProcessOption.listAlgorithms),

        /**
         * Specify otput file.
         */
        OUTPUT_FILE("o", "output", 1, ProcessOption.outputFile),

        /**
         * Specify input file. Currently only one is supported.
         */
        INPUT_FILE(null, null, 0, ProcessOption.inputFile); // Keep non option last

        private String _shortOption = null;
        private String _longOption = null;
        private int _numberOfArguments = -1;
        private ProcessOption _processor = null;

        private Option(
            String shortOption,
            String longOption,
            int numberOfArguments,
            ProcessOption processor)
        {
            _shortOption = shortOption == null ? null : "-" + shortOption;
            _longOption = longOption == null ? null : "--" + longOption;
            _numberOfArguments = numberOfArguments;
            _processor = processor;
        }

        public boolean isNonOption()
        {
            return _shortOption == null && _longOption == null;
        }

        public int getNumberOfArguments()
        {
            return _numberOfArguments;
        }

        public boolean matchOption(String arg)
        {
            return (_shortOption != null && _shortOption.equals(arg))
                || (_longOption != null && _longOption.equals(arg))
                // Non-option matches anything.
                || (_shortOption == null && _longOption == null);
        }

        public static Option match(String arg)
        {
            for (Option opt : values())
            {
                if (opt.matchOption(arg))
                {
                    return opt;
                }
            }

            return null;
        }

        public boolean processOption(String[] args, Configuration config)
        {
            // Non-options will always get one argument, and that is the
            // non-option its self.
            int numberOfArguments = isNonOption() ? 1 : getNumberOfArguments();

            if (args.length != numberOfArguments)
            {
                return false;
            }

            return _processor.processOption(args, config);
        }
    }

    // {{{ Nested types ///////////////////////////////////////////////////////

    // {{{ Public interface ///////////////////////////////////////////////////

    public static Configuration parseOptions(String[] args)
    {
        Configuration config = Configuration.theDefault();

        for (int i = 0; i < args.length; i++)
        {
            i += parseOption(config, args[i], i, args).getNumberOfArguments();
        }

        return config;
    }

    // {{{ Public interface ///////////////////////////////////////////////////

    // {{{ Implementation details /////////////////////////////////////////////

    private static Option parseOption(
        Configuration config,
        String arg,
        int argIndex,
        String[] args)
    {
        String[] optArgs = new String[0];
        Option opt = Option.match(arg);
        if (opt == null)
        {
            throw new NullPointerException("opt");
        }

        int numberOfArguments = opt.getNumberOfArguments();
        if (opt.isNonOption())
        {
            optArgs = Arrays.copyOfRange(args, argIndex, argIndex + 1);
        }
        else if (numberOfArguments > 0)
        {
            // Skip currently processed option.
            int startIndex = argIndex + 1;

            // End index is exclusive;
            int endIndex = startIndex + numberOfArguments;

            if (endIndex > args.length)
            {
                usageError(arg, "Missing argument(s).");
            }

            optArgs = Arrays.copyOfRange(args, startIndex, endIndex);
        }

        if (!opt.processOption(optArgs, config))
        {
            usageError(arg, "While processing option [and its argument(s)].");
        }

        return opt;
    }

    // }}} Implementation details /////////////////////////////////////////////
}
