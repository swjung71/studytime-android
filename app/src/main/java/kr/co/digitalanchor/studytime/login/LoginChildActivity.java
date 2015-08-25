package kr.co.digitalanchor.studytime.login;

import android.app.ProgressDialog;
import android.content.Intent;
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
import org.apache.commons.net.ftp.FTPFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
        mProgressDialog.setMessage("DB 다운로드 중");
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

    class DownloadFileFromURL extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Logger.d("onPreExecute");

            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            boolean result = false;

            if (params == null || params.length < 1) {

                return result;
            }

            FTPClient ftp = new FTPClient();

            long fileSize = 0L;
            long total = 0L;

            int bytesRead = -1;


            try {

                ftp.connect("14.63.225.89", 21);

                ftp.login("anonymous", "nobody");

                ftp.setFileType(FTP.BINARY_FILE_TYPE);

                ftp.enterLocalActiveMode();

                ftp.changeWorkingDirectory("/pub/");

                FTPFile[] files = ftp.listFiles();

                int i = 0;
                for (; files.length > i; i++) {

                    if (files[i].getName().equals(params[0])) {

                        break;
                    }
                }

                try {

                    fileSize = files[i].getSize();

                } catch (ArrayIndexOutOfBoundsException e) {

                    fileSize = 77041664L;
                }

                String remote = params[0];

                File downloadFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + params[0]);

                OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));

                InputStream inputStream = ftp.retrieveFileStream(remote);

                byte[] data = new byte[4096];

                bytesRead = -1;
                total = 0;

                while ((bytesRead = inputStream.read(data)) != -1) {

                    outputStream.write(data, 0, bytesRead);

                    total += bytesRead;

                    publishProgress((int) (total * 100 / fileSize));

                }

                boolean success = ftp.completePendingCommand();

                outputStream.close();

                inputStream.close();

            } catch (IOException e) {

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

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    mProgressDialog.setMessage("DB 적용중");
                }
            });

            FileInputStream fis = null;
            FileOutputStream fos = null;

            try {

                String fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + params[0];

                String dir = "/data/data/" + getApplicationContext().getPackageName() + "/databases";
                String dest = "adult.db";

                File directory = new File(dir);

                if (!directory.exists()) {

                    directory.mkdir();
                }

                File file2 = new File(fileName);
                File file = new File(dir + "/" + dest);


                if (file.exists()) {

                    file.delete();
                }

                file.createNewFile();

                fis = new FileInputStream(file2);

                fos = new FileOutputStream(file);

                Logger.d(fileName);
                Logger.d(dir + "/" + dest);

                Logger.d(file2.length() + " " + file.length());

                byte[] data = new byte[4096];

                bytesRead = -1;
                total = 0;

                while ((bytesRead = fis.read(data)) != -1) {

                    fos.write(data, 0, bytesRead);

                    total += bytesRead;

                    publishProgress((int) (total * 100 / fileSize));

                }

                result = true;

            } catch (IOException e) {

                Logger.e(e.toString());

            } finally {

                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {

                    }
                }

                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {

                    }
                }
            }

            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            mProgressDialog.dismiss();

            if (result) {

                sendEmptyMessage(REQUEST_ADD_INFO);

            } else {

                Toast.makeText(getApplicationContext(), "DB 파일 다운로드가 실패 하였습니다." +
                        " 잠시후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }

        }

    }

}
