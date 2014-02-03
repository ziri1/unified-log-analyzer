package unifiedloganalyzer.analyze;

import unifiedloganalyzer.IOutputMessage;
import unifiedloganalyzer.IParsedData;
import unifiedloganalyzer.analyze.AAnalyzer;
import unifiedloganalyzer.utils.ICompoundMessage;


/**
 *
 *
 * @author Peter Trsko
 */
public class FilterAnalyzer extends AAnalyzer
{
    public static class Configuration
    {
        private boolean _forgetEmptyMessages = false;
        private boolean _forgetNonCompositeMessages = false;
        private boolean _forgetParseErrors = false;
        private boolean _forgetParsedDataInComposites = true;

        private Configuration()
        {
            _forgetEmptyMessages = false;
            _forgetNonCompositeMessages = false;
            _forgetParseErrors = false;
            _forgetParsedDataInComposites = true;
        }

        public static Configuration theDefault()
        {
            return new Configuration();
        }

        public static Configuration compositesOnly(Configuration config)
        {
            config._forgetNonCompositeMessages = true;
            config._forgetParsedDataInComposites = true;
            config._forgetParseErrors = true;
            config._forgetEmptyMessages = true;

            return config;
        }

        public static Configuration compositesOnly()
        {
            return compositesOnly(theDefault());
        }

        public static Configuration noEmptyMessages(Configuration config)
        {
            config._forgetEmptyMessages = true;

            return config;
        }

        public static Configuration noEmptyMessages()
        {
            return noEmptyMessages(theDefault());
        }

        public static Configuration noParseErrors(Configuration config)
        {
            config._forgetParseErrors = true;

            return config;
        }

        public static Configuration noParseErrors()
        {
            return noParseErrors(theDefault());
        }

        public boolean forgetEmptyMessages()
        {
            return _forgetEmptyMessages;
        }

        public boolean forgetNonCompositeMessages()
        {
            return _forgetNonCompositeMessages;
        }

        public boolean forgetParseErrors()
        {
            return _forgetParseErrors;
        }

        public boolean forgetParsedDataInComposites()
        {
            return _forgetParsedDataInComposites;
        }
    }

    private Configuration _config = null;

    // {{{ Constructors ///////////////////////////////////////////////////////

    public FilterAnalyzer(Configuration config)
    {
        super();

        _config = config;
    }

    public FilterAnalyzer()
    {
        this(Configuration.theDefault());
    }

    // }}} Constructors ///////////////////////////////////////////////////////

    private void processEmptyMessageAndParseError(IParsedData data)
    {
        IOutputMessage outputMessage = null;

        if (data instanceof IOutputMessage)
        {
            outputMessage = (IOutputMessage)data;
        }
        else
        {
            outputMessage = new DummyOutputMessage(data);
        }

        runCallbacks(outputMessage);
    }

    // {{{ AAnalyzer, implementation of abstract methods //////////////////////

    protected void processEmptyMessage(IParsedData parsedData)
    {
        if (!_config.forgetParseErrors())
        {
            processEmptyMessageAndParseError(parsedData);
        }
    }

    protected void processParsedMessage(IParsedData parsedData)
    {
        IOutputMessage outputMessage = null;

        if (parsedData instanceof ICompoundMessage)
        {
            if (_config.forgetParsedDataInComposites())
            {
                outputMessage =
                    ((ICompoundMessage)parsedData).getOutputMessage();
            }
        }
        else if(_config.forgetNonCompositeMessages())
        {
            return;
        }

        if (outputMessage == null)
        {
            outputMessage = new DummyOutputMessage(parsedData);
        }

        runCallbacks(outputMessage);
    }

    protected void processParseError(IParsedData parsedData)
    {
        if (!_config.forgetParseErrors())
        {
            processEmptyMessageAndParseError(parsedData);
        }
    }

    // }}} AAnalyzer, implementation of abstract methods //////////////////////
}
