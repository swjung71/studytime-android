package kr.co.digitalanchor.studytime.monitor;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by user on 2015-08-12.
 */
public class DownloadFileTask extends AsyncTask<String, Integer, String> {

    public interface TaskListener {

        void onPreExecute();

        void onProgressUpdate(Integer... values);

        void onPostExecute(String s);
    }

    Context context;

    TaskListener listener;

    public DownloadFileTask(Context context) {

        this.context = context;

    }

    public void setListener(TaskListener listener) {

        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        if (params == null || params.length < 1) {

            return null;
        }

        InputStream input = null;

        try {

            URL url = new URL(params[0]);
            URLConnection connection = url.openConnection();
            connection.connect();
            input = new BufferedInputStream(url.openStream(), 8192);


        } catch (Exception e) {


        }


        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
