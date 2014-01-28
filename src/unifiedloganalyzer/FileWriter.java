/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package unifiedloganalyzer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CZ2B1142
 */
public class FileWriter implements IWriter {
    File myFile = null;
    BufferedWriter myWriter = null;
    ParsedData previous = null;
    
    public FileWriter(String sFile) {
        myFile = new File(sFile);
        try {
            myWriter = new BufferedWriter(new java.io.FileWriter(myFile));
        } catch (IOException ex) {
            Logger.getLogger(FileWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void write(ParsedData data) {
        if (previous != null) {
            if (previous.equals(data)) {
                return;
            } else {
                flush();
            }
        }
        previous = data;
    }

    @Override
    public void flush() {
        try {
            if (previous!=null)
                myWriter.write(previous.toString() + "\n");
            previous = null;
        } catch (IOException ex) {
            Logger.getLogger(FileWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void eof() {
        //TODO: previous should be queue maintaining more complex state
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
