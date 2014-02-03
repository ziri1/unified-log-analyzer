package unifiedloganalyzer;

import java.util.Iterator;


/**
 * Interface for objects that provide input data that will be later processed
 * by IParser objects.
 *
 * @author Kamil Cupr
 */
public interface ISource extends Iterator<String>
{
}
