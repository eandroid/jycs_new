package com.easemob.chatuidemo.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
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
import com.ab.view.progress.AbHorizontalProgressBar;
import com.ab.view.titlebar.AbTitleBar;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.adapter.ImageShowAdapter;

public class AddPhotoActivity extends AbActivity {
    private static final String TAG = "AddPhotoActivity";
    private DemoApplication application;
    private GridView mGridView = null;
    private ImageShowAdapter mImagePathAdapter = null;
    private ArrayList<String> mPhotoList = null;
    private int selectIndex = 0;
    private int camIndex = 0;
    private View mAvatarView = null;
    private TextView tv_square;
    /* 用来标识请求照相功能的activity */
    private static final int CAMERA_WITH_DATA = 3023;
    /* 用来标识请求gallery的activity */
    private static final int PHOTO_PICKED_WITH_DATA = 3021;
    /* 用来标识请求裁剪图片后的activity */
    private static final int CAMERA_CROP_DATA = 3022;
    /* 用来标识请求所在单位的activity */
    private static final int CODE_UNIT = 1111;
    /* 用来标识请求广场类型的activity */
    private static final int CODE_CATEGORY = 1112;

    /* 拍照的照片存储位置 */
    private File PHOTO_DIR = null;
    // 照相机拍照得到的图片
    private File mCurrentPhotoFile;
    private String mFileName;

