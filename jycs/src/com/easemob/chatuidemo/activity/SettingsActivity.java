package com.easemob.chatuidemo.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.util.AbSharedUtil;
import com.easemob.EMCallBack;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.R;

public class SettingsActivity extends Activity implements OnClickListener {
    private ImageView img_back_title;// 退回
    private RelativeLayout rl_02;// 帮助
    private RelativeLayout rl_03;// 版本更新
    private RelativeLayout rl_04;// 意见反馈
    private RelativeLayout rl_05;// 修改密码
    private RelativeLayout rl_06;// 登出
    private TextView tv_loginout;// 登出字体

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initViews();
    }

    private void initViews() {
        img_back_title = (ImageView) this.findViewById(R.id.img_back_title);
        rl_02 = (RelativeLayout) this.findViewById(R.id.rl_02);
        rl_03 = (RelativeLayout) this.findViewById(R.id.rl_03);
        rl_04 = (RelativeLayout) this.findViewById(R.id.rl_04);
        rl_05 = (RelativeLayout) this.findViewById(R.id.rl_05);
        rl_06 = (RelativeLayout) this.findViewById(R.id.rl_06);
        tv_loginout = (TextView) this.findViewById(R.id.tv_loginout);
        img_back_title.setOnClickListener(this);
        rl_02.setOnClickListener(this);
        rl_03.setOnClickListener(this);
        rl_04.setOnClickListener(this);
        rl_05.setOnClickListener(this);
        rl_06.setOnClickListener(this);
        if (!TextUtils.isEmpty(AbSharedUtil.getString(getApplicationContext(), "username"))) {
            tv_loginout.setText(getString(R.string.button_logout)
                    + "("
                    + AbSharedUtil.getString(getApplicationContext(),
                            "username") + ")");
        }
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
        case R.id.rl_02:// 帮助
            Toast.makeText(SettingsActivity.this, "帮助界面", Toast.LENGTH_SHORT)
                    .show();
            break;
        case R.id.rl_03:// 版本更新
            Toast.makeText(SettingsActivity.this, "版本更新", Toast.LENGTH_SHORT)
                    .show();
            break;
        case R.id.rl_04:// 意见反馈
            Toast.makeText(SettingsActivity.this, "意见反馈", Toast.LENGTH_SHORT)
                    .show();
            break;
        case R.id.rl_05:// 修改密码
            Intent intent = new Intent(SettingsActivity.this,
                    ResetPswActivity.class);
            startActivity(intent);
            break;
        case R.id.rl_06:// 登出
            logout();
            break;
        case R.id.img_back_title:// 返回
            onBackPressed();
            break;

        default:
            break;
        }
    }

    void logout() {
        final ProgressDialog pd = new ProgressDialog(SettingsActivity.this);
        String st = getResources().getString(R.string.Are_logged_out);
        pd.setMessage(st);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        DemoApplication.getInstance().logout(new EMCallBack() {

            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        pd.dismiss();
//                        in
                        // 重新显示登陆页面
                        startActivity(new Intent(SettingsActivity.this,
                                LoginActivity.class));
                        finish();

                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {

            }
        });
    }
}
