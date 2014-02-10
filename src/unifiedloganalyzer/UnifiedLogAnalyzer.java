package unifiedloganalyzer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import unifiedloganalyzer.adapter.AnalyzerCallback;
import unifiedloganalyzer.adapter.SinkCallback;
import unifiedloganalyzer.analyze.AnalysisChain;
import unifiedloganalyzer.analyze.DummyAnalyzer;
import unifiedloganalyzer.analyze.path.MagicPathAnalyzer;
import unifiedloganalyzer.analyze.path.strace.StracePathAnalyzer;
import unifiedloganalyzer.io.FileSource;
import unifiedloganalyzer.io.FileSink;
import unifiedloganalyzer.io.StdoutSink;
import unifiedloganalyzer.parse.DummyParser;
import unifiedloganalyzer.parse.strace.StraceParser;

import unifiedloganalyzer.main.AnalysisAlgorithm;
import unifiedloganalyzer.main.Configuration;
import unifiedloganalyzer.main.InputFormat;
import unifiedloganalyzer.main.ParseOptions;


/**
 * Class with main entry point.
 *
 * @author Kamil Cupr
 */
public class UnifiedLogAnalyzer
{
    // {{{ Core algorithm /////////////////////////////////////////////////////

    /**
     * Core algorithm without the plumbing that was left in main.
     *
     * @param source
     *   Object used as source of messages.
     * @param parser
     *   Object implementing parsing algorithm of source messages.
     * @param analyzer
     *   Object implementing analysis algorithm.
     * @param sink
     *   Object used as sink for the analysis result(s).
     *
     * @throws IOException
     *   If any source, parser, analyzer or writer method encounters I/O
     *   exception.
     */
    public static void doMain(
        ISource source,
        IParser parser,
        IAnalyzer analyzer,
        ISink sink) throws IOException
    {
        parser.registerCallback(new AnalyzerCallback(analyzer));
        analyzer.registerCallback(new SinkCallback(sink));

        while (source.hasNext()) {
            parser.parse(source.next());
        }
        parser.eof();
        sink.close();
    }

    // }}} Core algorithm /////////////////////////////////////////////////////

    // {{{ Factory methods ////////////////////////////////////////////////////

    /**
     * Detect files ending with <code>.tar.gz</code>, <code>.tgz</code> and
     * <code>.gz</code>.
     */
    private static final String _IS_GZIPPED_REGEX = "^.*\\.(t(ar\\.)?)?gz$";

    /**
     * Select appropriate ISource implementation.
     *
     * TODO: StdinSource.
     */
    private static ISource sourceFactory(String fileName)
        throws FileNotFoundException, IOException
    {
        if (fileName == null)
        {
            //return new StdinSource();
            return null;
        }

        return new FileSource(fileName, fileName.matches(_IS_GZIPPED_REGEX));
    }

    /**
     * Select appropriate IParser implementation.
     */
    private static IParser parserFactory(InputFormat inputFormat)
    {
        switch (inputFormat)
        {
            case DUMMY:
                return new DummyParser();
            case STRACE:
                return new StraceParser();
        }

        return null;
    }

    /**
     * Select appropriate IAnalyzer implementation.
     */
    private static IAnalyzer analyzerFactory(InputFormat inputFormat,
        AnalysisAlgorithm analysisAlgorithm)
    {
        StracePathAnalyzer.Configuration straceConfig =
            StracePathAnalyzer.Configuration.theDefault();

        if (!analysisAlgorithm.isSupportedForInputFormat(inputFormat))
        {
            return null;
        }

        switch (analysisAlgorithm)
        {
            case DUMMY:
                return new DummyAnalyzer();

            case STRACE_PATH_ANALYSIS_PARSED_DATA_PRESERVED:
                straceConfig = StracePathAnalyzer.Configuration
                    .preserveParsedData(straceConfig);
            case STRACE_PATH_ANALYSIS:
                return new StracePathAnalyzer(straceConfig);

            case MAGIC_PATH_ANALYSIS_PARSED_DATA_PRESERVED:
                straceConfig = StracePathAnalyzer.Configuration
                    .preserveParsedData(straceConfig);
            case MAGIC_PATH_ANALYSIS:
                MagicPathAnalyzer magicPathAnalyzer =
                    new MagicPathAnalyzer();

                if (inputFormat == InputFormat.STRACE)
                {
                    return new AnalysisChain(
                        new StracePathAnalyzer(straceConfig),
                        magicPathAnalyzer);
                }

                return magicPathAnalyzer;
        }

        return null;
    }

    /**
     * Select appropriate ISink implementation.
     *
     * @throws NoSuchFileException
     */
    private static ISink sinkFactory(String fileName)
        throws FileNotFoundException, IOException
    {
        if (fileName == null)
        {
            return new StdoutSink();
        }

        return new FileSink(fileName, fileName.matches(_IS_GZIPPED_REGEX));
    }

    // }}} Factory methods ////////////////////////////////////////////////////

    // {{{ Main ///////////////////////////////////////////////////////////////

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        Configuration config = ParseOptions.parseOptions(args);

        try
        {
            ISource source = sourceFactory(config.inputFile);
            if (source == null)
            {
                if (config.inputFile == null)
                {
                    System.err.println(
                        "Error: `stdin': Currently not supported.");
                    System.exit(2);
                }
                else
                {
                    throw new NullPointerException("source");
                }
            }

            ISink sink = sinkFactory(config.outputFile);
            if (sink == null)
            {
                throw new NullPointerException("writer");
            }

            IParser parser = parserFactory(config.inputFormat);
            if (parser == null)
            {
                ParseOptions.unsupportedInputFormat(config.inputFormat);
            }

            IAnalyzer analyzer = analyzerFactory(
                config.inputFormat,
                config.analysisAlgorithm);
            if (parser == null)
            {
                ParseOptions.analysisAlgorithmNotAvailableForThisInputFormat(
                    config.analysisAlgorithm,
                    config.inputFormat);
            }

            doMain(source, parser, analyzer, sink);
        }
        catch (FileNotFoundException ex)
        {
            ParseOptions.usageError(config.inputFile, "Input file not found.");
        }
        catch (IOException ex)
        {
            Logger.getLogger(SinkCallback.class.getName())
                .log(Level.SEVERE, null, ex);
        }
    }

    // }}} Main ///////////////////////////////////////////////////////////////
}