    /* ProgressBar进度控制 */
    private AbHorizontalProgressBar mAbProgressBar;
    /* 最大100 */
    private int max = 100;
    private int progress = 0;
    private DialogFragment mAlertDialog = null;
    private AbHttpUtil mAbHttpUtil = null;
    private TextView numberText, maxText;
    private TextView tv_unit;
    private EditText edit_information;// 发送的消息
    private String unit_id;
    private String category = "";
    private Button addBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.add_photo);
        application = (DemoApplication) abApplication;
        AbTitleBar mAbTitleBar = this.getTitleBar();
        mAbTitleBar.setTitleText("发送广场");
        mAbTitleBar.setLogo(R.drawable.back_title);
        mAbTitleBar.setTitleBarBackground(R.drawable.background_title);
        mAbTitleBar.setTitleTextMargin(30, 0, 0, 0);

        mPhotoList = new ArrayList<String>();
        tv_square = (TextView) this.findViewById(R.id.tv_square);

        // 获取Http工具类
        mAbHttpUtil = AbHttpUtil.getInstance(this);

        // 默认
        mPhotoList.add(String.valueOf(R.drawable.cam_photo));

        mGridView = (GridView) findViewById(R.id.myGrid);
        mImagePathAdapter = new ImageShowAdapter(this, mPhotoList, Dp2Px(
                AddPhotoActivity.this, 116), Dp2Px(AddPhotoActivity.this, 116));
        mGridView.setAdapter(mImagePathAdapter);

        // 初始化图片保存路径
        String photo_dir = AbFileUtil.getImageDownloadDir(this);
        if (AbStrUtil.isEmpty(photo_dir)) {
            AbToastUtil.showToast(AddPhotoActivity.this, "存储卡不存在");
        } else {
            PHOTO_DIR = new File(photo_dir);
        }

        addBtn = (Button) findViewById(R.id.addBtn);
        edit_information = (EditText) this.findViewById(R.id.edit_information);
        mGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                selectIndex = position;
                if (selectIndex == camIndex) {
                    mAvatarView = mInflater.inflate(R.layout.choose_avatar,
                            null);
                    Button albumButton = (Button) mAvatarView
                            .findViewById(R.id.choose_album);
                    Button camButton = (Button) mAvatarView
                            .findViewById(R.id.choose_cam);
                    Button cancelButton = (Button) mAvatarView
                            .findViewById(R.id.choose_cancel);
                    albumButton.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            AbDialogUtil.removeDialog(AddPhotoActivity.this);
                            // 从相册中去获取
                            try {
                                Intent intent = new Intent(
                                        Intent.ACTION_GET_CONTENT, null);
                                intent.setType("image/*");
                                startActivityForResult(intent,
                                        PHOTO_PICKED_WITH_DATA);
                            } catch (ActivityNotFoundException e) {
                                AbToastUtil.showToast(AddPhotoActivity.this,
                                        "没有找到照片");
                            }
                        }

                    });

                    camButton.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            AbDialogUtil.removeDialog(AddPhotoActivity.this);
                            doPickPhotoAction();
                        }

                    });

                    cancelButton.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            AbDialogUtil.removeDialog(AddPhotoActivity.this);
                        }

                    });
                    AbDialogUtil.showDialog(mAvatarView, Gravity.BOTTOM);
                } else {
                    for (int i = 0; i < mImagePathAdapter.getCount(); i++) {
                        ImageShowAdapter.ViewHolder mViewHolder = (ImageShowAdapter.ViewHolder) mGridView
                                .getChildAt(i).getTag();
                        if (mViewHolder != null) {
                            mViewHolder.mImageView2.setBackgroundDrawable(null);
                        }
                    }

                    ImageShowAdapter.ViewHolder mViewHolder = (ImageShowAdapter.ViewHolder) view
                            .getTag();
                    mViewHolder.mImageView2
                            .setBackgroundResource(R.drawable.photo_select);
                }
            }

        });

        addBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                addBtn.setClickable(false);
                uploadFile(mPhotoList);
            }
        });
        tv_unit = (TextView) this.findViewById(R.id.tv_unit);
    }

    /**
     * 从照相机获取
     */
    private void doPickPhotoAction() {
        String status = Environment.getExternalStorageState();
        // 判断是否有SD卡,如果有sd卡存入sd卡在说，没有sd卡直接转换为图片
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            doTakePhoto();
        } else {
            AbToastUtil.showToast(AddPhotoActivity.this, "没有可用的存储卡");
        }
    }

    /**
     * 拍照获取图片
     */
    protected void doTakePhoto() {
        try {
            mFileName = System.currentTimeMillis() + ".jpg";
            mCurrentPhotoFile = new File(PHOTO_DIR, mFileName);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(mCurrentPhotoFile));
            startActivityForResult(intent, CAMERA_WITH_DATA);
        } catch (Exception e) {
            AbToastUtil.showToast(AddPhotoActivity.this, "未找到系统相机程序");
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
                AbToastUtil.showToast(AddPhotoActivity.this, "未在存储卡中找到这个文件");
            }
            break;
        case CAMERA_WITH_DATA:
            AbLogUtil.d(AddPhotoActivity.class, "将要进行裁剪的图片的路径是 = "
                    + mCurrentPhotoFile.getPath());
            String currentFilePath2 = mCurrentPhotoFile.getPath();
            Intent intent2 = new Intent(this, CropImageActivity.class);
            intent2.putExtra("PATH", currentFilePath2);
            startActivityForResult(intent2, CAMERA_CROP_DATA);
            break;
        case CAMERA_CROP_DATA:
            String path = mIntent.getStringExtra("PATH");
            AbLogUtil.d(AddPhotoActivity.class, "裁剪后得到的图片的路径是 = " + path);
            mImagePathAdapter.addItem(mImagePathAdapter.getCount() - 1, path);
            camIndex++;
            break;
        case CODE_UNIT:
            unit_id = mIntent.getStringExtra("id");
            String unit_name = mIntent.getStringExtra("unit");
            tv_unit.setText(unit_name);
            break;
        case CODE_CATEGORY:
            category = mIntent.getStringExtra("category");
            tv_square.setText(category);
            break;
        }
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

    public void uploadFile(List<String> list) {
        String userID = AbSharedUtil.getString(application, "userId");
        /************暂时改动**********/
        unit_id= "23";
        /************暂时改动**********/
        String url = "http://vps1.taoware.com:8080/jc/square/user/" + userID
                + "/unit/" + unit_id;
        Log.i(TAG, "发广场请求url" + url);
        AbRequestParams params = new AbRequestParams();
        String information = edit_information.getText().toString().trim()+"";
//        if (TextUtils.isEmpty(information)) {
//            AbToastUtil.showToast(application, "广场信息不能为空");
//            edit_information.requestFocus();
//            return;
//        }
        params.put("information", information);
        params.put("type", category);
        Log.i(TAG, "发送的广场信息为" + information + "广场类型为" + category);
        if (list.size() != 1) {
            try {
                // 多文件上传添加多个即可

                // 文件参数，去掉后边那个按钮
                Log.i(TAG, "list的长度为" + list.size());
                for (int i = 0; i < list.size() - 1; i++) {

                    String path = list.get(i);
                    File file = new File(path);
                    int n = i + 1;
                    params.put("file" + n, file);
                }
                mAbHttpUtil.post(url, params,
                        new AbStringHttpResponseListener() {

                            @Override
                            public void onSuccess(int statusCode, String content) {
                                AbToastUtil.showToast(AddPhotoActivity.this,
                                        "上传成功");
                                finish();
                            }

                            // 开始执行前
                            @Override
                            public void onStart() {
                            }

                            @Override
                            public void onFailure(int statusCode,
                                    String content, Throwable error) {
                                AbToastUtil.showToast(AddPhotoActivity.this,
                                        error.getMessage() + statusCode
                                                + content);
                                addBtn.setClickable(true);
                            }

                            // 进度
                            @Override
                            public void onProgress(long bytesWritten,
                                    long totalSize) {
                                maxText.setText(bytesWritten
                                        / (totalSize / max) + "/" + max);
                                mAbProgressBar
                                        .setProgress((int) (bytesWritten / (totalSize / max)));
                            }

                            // 完成后调用，失败，成功，都要调用
                            public void onFinish() {
                            };

                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // File file = null;
            // params.put("file1",file);
            Log.i(TAG, "url = "+"http://vps1.taoware.com:8080/jc/square/ad/user/"
                    + userID + "/unit/" + unit_id + "/test?information="
                    + information + "&type=" + category);
            mAbHttpUtil.post("http://vps1.taoware.com:8080/jc/square/ad/user/"
                    + userID + "/unit/" + unit_id + "/test?information="
                    + information + "&type=" + category,
                    new AbStringHttpResponseListener() {

                        @Override
                        public void onStart() {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onFinish() {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onFailure(int statusCode, String content,
                                Throwable error) {
                            Log.i(TAG, "content" + content + "statuscode"
                                    + statusCode);
                            AbToastUtil.showToast(AddPhotoActivity.this, "发广场失败");
                            addBtn.setClickable(true);
                        }

                        @Override
                        public void onSuccess(int statusCode, String content) {
                            AbToastUtil
                                    .showToast(AddPhotoActivity.this, "上传成功");
                            finish();
                        }
                    });
        }

    }

    /**
     * 所在单位选择
     */
    public void unit(View v) {
        Intent intent = new Intent(AddPhotoActivity.this, UnitActivity.class);
        startActivityForResult(intent, CODE_UNIT);
    }

    /**
     * 广场类型
     */
    public void category(View v) {
        Intent intent = new Intent(AddPhotoActivity.this,
                SquareCategoryActivity.class);
        startActivityForResult(intent, CODE_CATEGORY);
    }

    public int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
