package unifiedloganalyzer.parse;

import java.io.IOException;

import unifiedloganalyzer.parse.AParsedData;


public class DummyParsedData extends AParsedData
{
    public DummyParsedData(String originalMessage)
    {
        super(originalMessage);
    }

    protected void appendRestTo(Appendable buff) throws IOException
    {
        ;   // Empty implementation.
    }
}
