package kr.co.digitalanchor.studytime.monitor;

import android.content.Context;
import android.os.AsyncTask;

import java.io.InputStream;

/**
 * Created by user on 2015-08-12.
 */
public class DownloadFileTask extends AsyncTask<String, Void, String> {

    Context context;

    public DownloadFileTask(Context context) {

        this.context = context;

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


        } catch (Exception e) {


        }


        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
