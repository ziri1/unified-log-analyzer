package unifiedloganalyzer;


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
     * @param parsedData
     *   Parsed log message.
     */
    void analyze(ParsedData parsedData);
}
