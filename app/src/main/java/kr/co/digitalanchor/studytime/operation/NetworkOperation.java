package kr.co.digitalanchor.studytime.operation;

import android.os.AsyncTask;

import java.util.HashMap;

import kr.co.digitalanchor.studytime.operation.event.OperationEvent;

/**
 * Created by Thomas on 2015-06-16.
 */
public class NetworkOperation extends AsyncTask<Void, Void, String> {

    public static final String REQUEST = "0";
    public static final String RESPONSE = "1";

    String requestXML = "";
    String responseXML = "";
    String className = "";
    String strFilePath = null;

    Operation operation;
    String operationURL = "";
    Object respondObject = null;

    OperationEvent eventDispatcher = null;

    String zipOutputFileName;

    public NetworkOperation(String className, HashMap<String, Object> params, OperationEvent event) {

        this.className = className;

        eventDispatcher = event;

        operation = OperationFactory.getOperation(className);

        requestXML = operation.BuildOperation(params);

        operationURL = operation.getOperationUrl();

        // TODO listener
    }

    public NetworkOperation(String className, HashMap<String, Object> operationParams, String filePath, OperationEvent operationEvent) {

        this(className, operationParams, operationEvent);

        strFilePath = filePath;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {

        return responseXML;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
