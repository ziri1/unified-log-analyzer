package unifiedloganalyzer.utils;


/**
 * Utility interface for objects with associated PID (system process ID).
 *
 * @author Peter Trsko
 */
public interface IHasPid
{
    /**
     * Get value of PID (system process ID).
     *
     * @return
     *   PID (system process ID) that is relevant to this instance.
     */
    int getPid();

    /**
     * Change value of PID (system process ID) that is associated with this
     * object.
     *
     * @param pid
     *   New value of PID (system process ID) that is relevant to this
     *   instance.
     */
    void setPid(int pid);

    /**
     * Check if there is associated PID (system process ID) with this instance.
     *
     * @return
     *    <code>true</code> if there is a PID (system process ID) associated
     *    with this instance and <code>false</code> otherwise.
     */
    public boolean hasPid();
}
