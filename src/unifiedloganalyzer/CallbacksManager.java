package unifiedloganalyzer;

import java.lang.IllegalArgumentException;
import java.util.ArrayList;
import java.util.List;

import trskop.ICallback;

import unifiedloganalyzer.IRegisterCallbacks;


/**
 *
 * @author Peter Trsko
 */
public class CallbacksManager<T> implements IRegisterCallbacks<T>
{
    private List<ICallback<T>> _callbacks;

    // {{{ Constructors ///////////////////////////////////////////////////////

    public CallbacksManager()
    {
        _callbacks = new ArrayList<ICallback<T>>();
    }

    public CallbacksManager(ICallback<T> callback)
    {
        this();

        registerCallback(callback);
    }

    // }}} Constructors ///////////////////////////////////////////////////////

    public void registerCallback(ICallback<T> callback)
    {
        _callbacks.add(callback);
    }

    public void runCallbacks(T message)
    {
        if (message == null)
        {
            throw new IllegalArgumentException("null");
        }

        for (ICallback<T> callback : _callbacks)
        {
            callback.runCallback(message);
        }
    }
}
