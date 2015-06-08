package com.easemob.chatuidemo.activity;

import com.ab.util.AbSharedUtil;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.widget.TopView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * 匿名用户登录
 * 
 * @author Administrator
 * 
 */
public class AnonymityActivity2 extends Activity {
    private TopView topView;
    private String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new BitmapDrawable());
        setContentView(R.layout.login1);
        id = AbSharedUtil.getString(getApplicationContext(), "userId");
        ImageView img_pic = (ImageView) this.findViewById(R.id.img_pic);
        if (!id.equals("866")) {
            img_pic.setImageResource(R.drawable.ungetcategory);
        } else {
            img_pic.setImageResource(R.drawable.unreach_get); 
        }
        WindowManager m = getWindowManager();  
        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高  
          
        android.view.WindowManager.LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值  
        p.height = (int) (d.getHeight() * 0.4);   //高度设置为屏幕的1.0 
        p.width = (int) (d.getWidth() * 0.4);    //宽度设置为屏幕的0.8 
//        p.height = (int) 100;   //高度设置为屏幕的1.0 
//        p.width = (int) 100;    //宽度设置为屏幕的0.8 
        
        p.alpha = 1.0f;                //设置本身透明度
        p.dimAmount = 0.5f;                //设置黑暗度
        getWindow().setAttributes(p);     //设置生效
//        thisActivity = this;
    }
    private  void login (){
        Intent intent = new Intent(AnonymityActivity2.this,LoginActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        topView = HomeActivity.getTopView();
        topView.setTitle("我");
        topView.setRightBtnDrawable(R.drawable.gologin);
        topView.setRightBtnListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                login();
            }
        });
        if (id.equals("866")){
            topView.showRightBtn();
        } else {
            topView.hideRightBtn();
        }
        topView.hideLefttBtn();
        topView.hideBackBtn();
        
    }
}
