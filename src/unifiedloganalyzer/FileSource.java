/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package unifiedloganalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CZ2B1142
 */
public class FileSource implements ISource {
    private File myFile = null;
    private BufferedReader myBuffer = null;
    private String currentLine = null;
    public FileSource(String sFile) {
        myFile = new File(sFile);
        try {
            myBuffer = new BufferedReader(new FileReader(myFile));
            currentLine = myBuffer.readLine();
            //TODO: open file, read file line by line until EOF
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileSource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileSource.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean hasNext() {
        return currentLine != null;
    }

    @Override
    public String next() {
        String ret = currentLine;
        try {
            currentLine = myBuffer.readLine();
        } catch (IOException ex) {
            Logger.getLogger(FileSource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    @Override
    public void remove() {
        //destructive next() does this work for us
    }
}
