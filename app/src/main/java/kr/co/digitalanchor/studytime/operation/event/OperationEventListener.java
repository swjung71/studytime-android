package kr.co.digitalanchor.studytime.operation.event;

import java.util.EventListener;

/**
 * Created by Thomas on 2015-06-16.
 */
public interface OperationEventListener extends EventListener {

    void onParsingCompleted(OperationEvent event, String requestURL, Object result);
}
