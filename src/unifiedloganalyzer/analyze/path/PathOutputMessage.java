package unifiedloganalyzer.analyze.path;

import java.io.IOException;

import unifiedloganalyzer.IOutputMessage;
import unifiedloganalyzer.utils.IHasPath;


/**
 * Analysis result that has as its main purpose pass file path allong.
 *
 * @author Peter Trsko
 */
public class PathOutputMessage implements IOutputMessage, IHasPath
{
    private String _path;

    // {{{ Constructors ///////////////////////////////////////////////////////

    public PathOutputMessage(String path)
    {
        _path = path;
    }

    /**
     * Produce empty instance.
     *
     * @return
     *   New PathOutputMessage instance with path set to <code>null</code>.
     */
    public static PathOutputMessage empty()
    {
        return new PathOutputMessage(null);
    }

    // }}} Constructors ///////////////////////////////////////////////////////

    // {{{ IHasPath interface implementation //////////////////////////////////

    /**
     * {@inheritDoc}
     */
    public String getPath()
    {
        return _path;
    }

    /**
     * {@inheritDoc}
     */
    public void setPath(String path)
    {
        _path = path;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasPath()
    {
        return _path != null;
    }

    // }}} IHasPath interface implementation //////////////////////////////////

    // {{{ IOutputMessage interface implementation ////////////////////////////

    /**
     * {@inheritDoc}
     */
    public void appendTo(Appendable buff) throws IOException
    {
        buff.append(_path);
    }

    /**
     * {@inheritDoc}
     */
    public boolean messageEquals(IOutputMessage message)
    {
        if (message != null && message instanceof PathOutputMessage)
        {
            String theirPath = ((PathOutputMessage)message).getPath();
            String ourPath = this.getPath();

            return (theirPath == null && ourPath == null)
                // Short-circuiting before calling equals on null.
                || (ourPath != null && ourPath.equals(theirPath));
        }

        return false;
    }

    // }}} IOutputMessage interface implementation ////////////////////////////
}
