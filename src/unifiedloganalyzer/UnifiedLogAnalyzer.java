package unifiedloganalyzer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import unifiedloganalyzer.adapter.AnalyzerCallback;
import unifiedloganalyzer.adapter.WriterCallback;
import unifiedloganalyzer.analyze.DummyAnalyzer;
import unifiedloganalyzer.analyze.path.strace.StracePathAnalyzer;
import unifiedloganalyzer.io.FileSource;
import unifiedloganalyzer.io.FileWriter;
import unifiedloganalyzer.io.StdoutWriter;
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

    public static void doMain(
        ISource source,
        IParser parser,
        IAnalyzer analyzer,
        IWriter writer) throws IOException
    {
        parser.registerCallback(new AnalyzerCallback(analyzer));
        analyzer.registerCallback(new WriterCallback(writer));

        while (source.hasNext()) {
            parser.parse(source.next());
        }
        parser.eof();
        writer.close();
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
    private static ISource sourceFactory(String fileName) throws IOException
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
        if (analysisAlgorithm.isSupportedForInputFormat(inputFormat))
        {
            switch (analysisAlgorithm)
            {
                case DUMMY:
                    return new DummyAnalyzer();

                case STRACE_PATH_ANALYSIS:
                    return new StracePathAnalyzer();

                case STRACE_PATH_ANALYSIS_PARSED_DATA_PRESERVED:
                    return new StracePathAnalyzer(
                        StracePathAnalyzer.Configuration.preserveParsedData());
            }
        }

        return null;
    }

    /**
     * Select appropriate IWriter implementation.
     */
    private static IWriter writerFactory(String fileName) throws IOException
    {
        if (fileName == null)
        {
            return new StdoutWriter();
        }

        return new FileWriter(fileName, fileName.matches(_IS_GZIPPED_REGEX));
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
                    System.err.println("Error: Using stdin as input is not"
                        + " currently supported");
                    System.exit(2);
                }
                else
                {
                    throw new NullPointerException("source");
                }
            }

            IWriter writer = writerFactory(config.outputFile);
            if (writer == null)
            {
                throw new NullPointerException("writer");
            }

            IParser parser = parserFactory(config.inputFormat);
            if (parser == null)
            {
                System.err.println("Error: " + config.inputFormat.toString()
                    + ": Unsupported input format.");
                System.exit(2);
            }

            IAnalyzer analyzer = analyzerFactory(
                config.inputFormat,
                config.analysisAlgorithm);
            if (parser == null)
            {
                System.err.println("Error: "
                    + config.analysisAlgorithm.toString()
                    + ": Analysis algorithm is not available or is not"
                    + " compatible with specified input format ("
                    + config.inputFormat.toString()
                    + ").");
                System.exit(2);
            }

            doMain(source, parser, analyzer, writer);
        }
        catch (IOException ex)
        {
            Logger.getLogger(WriterCallback.class.getName())
                .log(Level.SEVERE, null, ex);
        }
    }

    // }}} Main ///////////////////////////////////////////////////////////////
}
