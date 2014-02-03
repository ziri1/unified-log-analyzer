package unifiedloganalyzer;

import trskop.ICallback;


/**
 * Interface for objects that are capable of notifying registered object when
 * data are ready for processing or when some specific event occurred.
 *
 * @author Peter Trsko
 * 
 * @param <T>
 *   Type of messages callback(s) will receive.
 */
public interface IRegisterCallbacks<T>
{
    /**
     * Register callback for later execution.
     *
     * @param callback  Callback that will be executed later.
     */
    public void registerCallback(ICallback<T> callback);
}
