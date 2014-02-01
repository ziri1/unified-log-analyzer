package unifiedloganalyzer;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import unifiedloganalyzer.IAnalyzer;
import unifiedloganalyzer.IParser;
import unifiedloganalyzer.ISource;
import unifiedloganalyzer.IWriter;
import unifiedloganalyzer.adapter.AnalyzerCallback;
import unifiedloganalyzer.adapter.WriterCallback;
import unifiedloganalyzer.dummy.DummyAnalyzer;
import unifiedloganalyzer.io.FileSource;
import unifiedloganalyzer.io.FileWriter;
import unifiedloganalyzer.io.StdoutWriter;
import unifiedloganalyzer.strace.StraceParser;


/**
 *
 * @author Kamil Cupr
 */
public class UnifiedLogAnalyzer
{
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

            parser.registerCallback(new AnalyzerCallback(analyzer));
            analyzer.registerCallback(new WriterCallback(writer));

            while (source.hasNext()) {
                parser.parse(source.next());
            }
            writer.eof();
        }
        catch (IOException ex)
        {
            Logger.getLogger(WriterCallback.class.getName())
                .log(Level.SEVERE, null, ex);
        }
    }
}
