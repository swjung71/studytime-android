package kr.co.digitalanchor.studytime.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.SimpleXmlRequest;
import com.orhanobut.logger.Logger;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.chat.ChildChatActivity;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.AdultFileResult;
import kr.co.digitalanchor.studytime.model.ChildLoginResult;
import kr.co.digitalanchor.studytime.model.Files;
import kr.co.digitalanchor.studytime.model.GetAdultDB;
import kr.co.digitalanchor.studytime.model.ParentLogin;
import kr.co.digitalanchor.studytime.model.api.HttpHelper;
import kr.co.digitalanchor.utils.StringValidator;

import static kr.co.digitalanchor.studytime.model.api.HttpHelper.SUCCESS;

/**
 * Created by Thomas on 2015-06-15.
 */
public class LoginChildActivity extends BaseActivity implements View.OnClickListener {

    private final int REQUEST_CHILD_LOGIN = 50001;
    private final int REQUEST_ADD_INFO = 50003;
    private final int COMPLETE_CHILD_LOGIN = 50002;
    private final int REQUEST_ADULT_FILE_LIST = 50005;
    private final int REQUEST_ADULT_FILE = 50004;

    private final int ACTIVITY_ADDITIONAL_INFO = 60001;

    EditText mEditEmailAddr;

    EditText mEditPassword;

    EditText mEditChildName;

    DBHelper mDBHelper;

    String parentId;

