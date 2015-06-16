package kr.co.digitalanchor.studytime.operation;

import java.util.HashMap;

/**
 * Created by Thomas on 2015-06-16.
 */
public interface Operation {

    String BuildOperation(HashMap<String, Object> map);

    Object ParseOperationResult(String result);

    String getOperationUrl();
}
