package kr.co.digitalanchor.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Thomas on 2015-06-29.
 */
public class NotificationID {

    private final static AtomicInteger c = new AtomicInteger(0);

    public static int getID() {

        return c.incrementAndGet();
    }
}
