/**  
* @company Finedo.cn
* @author WuFeng
* @date 2014-4-15 下午9:47:19
* @Title: MyWebViewClient.java
* @Package cn.finedo.api.webview
* @Description: 解决webview中当开启新的页面的时候用webview来进行处理而不是用系统自带的浏览器处理 
* @version V1.0  
*/ 
package com.easemob.chatuidemo.widget.webview;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.webkit.HttpAuthHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ab.util.AbDialogUtil;
import com.easemob.chatuidemo.R;

@SuppressLint({ "HandlerLeak", "DefaultLocale" })
public class MyWebViewClient extends WebViewClient {
	@SuppressWarnings("unused")
	private AlertDialog dialog;
	private AlertDialog.Builder builder;
	
	private Context context;
	
	public MyWebViewClient(Context context){
		super();
		this.context = context;
	}
	/** 
     *  如果希望点击链接由自己处理，而不是新开Android的系统browser中响应该链接。 
     *  给WebView添加一个事件监听对象（WebViewClient)并重写其中的一些方法： 
     *  shouldOverrideUrlLoading：对网页中超链接按钮的响应。当按下某个连接时 
     *  WebViewClient会调用这个方法，并传递参数：按下的url。比如当webview内嵌 
     *  网页的某个数字被点击时，它会自动认为这是一个电话请求，会传递url：tel:123, 
     *  如果你不希望如此可通过重写shouldOverrideUrlLoading函数解决 
     */  
	@Override  
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // 当开启新的页面的时候用webview来进行处理而不是用系统自带的浏览器处理  
        // 互联网用：webView.loadUrl("http://www.google.com");   
        // 本地文件用：webView.loadUrl("file:///android_asset/XX.html");  本地文件存放
    	if(url.toUpperCase().indexOf("TEL:") < 0)
    		view.loadUrl(url);
        return true;
    }

    /**
     * 接收到Http请求的事件
     */
    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
    	super.onReceivedHttpAuthRequest(view, handler, host, realm);
    }
    
    /** 
    *载入页面完成的事件 
    **/  
    @Override  
    public void onPageFinished(WebView view, String url) {
        //获取网页内容  
    	
//		super.onPageFinished(view, url);
//		webViewLoadHandler.sendEmptyMessage(5);
    }
    
    /**
     * 载入页面开始的事件 
     * 这个事件就是开始载入页面调用的，通常我们可以在这设定一个loading的页面，告诉用户程序在等待网络响应。
     */
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
//    	super.onPageStarted(view, url, favicon);
//    	webViewLoadHandler.sendEmptyMessage(4);
    }
    

    Handler webViewLoadHandler = new Handler() {
		private AlertDialog dialogg;
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 4:
//				ShowWaitDialog();
//				dialogg = builder.create();
//				dialogg.show();
			    AbDialogUtil.showLoadPanel(context, R.drawable.ic_launcher, "正在加载中");
				break;
			case 5:
				AbDialogUtil.removeDialog(context);
				break;
			}
		}
	};
	
//    private void ShowWaitDialog() {
//    	View v = View.inflate(context, R.layout.webview_wait_view, null);
//		v.setVisibility(View.VISIBLE);
//		ImageView virus_iv = (ImageView) v.findViewById(R.id.virus_iv);
//		
//		RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 1.0f);
//		rotate.setDuration(3000);
//		rotate.setFillEnabled(true);
//		rotate.setFillAfter(true);
//		rotate.setRepeatCount(Animation.INFINITE);
//		virus_iv.setAnimation(rotate);
//		builder = new AlertDialog.Builder(context);
//		dialog = builder.create();
//		builder.setView(v);
//    }
}
