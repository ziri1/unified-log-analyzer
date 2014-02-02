package unifiedloganalyzer.main;

import unifiedloganalyzer.main.InputFormat;
import unifiedloganalyzer.main.AnalysisAlgorithm;


/**
 * Internal application configuration produced by parsing command line options.
 *
 * @author Peter Trsko
 */
public class Configuration
{
    public String inputFile = null;
    public String outputFile = null;
    public InputFormat inputFormat = null;
    public AnalysisAlgorithm analysisAlgorithm = null;

    private Configuration()
    {
        inputFormat = InputFormat.STRACE;
        analysisAlgorithm = AnalysisAlgorithm.STRACE_PATH_ANALYSIS;
    }

    public static Configuration theDefault()
    {
        return new Configuration();
    }
}
