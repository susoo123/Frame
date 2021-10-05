package com.example.frame;


import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class AddressApiActivity extends AppCompatActivity {
//
//    private WebView webView;
//
//    class MyJavaScriptInterface
//    {
//        @JavascriptInterface
//        @SuppressWarnings("unused")
//        public void processDATA(String data) {
//            Bundle extra = new Bundle();
//            Intent intent = new Intent();
//            extra.putString("data", data);
//            intent.putExtras(extra);
//            setResult(RESULT_OK, intent);
//            finish();
//        }
//    }
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.address_api);
//
//        webView = (WebView) findViewById(R.id.webView);
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setDomStorageEnabled(true);
//
//        webView.addJavascriptInterface(new MyJavaScriptInterface(), "Android");
//
//
//        webView.setWebViewClient(new WebViewClient() {
//
//            @Override
//            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                handler.proceed();
//            }
//
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                webView.loadUrl("javascript:sample2_execDaumPostcode();");
//            }
//        });
//
//        webView.loadUrl("http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/daum.html");
//
//
//    }


    private static String IP_ADDRESS = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/daum.php";
    class MyJavaScriptInterface {
        @JavascriptInterface @SuppressWarnings("unused")
        public void processDATA(String data) { Bundle extra = new Bundle();
        Intent intent = new Intent(); extra.putString("data", data);
        intent.putExtras(extra); setResult(RESULT_OK, intent); finish(); }
    }

    public WebView wv_search_address;
    private ProgressBar progress;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); setContentView(R.layout.address_api);
        progress = findViewById(R.id.web_progress);
        wv_search_address = findViewById(R.id.webView);
        wv_search_address.getSettings().setJavaScriptEnabled(true);
        wv_search_address.getSettings().setDomStorageEnabled(true);
        wv_search_address.addJavascriptInterface(new MyJavaScriptInterface(), "Android");
        wv_search_address.setWebViewClient(new WebViewClient() {
            @Override public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed(); // SSL 에러가 발생해도 계속 진행
                 }
                @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url); return true; } // 페이지 로딩 시작시 호출

             @Override public void onPageStarted(WebView view,String url , Bitmap favicon){
                progress.setVisibility(View.VISIBLE); }

                @Override public void onPageFinished(WebView view, String url) {
                progress.setVisibility(View.GONE);
                wv_search_address.loadUrl("javascript:sample2_execDaumPostcode();"); } });
        //ssl 인증이 없는 경우 해결을 위한 부분
         wv_search_address.setWebChromeClient(new WebChromeClient() {
             @TargetApi(Build.VERSION_CODES.LOLLIPOP)
             @Override public void onPermissionRequest(final PermissionRequest request) {
                 request.grant(request.getResources()); } });
         wv_search_address.loadUrl(IP_ADDRESS ); }


}





