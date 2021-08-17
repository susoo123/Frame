//package com.example.frame;
//
//import android.util.Log;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.Response;
//import com.android.volley.toolbox.StringRequest;
//
//import java.util.HashMap;
//import java.util.Map;
//
////서버로 입력한 id,pw 값을 맵핑해 보낸다. 완료 성공하면 테이블에 내가 입력한 정보가 칼럼마다 입력됨.
//public class RegisterRequest extends StringRequest {
//
//    final static private String URL = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/register.php";
//    private Map<String,String> map;
//
//    public RegisterRequest(String email, String pw, Response.Listener<String> listener){
//        super(Method.POST, URL, listener, null);
//
//        map = new HashMap<>();
//        map.put("email", email);
//        map.put("pw",pw); //두번째꺼가 위의 파라미터
//
//
//    }
//
//    @Override
//    protected Map<String,String> getParams() throws AuthFailureError{
//        return map;
//    }
//
//}