    String name;

    ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_child_login);

        mDBHelper = new DBHelper(getApplicationContext());

        // instantiate it within the onCreate method
        mProgressDialog = new ProgressDialog(LoginChildActivity.this);
        mProgressDialog.setMessage("DB 업데이트");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);

        initView();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    private void initView() {

        mEditEmailAddr = (EditText) findViewById(R.id.editEmailAddr);

        mEditPassword = (EditText) findViewById(R.id.editPassword);

        mEditChildName = (EditText) findViewById(R.id.editChildName);

        ((Button) findViewById(R.id.buttonLogin)).setOnClickListener(this);
    }

    @Override
    protected void onHandleMessage(Message msg) {

        Bundle data = null;

        switch (msg.what) {

            case REQUEST_CHILD_LOGIN:

                requestLogin();

                break;

            case REQUEST_ADD_INFO:

                showAddInfo(parentId, name);

                break;

            case REQUEST_ADULT_FILE:

                data = msg.getData();

                downloadAdultFile(data.getString("files"));

                break;

            case COMPLETE_CHILD_LOGIN:

                showMain();

                break;

            case REQUEST_ADULT_FILE_LIST:

                requestAdultFile();

                break;


            default:

                break;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.buttonLogin:

//                sendEmptyMessage(REQUEST_ADULT_FILE_LIST);

                if (isValidate()) {

                    sendEmptyMessage(REQUEST_CHILD_LOGIN);
                }

                break;

            default:

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTIVITY_ADDITIONAL_INFO) {

            if (resultCode == RESULT_OK) {

                sendEmptyMessage(COMPLETE_CHILD_LOGIN);
            }
        }
    }

    private void showAddInfo(String parentId, String name) {

        Intent intent = new Intent();

        intent.setClass(getApplicationContext(), AddInfoActivity.class);

        Logger.d(parentId + " " + name);

        intent.putExtra("ParentID", parentId);
        intent.putExtra("Name", name);

        startActivityForResult(intent, ACTIVITY_ADDITIONAL_INFO);
    }

    private void showMain() {

        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), ChildChatActivity.class);

        startActivity(intent);

        finish();
    }

    private boolean isValidate() {


        String tmp = null;

        String msg = null;

        do {

            tmp = mEditEmailAddr.getText().toString();

            if (TextUtils.isEmpty(tmp)) {

                msg = "이메일을 입력하세요.";

                break;
            }

            if (!StringValidator.isEmail(tmp)) {

                msg = " 이메일 형식에 맞지 않습니다.";

                break;
            }

            tmp = null;

            tmp = mEditPassword.getText().toString();

            if (TextUtils.isEmpty(tmp)) {

                msg = "비밀번호를 입력하세요.";

                break;
            }

            if (!StringValidator.isPassword(tmp)) {

                msg = "비밀번호 형식에 맞지 않습니다.";

                break;
            }

            tmp = null;

            tmp = mEditChildName.getText().toString();

            if (TextUtils.isEmpty(tmp)) {

                msg = "이름을 입력하세요.";

                break;
            }

        } while (false);

        if (TextUtils.isEmpty(msg)) {

            return true;

        } else {

            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

            return false;
        }
    }

    private void requestLogin() {

        showLoading();

        String tmp = null;

        ParentLogin model = new ParentLogin();

        model.setEmail(mEditEmailAddr.getText().toString());

        model.setPassword(mEditPassword.getText().toString());

        SimpleXmlRequest request = HttpHelper.getChildLogin(model, new Response.Listener<ChildLoginResult>() {
            @Override
            public void onResponse(ChildLoginResult response) {

                Bundle data = null;

                switch (response.getResultCode()) {

                    case SUCCESS:

                        dismissLoading();

                        parentId = response.getParentID();

                        name = mEditChildName.getText().toString();

                        sendEmptyMessage(REQUEST_ADULT_FILE_LIST);

//                        sendEmptyMessage(REQUEST_ADD_INFO);

                        break;

                    default:

                        handleResultCode(response.getResultCode(), response.getResultMessage());

                        break;
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                handleError(error);
            }
        });

        addRequest(request);
    }

    private void downloadAdultFile(String file) {

        Logger.d("downloadAdultFile " + file);

        if (TextUtils.isEmpty(file)) {

            sendEmptyMessage(REQUEST_ADD_INFO);

            return;
        }

        new DownloadFileFromURL().execute(file);
    }

    private void requestAdultFile() {

        showLoading();

        GetAdultDB model = new GetAdultDB();

        String date = null;// mDBHelper.getAdultFile();

        if (date != null) {

            model.setDate(date);
        }

        SimpleXmlRequest request = HttpHelper.getAdultFileList(model,
                new Response.Listener<AdultFileResult>() {

                    @Override
                    public void onResponse(AdultFileResult response) {

                        Bundle data = null;

                        switch (response.getResultCode()) {

                            case SUCCESS:

                                dismissLoading();

                                data = new Bundle();

                                ArrayList<Files> files = response.getFileName();

                                mDBHelper.setAdultFile(response);

                                data.putString("files", response.getFileName().get(files.size() - 1).getFileName());
                                Logger.d(data.toString());

                                sendMessage(REQUEST_ADULT_FILE, data);

                                break;

                            default:

                                break;
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        handleError(error);
                    }
                });

        addRequest(request);
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Logger.d("onPreExecute");

            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            if (params == null || params.length < 1) {

                return null;
            }
/*
            InputStream input = null;
            URLConnection conn = null;
            FileOutputStream fos = null;
            BufferedOutputStream bos = null;

            try {


                URL url = new URL("https://www.dastudytime.kr/resources/studytime/" + params[0]);

                conn = url.openConnection();
                conn.connect();

                int lengthOfFile = conn.getContentLength();

                input = url.openStream();

                fos = new FileOutputStream("/sdcard/" + params[0]); // target은 File 객체입니다.
                bos = new BufferedOutputStream(fos);

                byte data[] = new byte[8192];
                long total = 0;
                int count = 0;

                while ((count = input.read(data)) > 0) {
                    // allow canceling with back button

                    if (isCancelled()) {
                        return null;
                    }

                    total += count;
                    // publishing the progress....
                    if (lengthOfFile > 0) // only if total length is known
                        publishProgress((int) (total * 100 / lengthOfFile));

                    bos.write(data, 0, count);
                }

//                BufferedReader br = new BufferedReader(new InputStreamReader(input, "UTF-8"));
//
//                Logger.d(lengthOfFile + " file");
//
//                mDBHelper.setTableAdultUrl(br, lengthOfFile, this);

//                mDBHelper.setTableAdultUrl(br);

                return null;

            } catch (Exception e) {

               Logger.e(e.toString());

            } finally {

                try {

                    if (input != null)
                        input.close();

                    if (fos != null)
                        fos.close();

                    if (bos != null)
                        bos.close();

                } catch (IOException ignored) {

                }
            }
            */
            FTPClient ftp = new FTPClient();

            try {

                ftp.connect("14.63.225.89", 21);

                ftp.login("anonymous", "nobody");

                ftp.setFileType(FTP.BINARY_FILE_TYPE);
                ftp.enterLocalActiveMode();

                String remote = "/pub/" + params[0];

                File downloadFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + params[0]);

                OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));

                InputStream inputStream = ftp.retrieveFileStream(remote);

                byte[] data = new byte[4096];

                int bytesRead = -1;
                long total = 0;

                while ((bytesRead = inputStream.read(data)) != -1) {

                    outputStream.write(data, 0, bytesRead);

                    total += bytesRead;

                    publishProgress(String.valueOf(total * 100 / 150000000L), "DB 다운로드");
                }

                boolean success = ftp.completePendingCommand();

                outputStream.close();

                inputStream.close();

                String fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + params[0];

                File file = new File(fileName);

                long fileSize = 0;

                if (file.exists()) {

                    fileSize = file.length();
                }

                BufferedReader br = new BufferedReader(new FileReader(fileName));

//                mDBHelper.setTableAdultUrl(br);

                String line = null;

                total = 0;

                SQLiteDatabase db = mDBHelper.getWritableDatabase();

                try {

                    while ((line = br.readLine()) != null) {

//                db.rawQuery(line, null);
                        db.execSQL(line);

                        total += line.getBytes().length * 4;

                        publishProgress(String.valueOf(total * 100/fileSize), "DB 적용 중");
                    }

                } catch (IOException e) {
                    Logger.d(e.toString());
                } finally {

                    db.close();
                    br.close();
                }

//                publishProgress(String.valueOf(100), "DB 적용 중");


            } catch (SocketException e) {

                Logger.e(e.toString());
            } catch (IOException e) {

                Logger.e(e.toString());
            } catch (Exception e) {

                Logger.e(e.toString());
            } finally {

                try {

                    if (ftp.isConnected()) {

                        ftp.logout();

                        ftp.disconnect();
                    }

                } catch (IOException e) {

                    Logger.e(e.toString());
                }
            }

            /*
            try {

                ftp.connect("14.63.225.89", 21);

                ftp.login("anonymous", "nobody");

                ftp.setFileType(FTP.BINARY_FILE_TYPE);
                ftp.enterLocalActiveMode();

                String remote = "/pub/" + params[0];

                Logger.d("ftp status: " + ftp.getStatus());

                File downloadFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + params[0]);

                if (downloadFile.createNewFile()) {

                    Logger.d("File is created!");

                } else {
                    Logger.d("File already exists.");

                }

                FileOutputStream local = new FileOutputStream(downloadFile);

                boolean rtn = ftp.retrieveFile(remote, local);

                Logger.d("result " + rtn);

                local.close();

                ftp.disconnect();

                BufferedReader br = new BufferedReader(new FileReader(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + params[0]));

                mDBHelper.setTableAdultUrl(br);

                br.close();

            } catch (SocketException e) {

                Logger.e(e.toString());
            } catch (IOException e) {

                Logger.e(e.toString());
            } catch (Exception e) {

                Logger.e(e.toString());
            }

*/
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(Integer.parseInt(values[0]));
            mProgressDialog.setMessage(values[1]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            mProgressDialog.dismiss();

            sendEmptyMessage(REQUEST_ADD_INFO);
        }

    }

}
