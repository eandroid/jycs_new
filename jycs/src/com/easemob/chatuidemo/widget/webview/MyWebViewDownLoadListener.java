/**  
* @company Finedo.cn
* @author WuFeng
* @date 2014-4-15 下午9:54:33
* @Title: MyWebViewDownLoadListener.java
* @Package cn.finedo.api.webview
* @Description: TODO(用一句话描述该文件做什么)
* @version V1.0  
*/ 
package com.easemob.chatuidemo.widget.webview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.DownloadListener;

public class MyWebViewDownLoadListener implements DownloadListener {
	private Context context;
	
	public MyWebViewDownLoadListener(Context context){
		super();
		this.context = context;
	}
	@Override
	public void onDownloadStart(String url, String userAgent,
			String contentDisposition, String mimetype, long contentLength) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		context.startActivity(i);
	}

}
