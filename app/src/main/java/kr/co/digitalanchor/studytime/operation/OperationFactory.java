package kr.co.digitalanchor.studytime.operation;

/**
 * Created by Thomas on 2015-06-16.
 */
public class OperationFactory {

    public static Operation getOperation(String className) {

        Operation operation = null;

        try {

            Class opClass = Class.forName(className);
            operation = (Operation) opClass.newInstance();

        } catch (Exception e) {

            operation = null;
        }

        return operation;
    }
}
