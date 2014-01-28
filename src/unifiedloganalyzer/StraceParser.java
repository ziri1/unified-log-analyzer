/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package unifiedloganalyzer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author CZ2B1142
 */
public class StraceParser implements IParser {
/*
    execve("/homes/cz2b1142/bin/m", ["m", "n"], ["VOBROOT=", "HOSTNAME=czbrqh055x", "SHELL=/bin/bash", "TERM=xterm", "HISTSIZE=1000", "SSH_CLIENT=::ffff:172.27.6.35 55747 22", "PERL5LIB=/homes/cz2b1142/perl", "KVM_EXE=/vobs/Opera_3rdParty/PreBuilt/kvm", "SSH_TTY=/dev/pts/40", "ANT_HOME=/vobs/Opera_DevTools/ant/apache-ant-1.6.5", "LC_ALL=C", "USER=cz2b1142", "LD_LIBRARY_PATH=/vobs/Opera_DevTools/PreBuilt/x86:/vobs/Opera_3rdParty/QTE/output/x86/qtopia-core-commercial-src-4.1.4/lib:/vobs/Opera_Build/deployment/x86:/usr/kerberos/lib:::/vobs/Opera_DevTools/gltt/lib:/vobs/Opera_Platform_Linux/Linux_BSP/ifx-com-linux/source/user/opensource/openssl/opera/x86", "LS_COLORS=no=00:fi=00:di=00;34:ln=00;36:pi=40;33:so=00;35:bd=40;33;01:cd=40;33;01:or=01;05;37;41:mi=01;05;37;41:ex=00;32:*.cmd=00;32:*.exe=00;32:*.com=00;32:*.btm=00;32:*.bat=00;32:*.sh=00;32:*.csh=00;32:*.tar=00;31:*.tgz=00;31:*.arj=00;31:*.taz=00;31:*.lzh=00;31:*.zip=00;31:*.z=00;31:*.Z=00;31:*.gz=00;31:*.bz2=00;31:*.bz=00;31:*.tz=00;31:*.rpm=00;31:*.cpio=00;31:*.jpg=00;35:*.gif=00;35:*.bmp=00;35:*.xbm=00;35:*.xpm=00;35:*.png=00;35:*.tif=00;35:", "CLEARCASE_CMDLINE=setview cz2b1142_latest", "PATH=/vobs/Opera_DevTools/scripts:/opt/uclibc-toolchain/gcc-3.3.6/toolchain-mips/bin:/opt/toolchain/montavista/devkit/mips/fp_be/bin:/opt/rational/clearcase/bin:/opt/j2sdk1.4.2_07/bin:/usr/kerberos/bin:/usr/local/bin:/bin:/usr/bin:/usr/X11R6/bin:/homes/cz2b1142/bin:/homes/cz2b1142/bin/priv:/homes/cz2b1142/cc/cov-analysis-linux-6.5.1/bin:/vobs/Opera_DevTools/ant/apache-ant-1.6.5/bin:/vobs/Opera_DevTools/GDB:/vobs/Opera_DevTools/gltt/bin:/vobs/Opera_Platform_Linux/Libraries/x86:/opt/valgrind-3.1.0/bin:/opt/kcachegrind-0.4.6/bin:/opt/graphviz-2.8/bin:/homes/cz2b1142/bin:/homes/cz2b1142/bin/priv:/homes/cz2b1142/cc/cov-analysis-linux-6.5.1/bin", "MAIL=/var/spool/mail/cz2b1142", "PWD=/homes/cz2b1142/install/strace-4.5.14", "INPUTRC=/etc/inputrc", "CLEARCASE_ROOT=/view/cz2b1142_latest", "JAVA_HOME=/opt/j2sdk1.4.2_07", "EDITOR=vi", "LANG=en_US", "PS1=[\\u@cz2b1142_latest] \\W$ ", "WTK_HOME=/vobs/Opera_DevTools/j2me_wtk/2.2/linux", "SSH_ASKPASS=/usr/libexec/openssh/gnome-ssh-askpass", "HOME=/homes/cz2b1142", "SHLVL=4", "LD_ASSUME_KERNEL=2.2.5", "PYTHONPATH=:/vobs/Opera_DevTools:/vobs/Opera_DevTools/PythonModules", "LOGNAME=cz2b1142", "SSH_CONNECTION=::ffff:172.27.6.35 55747 ::ffff:172.27.4.64 22", "LESSOPEN=|/usr/bin/lesspipe.sh %s", "GLIBCPP_FORCE_NEW=1", "DISPLAY=localhost:17.0", "G_BROKEN_FILENAMES=1", "_=./strace", "OLDPWD=/homes/cz2b1142/install"]) = 0 
*/
    Pattern p_execve = Pattern.compile("^execve\\(");
    
    @Override
    public ParsedData parse(String data) {
        ParsedData ret = new ParsedData();
        Matcher m_execve = p_execve.matcher(data);
        if (m_execve.matches()) {
            //TODO: parse execve, mainly PWD, return code, path, etc.
            ret.setPID(null, null);
            ret.setPWD(null);
        }
        return ret;
    }
}
