package kr.co.digitalanchor.studytime.operation.event;

/**
 * Created by Thomas on 2015-06-16.
 */
public enum OperationEventEnumeration {

    PARSING_COMPLETED("Received XML Parsing Complete. Result Object Must Be Parsed with Specific Class which need to TargetActivity.");

    private String description;

    OperationEventEnumeration(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
