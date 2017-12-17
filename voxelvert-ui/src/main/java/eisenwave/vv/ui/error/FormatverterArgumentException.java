package eisenwave.vv.ui.error;

import eisenwave.vv.ui.fmtvert.Option;

public class FormatverterArgumentException extends FormatverterException {
    
    public FormatverterArgumentException(String option, String issue) {
        super("Issue with '" + option + "': " + issue + "");
    }
    
    public FormatverterArgumentException(Option option, String issue) {
        this(option.toString(), issue);
    }
    
}
