package unifiedloganalyzer;

import trskop.ICallback;


/**
 *
 * @author Peter Trsko
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
