package com.easemob.chatuidemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.easemob.chatuidemo.R;

public class EditActivity extends BaseActivity{
    private static final String TAG ="EditActivity";
	private EditText editText;


	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_edit);
		
		editText = (EditText) findViewById(R.id.edittext);
		String title = getIntent().getStringExtra("title");
		String data = getIntent().getStringExtra("data");
		if(title != null)
			((TextView)findViewById(R.id.tv_title)).setText(title);
		if(data != null){
		    
//		    String groupname = data.replace("crowd_", "");
		    Log.i(TAG,"显示修改前的群名称:" + data);
		    editText.setText(data);
		}
		editText.setSelection(editText.length());
		
	}
	
	
	public void save(View view){
	    String name = editText.getText().toString();
//	    Log.i(TAG, "显示修改后的群名称"+name);
//	    String pre_name = NewGroupActivity.pre_group + name;
//	    Log.i(TAG,"显示加了前缀以后的群名称:"+pre_name);
		setResult(RESULT_OK,new Intent().putExtra("data", name));
		finish();
	}
}
