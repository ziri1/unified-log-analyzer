package unifiedloganalyzer;

import unifiedloganalyzer.IRegisterCallbacks;
import unifiedloganalyzer.ParsedData;


/**
 *
 * @author Kamil Cupr
 */
public interface IParser extends IRegisterCallbacks<ParsedData>
{
    void parse(String data);
}
