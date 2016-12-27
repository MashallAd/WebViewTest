package www.baidu.com.zhangxu19.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

public class ClientOpenActivity extends AppCompatActivity {

    private static final String TAG = ClientOpenActivity.class.getSimpleName();

    private EditText mEtInput;
    private Button mBtnLoad;
    private WebView mWebView;

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
    }

    private void initWebView() {
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "shouldOverrideUrlLoading, url: " + url);
                if (!url.startsWith("http://") && !url.startsWith("https://")) {

                }
                return super.shouldOverrideUrlLoading(view, url);
            }

        });
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
