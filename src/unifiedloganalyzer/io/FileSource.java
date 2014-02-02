package unifiedloganalyzer.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import unifiedloganalyzer.ISource;


/**
 *
 * @author Kamil Cupr
 */
public class FileSource implements ISource
{
    private File myFile = null;
    private BufferedReader myBuffer = null;
    private String currentLine = null;

    public FileSource(String sFile, boolean isGzipped)
    {
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

    public FileSource(String file)
    {
        this(file, false);
    }

    public boolean hasNext() {
        return currentLine != null;
    }

    public String next() {
        String ret = currentLine;
        try {
            currentLine = myBuffer.readLine();
        } catch (IOException ex) {
            Logger.getLogger(FileSource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    public void remove() {
        //destructive next() does this work for us
    }
}
