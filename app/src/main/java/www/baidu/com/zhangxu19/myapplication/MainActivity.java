package www.baidu.com.zhangxu19.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.net.URI;

import www.baidu.com.zhangxu19.myapplication.databinding.ActivityMainBinding;

import static android.R.attr.description;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding mainBinding;
    private WebView mWebView;
    private ProgressBar mProgressBar;

    private Activity mActivity;

    private AlertDialog.Builder builder;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mainBinding.setHandlers(this);

        mWebView = mainBinding.webview;

        mProgressBar = mainBinding.progress;

        mActivity = this;

        builder = new AlertDialog.Builder(this);
        initWebView();
//        initWebView2();

    }

    @SuppressLint("JavascriptInterface")
    private void initWebView2() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);

        mWebView.setWebChromeClient(new MyWebChromeClient());

        mWebView.addJavascriptInterface(new DemoJavaScriptInterface(), "demo");

        mWebView.loadUrl("file:///android_asset/demo.html");
    }

    @SuppressLint("JavascriptInterface")
    private void initWebView() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        mWebView.addJavascriptInterface(new DemoJavaScriptInterface(), "demo");
        mWebView.setWebViewClient(new WebViewClient() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Log.d(TAG, "shouldOverrideUrlLoading: " + request.getUrl().getPath().toString());
                view.loadUrl(request.getUrl().toString());
                return false;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Toast.makeText(mActivity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d(TAG, "onPageStarted: url: " + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d(TAG, "onPageFinished: url: " + url);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
//                Log.d(TAG, "onLoadResource: url: " + url);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mProgressBar.setProgress(newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                Log.d(TAG, "onReceivedTitle: title: " + title);
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                Log.d(TAG, "onReceivedIcon: icon");
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                Log.d(TAG, "onShowCustomView: ");
            }

            @Override
            public void onHideCustomView() {
                Log.d(TAG, "onHideCustomView: ");
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                Log.d(TAG, "onJsAlert: url: " + url + ", message: " + message + ", JsResult: " + result);

                AlertDialog.Builder b2 = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("提示").setMessage(message)
                        .setPositiveButton("ok",
                                new AlertDialog.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // TODO Auto-generated method stub
                                        result.confirm();
                                    }
                                });

                b2.setCancelable(false);
                b2.create();
                b2.show();
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                Log.d(TAG, "onJsConfirm: url: " + url + ", message: " + message + ", JsResult: " + result);
                builder.setTitle("网络内容")
                        .setMessage(message)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.d(TAG, "onClick: confirm");
                                result.confirm();
                                dialog.dismiss();
                                dialog = null;
                            }
                        }).setCancelable(true).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        result.cancel();
                    }
                });
                dialog = builder.create();
                dialog.show();
                return true;
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                Log.d(TAG, "onJsPrompt: url: " + url + ", message: " + message + "; defaultValue: "
                        + defaultValue + "; JsPromptResult: " + result);


                return super.onJsPrompt(view, url, message, defaultValue, result);
            }

            @Override
            public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
                Log.d(TAG, "onJsBeforeUnload: url: " + url + ", message: " + message);
                return super.onJsBeforeUnload(view, url, message, result);
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                Log.d(TAG, "onShowFileChooser: ");
                return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
            }
        });

        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
//        mWebView.setInitialScale(1);
    }

    private void loadData() {
//        final String url = "http://www.google.cn/";
        String url = URI.create(mainBinding.editUrl.getText().toString()).toString();
//        url = "http://172.18.16.38:8000/prompt.html";
        url = "http://www.baidu.com";
        Log.d(TAG, "loadData: url: " + url);
        mWebView.loadUrl(url);

//        String summary = "file:///android_asset/test.html";
//        mWebView.loadUrl(summary);
    }

    public void loadUrl(View view) {
        loadData();
    }

    public void goBack(View view) {
        mWebView.goBack();
    }

    public void goForward(View view) {
        mWebView.goForward();
    }

    public void showHistory(View view) {
        WebBackForwardList webBackForwardList = mWebView.copyBackForwardList();
        Toast.makeText(this, "webBackForwardList, size: " + webBackForwardList.getSize(), Toast.LENGTH_SHORT).show();
        Log.d(TAG, "showHistory, webBackForwardList: url: " + webBackForwardList.getCurrentItem().getUrl()
                + ", title: " + webBackForwardList.getCurrentItem().getTitle());
    }

    public void openZoomPicker(View view) {
        mWebView.invokeZoomPicker();
    }

    public void zoomIn(View view) {
        mWebView.zoomIn();
    }

    public void zoomOut(View view) {
        mWebView.zoomOut();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void findAllAsync(View view) {
        mWebView.findAllAsync(mainBinding.editFind.getText().toString());
    }

    public void findNext(View view) {
        mWebView.findNext(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private Handler mHandler = new Handler();
    private int count = 0;

    final class DemoJavaScriptInterface {

        DemoJavaScriptInterface() {
        }

        /**
         * This is not called on the UI thread. Post a runnable to invoke
         * loadUrl on the UI thread.
         */
        @JavascriptInterface
        public void clickOnAndroid() {
            mHandler.post(new Runnable() {
                public void run() {
                    mWebView.loadUrl("javascript:wave()");
                }
            });

        }

        @JavascriptInterface
        public void changeText() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl("javascript:changeTextCallBack('" + count++
                            + "')");
                }
            });
        }
    }

    final class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            Log.d(TAG, message);
            result.confirm();
            return true;
        }
    }

}
