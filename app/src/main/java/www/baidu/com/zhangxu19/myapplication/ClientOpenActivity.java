package www.baidu.com.zhangxu19.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

import www.baidu.com.zhangxu19.myapplication.databinding.OpenClientPopupViewBinding;

public class ClientOpenActivity extends AppCompatActivity implements IClientOpenPopupBtnClick {

    private static final String TAG = ClientOpenActivity.class.getSimpleName();

    private static final String SP_OPEN_CLIENT_MODE_FILE_NAME = "open_client_popup_mode_file_name";
    private static final String SP_OPEN_CLIENT_MODE = "sp_open_client_mode";

    private EditText mEtInput;
    private Button mBtnLoad;
    private WebView mWebView;

    private AlertDialog mDialog;

    private OpenClientPopupViewBinding mOpenClientBinding;

    // set and get
    //
    private String curOpenClientUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_open);

        mEtInput = (EditText) findViewById(R.id.et_input);
        mBtnLoad = (Button) findViewById(R.id.btn_load);
        mWebView = (WebView) findViewById(R.id.webview);

        mBtnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = mEtInput.getText().toString().trim();
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "http://" + url;
                }
                Log.d(TAG, "onClick: url: " + url);
                mWebView.setVisibility(View.VISIBLE);
                mWebView.loadUrl(url);
            }
        });

        initWebView();

        mOpenClientBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.open_client_popup_view,
                null, false);
        mOpenClientBinding.setHandlers(this);
    }

    private void initWebView() {
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "shouldOverrideUrlLoading, url: " + url);
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    curOpenClientUrl = url;
                    dealOpenClient();
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

        });
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
    }

    private void startActivityLocal() {
        try {
            // 以下固定写法
            final Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(curOpenClientUrl));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        } catch (Exception e) {
            // 防止没有安装的情况
            e.printStackTrace();
        }
    }

    private void dealOpenClient() {

        // getOpenClientModeInSharedPreference
        SharedPreferences sp = getSharedPreferences(SP_OPEN_CLIENT_MODE_FILE_NAME, MODE_PRIVATE);
        String openClientMode = sp.getString(SP_OPEN_CLIENT_MODE,
                OpenClientPopupMode.ONLY_THIS_TIME_PERMIT.name());

        // dealOpenClientByMode
        if (openClientMode.equals(OpenClientPopupMode.ALLWAYS_PERMIT.name())) {
            // if has app, to start
            startActivityLocal();
        } else if (openClientMode.equals(OpenClientPopupMode.NO_MORE_HINT.name())) {
            // nothing to do
        } else {
            // default ONLY_THIS_TIME_PERMIT,
            // open dialog
            openDialog();
        }


    }

    private void openDialog() {
        if (mDialog == null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setView(mOpenClientBinding.getRoot());
            mDialog = builder.create();
        }
        mDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void openOuterClientThisTime(View view) {
        setOpenClientModeInSP(OpenClientPopupMode.ONLY_THIS_TIME_PERMIT.name());
        startActivityLocal();
        mDialog.dismiss();
    }

    @Override
    public void openOuterClientAllways(View view) {
        setOpenClientModeInSP(OpenClientPopupMode.ALLWAYS_PERMIT.name());
        mDialog.dismiss();
        startActivityLocal();
    }

    @Override
    public void openOuterClientNoMoreHint(View view) {
        setOpenClientModeInSP(OpenClientPopupMode.NO_MORE_HINT.name());
        mDialog.dismiss();
    }

    @Override
    public void openOuterClientCancel(View view) {
        mDialog.dismiss();
    }

    private void setOpenClientModeInSP(String mode) {
        SharedPreferences sp = getSharedPreferences(SP_OPEN_CLIENT_MODE_FILE_NAME, MODE_PRIVATE);
        sp.edit().putString(SP_OPEN_CLIENT_MODE, mode).apply();
    }
}
