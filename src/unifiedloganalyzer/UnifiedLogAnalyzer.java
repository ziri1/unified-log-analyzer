package unifiedloganalyzer;

import java.io.IOException;
//import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import unifiedloganalyzer.IAnalyzer;
import unifiedloganalyzer.IParser;
import unifiedloganalyzer.ISource;
import unifiedloganalyzer.IWriter;
import unifiedloganalyzer.adapter.AnalyzerCallback;
import unifiedloganalyzer.adapter.WriterCallback;
import unifiedloganalyzer.analyze.DummyAnalyzer;
import unifiedloganalyzer.io.FileSource;
import unifiedloganalyzer.io.FileWriter;
import unifiedloganalyzer.io.StdoutWriter;
import unifiedloganalyzer.parse.strace.StraceParser;


/**
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

    // public static void *factory

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        try
        {
            String inputFile = "strace.out";
            String outputFile = "analysis.out";

            ISource source = null;
            IParser parser = new StraceParser();
            IAnalyzer analyzer = new DummyAnalyzer();
            IWriter writer = null;

            if (args.length > 0)
            {
                inputFile = args[0];
                if (args.length > 1)
                {
                    outputFile = args[1];
                }
            }

            source = new FileSource(inputFile);

            if (outputFile.equals("-"))
            {
                writer = new StdoutWriter();
            }
            else
            {
                writer = new FileWriter(outputFile);
            }

            doMain(source, parser, analyzer, writer);
        }
        catch (IOException ex)
        {
            Logger.getLogger(WriterCallback.class.getName())
                .log(Level.SEVERE, null, ex);
        }
    }
}
