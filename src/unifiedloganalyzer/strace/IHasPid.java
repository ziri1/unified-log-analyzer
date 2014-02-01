package unifiedloganalyzer.strace;


/**
 *
 * @author Peter Trsko
 */
public interface IHasPid
{
    int getPid();
    void setPid(int pid);
    public boolean hasPid();
}
