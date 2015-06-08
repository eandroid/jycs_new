package com.easemob.chatuidemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbToastUtil;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.http.Url;
import com.easemob.chatuidemo.widget.TimeButton;

public class ResetPswActivity extends BaseActivity implements OnClickListener {
    private ImageView back_img;// 返回按钮
    private EditText et_phonenumber;// 手机号输入框
    private EditText et_code_identify;// 验证码输入框
    private TimeButton img_send_identifying;// 验证码按钮
    private ImageView img_identify_next;// 提交
    private AbHttpUtil abHttpUtil;
    private DemoApplication application;
    private String code_verification;// 验证码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_psw);
        application = (DemoApplication) getApplication();
        abHttpUtil = AbHttpUtil.getInstance(ResetPswActivity.this);
        if (!application.isNetworkConnected()) {
            AbToastUtil.showToast(application, "暂时无法连接网络");
        }
        initViews(savedInstanceState);

    }

    @Override
    protected void onDestroy() {
        img_send_identifying.onDestroy();
        super.onDestroy();
    }

    /**
     * 初始化控件
     */
    private void initViews(Bundle t) {
        img_send_identifying = (TimeButton) this
                .findViewById(R.id.img_send_identifying);
        img_send_identifying.onCreate(t);
        img_send_identifying.setTextAfter("秒").setTextBefore("获取验证码")
                .setLenght(60 * 1000);
        img_send_identifying.setOnClickListener(this);
        img_send_identifying.setClickable(false);
        back_img = (ImageView) this.findViewById(R.id.back_img);
        et_phonenumber = (EditText) this.findViewById(R.id.et_phonenumber);
        et_code_identify = (EditText) this.findViewById(R.id.et_code_identify);
        img_identify_next = (ImageView) this
                .findViewById(R.id.img_identify_next);
        img_identify_next.setClickable(false);
        back_img.setOnClickListener(this);
        img_identify_next.setOnClickListener(this);
        et_phonenumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                    int arg3) {
                if (arg0.length() >= 11) {
                    et_code_identify.requestFocus();
                   
                    img_send_identifying.setClickable(true);
                } else {
                    img_send_identifying.setClickable(false);
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
        et_code_identify.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int arg1, int arg2,
                    int arg3) {
                if (s.length() >= 4) {
//                    et_code_identify.setFocusable(false);
//                    et_phonenumber.requestFocus();
                    img_identify_next.setClickable(true);
                    img_identify_next
                            .setImageResource(R.drawable.img_background_identyfy_press);
                } else {
                    img_identify_next.setClickable(false);
                    img_identify_next
                            .setImageResource(R.drawable.img_background_identyfy_normal);
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
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
        case R.id.img_send_identifying:
            sendVerificationCode();
            break;

        case R.id.back_img:
            onBackPressed();
            break;
        case R.id.img_identify_next:
            String code_verify = et_code_identify.getText().toString().trim();
            if (TextUtils.isEmpty(code_verify)
                    | !code_verify.equals(code_verification)) {
                AbToastUtil.showToast(ResetPswActivity.this, "验证码输入有误");
                et_code_identify.requestFocus();
                return;
            }
            Intent intent = new Intent(ResetPswActivity.this,
                    ResetPswActivity2.class);
            String mobile = et_phonenumber.getText().toString().trim();
            intent.putExtra("mobile", mobile);
            startActivity(intent);
            break;
        }

    }

    private void sendVerificationCode() {
        String phone = et_phonenumber.getText().toString().trim();
        // if (TextUtils.isEmpty(phone) | phone.length() < 11){
        // UIHelper.ToastMessage(this, "请输入正确的手机号");
        // et_phonenumber.requestFocus();
        // return ;
        // }
        int x = (int) (Math.random() * 1000);
        code_verification = String.format("%04d", x);
        Log.i("ResetPswActivity", code_verification);
        AbRequestParams params = new AbRequestParams();
        params.put("mobile", phone);
        params.put("message", code_verification
                + "是您本次身份校验码，30分钟内有效，教育超市工作人员绝不会向您索取此校验码，切勿告知他人。");
        abHttpUtil.get(Url.URL_CODE_VERYFICATION, params,
                new AbStringHttpResponseListener() {

                    @Override
                    public void onStart() {
                        AbDialogUtil.showLoadDialog(ResetPswActivity.this,
                                R.drawable.progress_circular, "正在获取验证码...");
                    }

                    @Override
                    public void onFinish() {
                        AbDialogUtil.removeDialog(ResetPswActivity.this);
                    }

                    @Override
                    public void onFailure(int statusCode, String content,
                            Throwable error) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onSuccess(int statusCode, String content) {
                        // TODO Auto-generated method stub

                    }
                });
    }
}
