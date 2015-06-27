package kr.co.digitalanchor.studytime.chat;

/**
 * Created by Thomas on 2015-06-26.
 */
public interface MessageItemListener {

    void onRequestMessageDelete(String messageId);

    void onRequestMassageRetry(String messageId);
}
