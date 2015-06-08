package com.easemob.chatuidemo.activity;

import java.io.File;
import java.util.List;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbFileUtil;
import com.ab.util.AbLogUtil;
import com.ab.util.AbSharedUtil;
import com.ab.util.AbStrUtil;
import com.ab.util.AbToastUtil;
import com.alibaba.fastjson.TypeReference;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.domain.InvidateUser;
import com.easemob.chatuidemo.domain.Result;
import com.easemob.chatuidemo.domain.UploadedFile;
import com.easemob.chatuidemo.domain.User2;
import com.easemob.chatuidemo.domain.dao.InvidateUserInsideDao;
import com.easemob.chatuidemo.http.Url;
import com.easemob.chatuidemo.utils.jsonutils.JSONUtils;
import com.easemob.chatuidemo.widget.TopView;

/**
 * 我
 * 
 * @author 让雨灭了火
 * 
 */
public class InvidateActivity extends AbActivity {
    private static final String TAG = "InvidateActivity";
    private TopView topView;
    private InvidateUserInsideDao userInsideDao;
    /* 用户信息 */
    private String url_avater = null;// 头像地址
    private String username = null;// 用户名
    private String mobile = null;// 手机号
    private String category = null;// 职位
    private ImageView img_avater;// 头像信息
    private TextView tv_avater_name;// 用户名
    private TextView tv_avater_invidator;// 手机号加职位
    private DemoApplication application;
    private AbHttpUtil abHttpUtil;// 请求网络工具类
    /* 用来标识请求照相功能的activity */
    private static final int CAMERA_WITH_DATA = 3023;
    /* 用来标识请求gallery的activity */
    private static final int PHOTO_PICKED_WITH_DATA = 3021;
    /* 用来标识请求裁剪图片后的activity */
    private static final int CAMERA_CROP_DATA = 3022;
    /* 拍照的照片存储位置 */
    private File PHOTO_DIR = null;
    private View mAvatarView = null;
 // 照相机拍照得到的图片
    private File mCurrentPhotoFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (DemoApplication) getApplication();
        setContentView(R.layout.activity_invidicatuil_center);
        abHttpUtil = AbHttpUtil.getInstance(application);
        abHttpUtil.setTimeout(20000);
        if (!application.isNetworkConnected()) {
            AbToastUtil.showToast(InvidateActivity.this,
                    R.string.network_not_connected);
        }
        getUserInfoFromDB();
        initViews();
        // 初始化图片保存路径
        String photo_dir = AbFileUtil.getImageDownloadDir(this);
        if (AbStrUtil.isEmpty(photo_dir)) {
            AbToastUtil.showToast(InvidateActivity.this, "存储卡不存在");
        } else {
            PHOTO_DIR = new File(photo_dir);
        }
    }

    private void initViews() {
        img_avater = (ImageView) this.findViewById(R.id.img_avater);
        application.getLoader().display(img_avater, url_avater);
        tv_avater_name = (TextView) this.findViewById(R.id.tv_avater_name);
        tv_avater_name.setText(AbSharedUtil.getString(application, "name"));
        tv_avater_invidator = (TextView) this
                .findViewById(R.id.tv_avater_invidator);
//        tv_avater_invidator.setText(mobile + "(" + category + ")");
        tv_avater_invidator.setText( AbSharedUtil.getString(application, "category") );
    }

    /**
     * 从数据库获取用户信息
     */
    private void getUserInfoFromDB() {
        userInsideDao = new InvidateUserInsideDao(InvidateActivity.this);
        userInsideDao.startReadableDatabase();
        List<InvidateUser> list_user = userInsideDao.queryList();
        if (list_user != null && list_user.size() > 0) {
            InvidateUser user2 = list_user.get(0);
            Log.i(TAG, "user2" + user2.toString());
            if (user2 != null) {
                username = user2.getName();
                mobile = user2.getMobile();
                category = user2.getScreenName();
                
                url_avater = Url.url_base_img_news + user2.get_url();
                Log.i(TAG, "头像地址：" + url_avater);

            }
        }
    }

    /**
     * 发布
     */
    public void publish(View v) {
        Intent intent = new Intent(InvidateActivity.this,CommitedSqareActivity.class);
        startActivity(intent);
    }

    /**
     * 收藏
     */
    public void collect(View v) {

    }

    /**
     * 更换头像
     * 
     * @param v
     */
    public void changeAvator(View v) {
        mAvatarView = mInflater.inflate(R.layout.choose_avatar, null);
        Button albumButton = (Button) mAvatarView
                .findViewById(R.id.choose_album);
        Button camButton = (Button) mAvatarView.findViewById(R.id.choose_cam);
        Button cancelButton = (Button) mAvatarView
                .findViewById(R.id.choose_cancel);
        albumButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AbDialogUtil.removeDialog(InvidateActivity.this);
                // 从相册中去获取
                try {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
                    intent.setType("image/*");
                    startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
                } catch (ActivityNotFoundException e) {
                    AbToastUtil.showToast(InvidateActivity.this, "没有找到照片");
                }
            }

        });

        camButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AbDialogUtil.removeDialog(InvidateActivity.this);
                 doPickPhotoAction();
            }

        });

        cancelButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AbDialogUtil.removeDialog(InvidateActivity.this);
            }

        });
        AbDialogUtil.showDialog(mAvatarView, Gravity.BOTTOM);
    }
    /**
     * 从照相机获取
     */
    private void doPickPhotoAction() {
        String status = Environment.getExternalStorageState();
        //判断是否有SD卡,如果有sd卡存入sd卡在说，没有sd卡直接转换为图片
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            doTakePhoto();
        } else {
            AbToastUtil.showToast(InvidateActivity.this,"没有可用的存储卡");
        }
    }
    /**
     * 拍照获取图片
     */
    protected void doTakePhoto() {
        try {
           String mFileName = System.currentTimeMillis() + ".jpg";
            mCurrentPhotoFile = new File(PHOTO_DIR, mFileName);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCurrentPhotoFile));
            startActivityForResult(intent, CAMERA_WITH_DATA);
        } catch (Exception e) {
            AbToastUtil.showToast(InvidateActivity.this,"未找到系统相机程序");
        }
    }
    /**
     * 描述：因为调用了Camera和Gally所以要判断他们各自的返回情况, 他们启动时是这样的startActivityForResult
     */
    protected void onActivityResult(int requestCode, int resultCode,
            Intent mIntent) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
        case PHOTO_PICKED_WITH_DATA:
            Uri uri = mIntent.getData();
            String currentFilePath = getPath(uri);
            if (!AbStrUtil.isEmpty(currentFilePath)) {
                Intent intent1 = new Intent(this, CropImageActivity.class);
                intent1.putExtra("PATH", currentFilePath);
                startActivityForResult(intent1, CAMERA_CROP_DATA);
            } else {
                AbToastUtil.showToast(InvidateActivity.this, "未在存储卡中找到这个文件");
            }
            break;
        case CAMERA_WITH_DATA:
             AbLogUtil.d(AddPhotoActivity.class, "将要进行裁剪的图片的路径是 = " +
             mCurrentPhotoFile.getPath());
             String currentFilePath2 = mCurrentPhotoFile.getPath();
             Intent intent2 = new Intent(this, CropImageActivity.class);
             intent2.putExtra("PATH",currentFilePath2);
             startActivityForResult(intent2,CAMERA_CROP_DATA);
            break;
        case CAMERA_CROP_DATA:
            String path = mIntent.getStringExtra("PATH");
            AbLogUtil.i(InvidateActivity.class, "裁剪后得到的图片的路径是 = " + path);
            File file = new File(path);
            uploadedFile(file);
            break;
        }
    }

    /**
     * 替换或提交头像
     * 
     * @param file
     */
    private void uploadedFile(File file) {
        AbRequestParams params = new AbRequestParams();
        params.put("file", file);
      String userId = AbSharedUtil.getString(application, "userId");
        abHttpUtil.post("http://vps1.taoware.com:8080/jc/users/"+userId+"/avatar", params,new AbStringHttpResponseListener() {
            
            @Override
            public void onStart() {
            }
            
            @Override
            public void onFinish() {
            }
            
            @Override
            public void onFailure(int statusCode, String content, Throwable error) {
               AbLogUtil.i(InvidateActivity.class, "statuscode" + statusCode + "content" + content);
            }
            
            @SuppressWarnings("unchecked")
            @Override
            public void onSuccess(int statusCode, String content) {
              AbLogUtil.i(InvidateActivity.class, content);  
                final Result<User2> ret = (Result<User2>) JSONUtils.parseObject(
                        content, new TypeReference<Result<User2>>() {
                        });
                InvidateUserInsideDao dao = new InvidateUserInsideDao(InvidateActivity.this);
                dao.startReadableDatabase();
                InvidateUser user = dao.queryList().get(0);
                user.set_url(ret.getResults().get(0).getAvatar().getThumbnailUrl());
                AbSharedUtil
                .putString(
                        getApplication(),
                        "url",
                        Url.url_base_img_news
                                + ret.getResults().get(0).getAvatar().getThumbnailUrl());
                dao.update(user);
                dao.closeDatabase();
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                     System.out.println(Thread.currentThread().getId());
                     runOnUiThread(new Runnable() {

                      @Override
                      public void run() {
                          application.getLoader().display(img_avater,  Url.url_base_img_news
                                  + ret.getResults().get(0).getAvatar().getThumbnailUrl());
                      }
                     });
                    }

                   }).start();
            }
        });
    }

    /**
     * 设置
     */
    public void setting(View v) {
        Intent intent = new Intent(InvidateActivity.this,
                SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        url_avater = AbSharedUtil.getString(application, "url");
        application.getLoader().display(img_avater, url_avater);
        topView = HomeActivity.getTopView();
        topView.setTitle("我");
        topView.hideRightBtn();
        topView.hideLefttBtn();
        topView.hideBackBtn();
    }

    /**
     * 从相册得到的url转换为SD卡中图片路径
     */
    public String getPath(Uri uri) {
        if (AbStrUtil.isEmpty(uri.getAuthority())) {
            return null;
        }
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        return path;
    }
}
