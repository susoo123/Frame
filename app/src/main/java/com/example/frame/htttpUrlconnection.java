package com.example.frame;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;




public class htttpUrlconnection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // HttpUrlConnection
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    String page = "url";
                    // URL 객체 생성
                    URL url = new URL(page);
                    // 연결 객체 생성
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    // Post 파라미터
                    String params = "param=1"
                            + "¶m2=2" + "sale";
                    // 결과값 저장 문자열
                    final StringBuilder sb = new StringBuilder();
                    // 연결되면
                    if (conn != null) {
                        Log.i("tag", "conn 연결");
                        // 응답 타임아웃 설정
                        conn.setRequestProperty("Accept", "application/json");
                        conn.setConnectTimeout(10000);
                        // POST 요청방식
                        conn.setRequestMethod("POST");
                        // 포스트 파라미터 전달
                        conn.getOutputStream().write(params.getBytes("utf-8"));
                        // url에 접속 성공하면 (200)
                        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            // 결과 값 읽어오는 부분
                            BufferedReader br = new BufferedReader(new InputStreamReader(
                                    conn.getInputStream(), "utf-8"
                            ));
                            String line;
                            while ((line = br.readLine()) != null) {
                                sb.append(line);
                            }
                            // 버퍼리더 종료
                            br.close();
                            Log.i("tag", "결과 문자열 :" + sb.toString());
                            // 응답 Json 타입일 경우
                            //JSONArray jsonResponse = new JSONArray(sb.toString());
                            //Log.i("tag", "확인 jsonArray : " + jsonResponse);

                        }
                        // 연결 끊기
                        conn.disconnect();
                    }

                    //백그라운드 스레드에서는 메인화면을 변경 할 수 없음
                    // runOnUiThread(메인 스레드영역)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "응답" + sb.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (Exception e) {
                    Log.i("tag", "error :" + e);

                }
            }
        });

        th.start();
    }


}
