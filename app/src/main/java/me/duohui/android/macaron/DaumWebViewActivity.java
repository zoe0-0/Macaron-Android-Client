package me.duohui.android.macaron;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import me.duohui.android.macaron.Api.MacaronApi;

public class DaumWebViewActivity extends AppCompatActivity {

    private WebView daum_webView;
    private TextView daum_result;
    private Handler handler;
    private Button btn_register;
    private String guName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daum_web_view);


        daum_result = findViewById(R.id.daum_result);
        handler = new Handler();

        // WebView 초기화
        init_webView();

    }

    public void init_webView() {

        // WebView 설정
        daum_webView = findViewById(R.id.daum_webview);

        // JavaScript 허용
        daum_webView.getSettings().setJavaScriptEnabled(true);

        // JavaScript의 window.open 허용
        daum_webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        // JavaScript이벤트에 대응할 함수를 정의 한 클래스를 붙여줌
        daum_webView.addJavascriptInterface(new AndroidBridge(), "TestApp");

        // web client 를 chrome 으로 설정
        daum_webView.setWebChromeClient(new WebChromeClient());

        // webview url load.
        daum_webView.loadUrl(MacaronApi.ip+"daumApi");

    }

    private class AndroidBridge {
        @JavascriptInterface
        public void setAddress(final String arg1, final String arg2, final String arg3, final String arg4, final String arg5, final String arg6) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d("daum_result", "run: "+arg1+arg2+arg3+" "+arg4+" "+arg5+" "+arg6);
                    guName = arg4;
                    daum_result.setText(String.format("%s %s", arg2, arg3));
                    // WebView를 초기화 하지않으면 재사용할 수 없음
                    init_webView();
                }
            });

        }

    }

    public void onClickRegisterBtn(View view){

        String address = daum_result.getText().toString().trim();
        if(address== "" || address == null) return;

        Intent resultIntent = new Intent();
        resultIntent.putExtra("result",address);
        resultIntent.putExtra("guName",guName);
        setResult(RESULT_OK,resultIntent);
        finish();


    }

}
