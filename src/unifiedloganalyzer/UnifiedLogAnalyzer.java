/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package unifiedloganalyzer;

/**
 *
 * @author CZ2B1142
 */
public class UnifiedLogAnalyzer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ISource source = new FileSource("strace.out");
        IParser parser = null;
        IWriter writer = null;
        while (source.hasNext()) {
            writer.write(parser.parse(source.next()));
        }
        writer.flush();
    }
}
