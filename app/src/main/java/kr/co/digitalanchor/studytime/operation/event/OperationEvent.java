package kr.co.digitalanchor.studytime.operation.event;

import java.util.EventObject;

/**
 * Created by Thomas on 2015-06-16.
 */
public class OperationEvent extends EventObject {

    public OperationEvent(Object o) {

        super(o);
    }

    public OperationEventListenerList eventListenerList = OperationEventListenerList.getInstance();

    public void addEventListener(OperationEventListener listener) {

        eventListenerList.add(OperationEventListener.class, listener);
    }

    public void removeEventListener(OperationEventListener listener) {

        eventListenerList.remove(OperationEventListener.class, listener);
    }

    public void dispatchParsingCompletedEvent(OperationEvent event, String url, Object result) {

        Object[] listeners = eventListenerList.getListenerList();

        Object eventObj = event.getSource();

        String eventName = eventObj.toString();

        for (int i = 0; i < listeners.length; i += 2) {

            if (listeners[i] == OperationEventListener.class && this.source == listeners[i + 1]) {

                if (eventName.contains("PARSING_COMPLETED")) {

                    ((OperationEventListener) listeners[i + 1]).onParsingCompleted(event, url, result);
                }
            }
        }
    }
}
