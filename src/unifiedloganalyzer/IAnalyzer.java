package unifiedloganalyzer;

import trskop.ICallback;
import unifiedloganalyzer.IRegisterCallbacks;
import unifiedloganalyzer.IWriter;


/**
 * Interface for object that perform analysis of parsed data.
 *
 * @author Peter Trsko
 */
public interface IAnalyzer extends IRegisterCallbacks<IOutputMessage>
{
    /**
     * Send ParsedData for analysis.
     *
     * @param data
     *   Parsed log message.
     */
    void analyze(ParsedData data);
}
