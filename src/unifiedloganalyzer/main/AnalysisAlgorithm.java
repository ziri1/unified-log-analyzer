package unifiedloganalyzer.main;

import unifiedloganalyzer.main.InputFormat;


public enum AnalysisAlgorithm
{
    DUMMY(),
    STRACE_PATH_ANALYSIS(InputFormat.STRACE),
    STRACE_PATH_ANALYSIS_PARSED_DATA_PRESERVED(InputFormat.STRACE);

    /**
     * List of supported input formats.
     *
     * Value <code>null</code> behaves as wildcard that covers any format.
     */
    private InputFormat[] _supportedInputFormats = null;

    private AnalysisAlgorithm()
    {
        _supportedInputFormats = null;
    }

    private AnalysisAlgorithm(InputFormat inputFormat)
    {
        if (inputFormat == null)
        {
            throw new IllegalArgumentException("null");
        }

        _supportedInputFormats = new InputFormat[1];
        _supportedInputFormats[0] = inputFormat;
    }

    private AnalysisAlgorithm(InputFormat[] inputFormats)
    {
        if (inputFormats == null)
        {
            throw new IllegalArgumentException("null");
        }

        _supportedInputFormats = inputFormats;
    }

    public boolean isSupportedForInputFormat(InputFormat inputFormat)
    {
        if (_supportedInputFormats == null)
        {
            return true;
        }

        for (InputFormat supportedInputFormat : _supportedInputFormats)
        {
            if (supportedInputFormat.equals(inputFormat))
            {
                return true;
            }
        } 

        return false;
    }

    public String toArgument()
    {
        return toString().toLowerCase().replace('_', '-');
    }

    public StringBuilder listSupportedInputFormats(StringBuilder buff)
    {
        if (_supportedInputFormats == null)
        {
            buff.append("* [all supported input formats]");
        }
        else
        {
            boolean isFirst = true;
            for (InputFormat supportedInputFormat : _supportedInputFormats)
            {
                if (isFirst)
                {
                    isFirst = false;
                }
                else
                {
                    buff.append(", ");
                }
                buff.append(supportedInputFormat.toArgument());
            } 
        }

        return buff;
    }
}
