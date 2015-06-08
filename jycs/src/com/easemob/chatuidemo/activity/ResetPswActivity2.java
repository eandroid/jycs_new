package com.easemob.chatuidemo.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ab.http.AbHttpUtil;
import com.ab.util.AbToastUtil;
import com.alibaba.fastjson.TypeReference;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.domain.Result;
import com.easemob.chatuidemo.http.Url;
import com.easemob.chatuidemo.utils.jsonutils.JSONUtils;

@SuppressLint("HandlerLeak")
public class ResetPswActivity2 extends Activity implements OnClickListener {
    private EditText et_password;// 密码框
    private EditText et_password_config;// 再次输入密码框
    private ImageView img_submit;// 提交
    private ImageView img_back_title;// 退出
    private DemoApplication application;
    private AbHttpUtil abHttpUtil;
    private String result;// 请求回来结果
    private ProgressDialog pb ;
    private String mobile = null;//手机号
    private static final String TAG = "ResetPswActivity2";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (DemoApplication) getApplication();
        abHttpUtil = AbHttpUtil.getInstance(ResetPswActivity2.this);
        abHttpUtil.setTimeout(20000);
        if (!application.isNetworkConnected()) {
           AbToastUtil.showToast(application, R.string.network_not_connected);
        }
        Intent intent = getIntent();
        mobile = intent.getExtras().getString("mobile");
        setContentView(R.layout.activity_reset_psw2);
        initViews();
    }

    /**
     * 初始化视图
     */
    private void initViews() {
        et_password = (EditText) this.findViewById(R.id.et_password);
        img_back_title = (ImageView) this.findViewById(R.id.img_back_title);
        et_password_config = (EditText) this
                .findViewById(R.id.et_password_config);
        img_submit = (ImageView) this.findViewById(R.id.img_submit);
        img_submit.setOnClickListener(this);
        img_back_title.setOnClickListener(this);
        et_password_config.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int arg1, int arg2,
                    int arg3) {
                if (s.length() >= 6) {
                    img_submit.setImageResource(R.drawable.reset_submit_press);
                } else {
                    img_submit.setImageResource(R.drawable.reset_submit_noamal);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                    int arg2, int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {

            }
        });
        pb = new ProgressDialog(ResetPswActivity2.this);
        pb.setCanceledOnTouchOutside(false);
        pb.setCancelable(true);
        pb.setMessage("正在提交......");
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
        case R.id.img_back_title:
            onBackPressed();
            break;

        case R.id.img_submit:// 提交
            String phoneNumber = et_password.getText().toString().trim();
            String indentiCode = et_password_config.getText().toString().trim();
            if (TextUtils.isEmpty(phoneNumber) | phoneNumber.length() < 6
                    | indentiCode.length() > 20) {
                Toast.makeText(ResetPswActivity2.this, "请正确输入密码",
                        Toast.LENGTH_SHORT).show();
                et_password.requestFocus();
                return;
            } else if (TextUtils.isEmpty(indentiCode)) {
                Toast.makeText(ResetPswActivity2.this, "请再次输入密码",
                        Toast.LENGTH_SHORT).show();
                et_password_config.requestFocus();
                return;
            } else if (!phoneNumber.equals(indentiCode)) {
                AbToastUtil.showToast(ResetPswActivity2.this, "2次密码不一致");
                et_password_config.requestFocus();
                return;
            }
            submitResetPsw(indentiCode);
            break;

        default:
            break;
        }
    }

    /**
     * 提交到服务器修改密码
     */
    private void submitResetPsw(String password) {
        final String json = "{\"password\":\"" + password + "\"}";
        Log.i("ResetPswActivity2", json);
        pb.show();
        new Thread() {
            public void run() {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost post = new HttpPost(Url.URL_RESET_PASSWORD+mobile);
                Log.i("ResetActivity", Url.URL_RESET_PASSWORD+mobile);
                StringEntity postingString;
                try {
                    postingString = new StringEntity(json);
                    post.setEntity(postingString);
                    post.setHeader("Content-type", "application/json");
                    HttpResponse response = httpClient.execute(post);
                    Log.i(TAG, "code" + response.getStatusLine().getStatusCode());
                    String content = EntityUtils.toString(response.getEntity());
                     Log.i(TAG,content);
                    result = content;
                    handler.sendEmptyMessage(0);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }// json传递
                catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            };
        }.start();

    }

    Handler handler = new Handler() {
        @SuppressWarnings("unchecked")
        public void handleMessage(android.os.Message msg) {
            pb.cancel();
            Result<String> ret = (Result<String>) JSONUtils.parseObject(result,
                    new TypeReference<Result<String>>() {
                    });
            if (ret.getMsg().equals("error")) {
                AbToastUtil.showToast(ResetPswActivity2.this, "修改失败");
                return;
            }
            Intent intent = new Intent(ResetPswActivity2.this,
                    ResetPswActivity3.class);
            startActivity(intent);
           
        };
    };
}
