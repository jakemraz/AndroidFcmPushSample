package kr.jhb.androidfcmpushsample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import kr.jhb.androidfcmpushsample.utils.VolleyHelper;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseInstanceId.getInstance().getInstanceId()
            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "getInstanceId failed", task.getException());
                        return;
                    }

                    // Get new Instance ID token
                    String token = task.getResult().getToken();

                    // SharedPreference와 비교
                    SharedPreferences pref = getApplicationContext().
                        getSharedPreferences("fcm", Context.MODE_PRIVATE);
                    String prevToken = pref.getString("token", "n/a");

                    // 같을 경우 토큰값 출력하고 이대로 종료
                    if (prevToken.equals(token)) {
                        Log.d(TAG, "CurrentToken:" + token);
                        return;
                    }

                    // 같지 않을 경우

                    // 서버에 토큰 등록 // 등록 실패 시 리트라이
                    final String url = "";
                    JSONObject jobj = new JSONObject();
                    try {
                        jobj.put("ApplicationName", "HelloApp");
                        jobj.put("UserId", "jakemraz");
                        jobj.put("Token", token);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    VolleyHelper.getInstance(getApplicationContext()).execute(
                        Request.Method.POST,
                        url,
                        jobj,
                        null,
                        3
                    );

                    // 등록 성공시 SharedPreference에 업데이트
                    pref.edit().putString("token", token).commit();
                    // Log and toast

                    Log.d(TAG, "NewToken:" + token);
                    Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                }
            });
    }
}
