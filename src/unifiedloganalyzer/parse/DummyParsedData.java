package unifiedloganalyzer.parse;

import java.io.IOException;


public class DummyParsedData extends AParsedData
{
    public DummyParsedData(String originalMessage)
    {
        super(originalMessage);
    }

    @Override
    protected void appendRestTo(Appendable buff) throws IOException
    {
        // Empty implementation.
    }
}
