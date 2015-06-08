/**  
* @company Finedo.cn
* @author WuFeng
* @date 2014-4-15 下午9:15:54
* @Title: FDWebView.java
* @Package cn.finedo.api.webview
* @Description: webview
* @version V1.0  
*/ 
package com.easemob.chatuidemo.widget.webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

@SuppressLint("SetJavaScriptEnabled")
public class FDWebView extends WebView {
	private Context context;
	
	public FDWebView(Context context) {
		super(context);
		this.context = context;
		init();
	}
	
	public FDWebView(Context context, AttributeSet attrs){
		super(context, attrs);
		this.context = context;
		init();
	}

	public FDWebView(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		this.context = context;
		init();
	}
	
	/**
	 * @author	WuFeng
	 * @date 2014-4-15 下午9:20:50
	 * @Description: 初始化webview参数
	 */
	private void init(){
		getSettings().setLoadWithOverviewMode(true);
		getSettings().setJavaScriptEnabled(true);  //设置页面支持Javascript  
		getSettings().setLoadWithOverviewMode(true);  
        getSettings().setSupportZoom(true);          //支持缩放  
        getSettings().setBuiltInZoomControls(true); //显示放大缩小   
        getSettings().setDefaultTextEncodingName("utf-8");    //字符集
        getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);//解决缓存问题
        getSettings().setSupportZoom(true);
        getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        setWebViewClient(new MyWebViewClient(context)); 
        setWebChromeClient(new MyWebChromeClient());
        setDownloadListener(new MyWebViewDownLoadListener(context));
	}
	
	@SuppressWarnings("rawtypes")
	public void setJsInterface(Class cls, String jsInterfaceName){
		try {
			addJavascriptInterface(cls.newInstance(), jsInterfaceName);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
