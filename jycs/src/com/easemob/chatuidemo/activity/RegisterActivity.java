/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.easemob.chatuidemo.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbToastUtil;
import com.alibaba.fastjson.TypeReference;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.domain.Result;
import com.easemob.chatuidemo.domain.User2;
import com.easemob.chatuidemo.http.Url;
import com.easemob.chatuidemo.utils.jsonutils.JSONUtils;
import com.easemob.chatuidemo.widget.TimeButton;

/**
 * 注册页
 * 
 */
@SuppressLint({ "HandlerLeak", "DefaultLocale" })
public class RegisterActivity extends BaseActivity implements OnClickListener,
        OnItemSelectedListener {
    private static final String TAG = "RegisterActivity";
    private EditText et_regisname;// 注册的用户名
    private EditText et_register_psw;// 注册的密码
    private EditText et_confirmPwdEditText;// 再次重复密码
    private Button img_back_title;// 注册界面返回按钮
    private Spinner et_regiswork;// 输入具体的职务
    private EditText et_phonenum;// 输入用户常用手机号
    private EditText et_backs;// 特殊需求描述
    private ImageView btn_submit;// 注册提交按钮
    private EditText detail_address;// 详细地址
    private Spinner select_sex;// 性别选择
    private Spinner select_category;// 职务选择
    private Spinner select_gove;// 省份选择
    private Spinner select_city;// 市选择
    private String[] mGoves;
    private AbHttpUtil abHttpUtil;// 请求网络类
    String code_verification = "";// 验证码
    private DemoApplication appContext;
    public static String result;
    ProgressDialog pd;
    private TimeButton timebtn_verification;// 验证码
    private EditText et_identifying_code;// 验证码输入框
    private Map<String, String> key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        appContext = (DemoApplication) getApplication();
        abHttpUtil = AbHttpUtil.getInstance(RegisterActivity.this);
        if (!appContext.isNetworkConnected()) {
            AbToastUtil.showToast(RegisterActivity.this,
                    R.string.network_not_connected);
        }

        initViews(savedInstanceState);
        initSex();
        initCategory();
        initBusiness();
        initAddress();
        initEvents();
    }

    /**
     * 初始化省份
     */
    private void initAddress() {
        mGoves = new String[] { "安徽", "北京", "福建", "甘肃", "广东", "广西", "贵州", "海南",
                "河北", "河南", "黑龙江", "湖北", "湖南", "吉林", "江苏", "江西", "辽宁", "内蒙古",
                "宁夏", "青海", "山东", "山西", "陕西", "上海", "四川", "天津", "西藏", "新疆",
                "云南", "浙江", "重庆" };
        ArrayAdapter<String> govAdapter = new ArrayAdapter<String>(
                RegisterActivity.this, android.R.layout.simple_spinner_item,
                mGoves);
        govAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_item);
        select_gove.setAdapter(govAdapter);
        select_gove.setOnItemSelectedListener(this);
    }

    /**
     * 初始化职务
     */
    private void initBusiness() {
        String[] array_business = new String[] { "角色", "管理员", "联采", "员工",
                "供应商", "游客" };
        ArrayAdapter<String> businessAdapter = new ArrayAdapter<String>(
                RegisterActivity.this, android.R.layout.simple_spinner_item,
                array_business);
        businessAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_item);
        select_category.setAdapter(businessAdapter);
    }

    /**
     * 初始化性别选择spinner
     */
    private void initSex() {
        String[] array_sex = new String[] { "性别", "男", "女" };
        ArrayAdapter<String> sexAdapter = new ArrayAdapter<String>(
                RegisterActivity.this, android.R.layout.simple_spinner_item,
                array_sex);
        sexAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_item);
        select_sex.setAdapter(sexAdapter);
    }

    /**
     * 初始化职务选择spinner
     */
    private void initCategory() {
        String[] array_sex = new String[] { "职务", "副校长", "董事长", "校长助理", "主任",
                "秘书长", "副主任", "副秘书长", "处长", "总经理", "副处长", "副总经理", "科长", "部门经理",
                "员工" };
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(
                RegisterActivity.this, android.R.layout.simple_spinner_item,
                array_sex);
        categoryAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_item);
        et_regiswork.setAdapter(categoryAdapter);
    }

    /**
     * 初始化相应事件
     */
    private void initEvents() {
        btn_submit.setOnClickListener(this);
        img_back_title.setOnClickListener(this);
        // select_sex.setOnItemSelectedListener(this);
        et_phonenum.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
                if (s.length() >= 11) {
                    et_identifying_code.requestFocus();
                    timebtn_verification.setClickable(true);
                } else {
                    timebtn_verification.setClickable(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_identifying_code.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int arg1, int arg2,
                    int arg3) {
                if (s.length() >= 4) {
                    // et_code_identify.setFocusable(false);
                    // et_phonenum.requestFocus();
                    timebtn_verification.setClickable(true);

                } else {
                    timebtn_verification.setClickable(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                    int arg2, int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                key = new HashMap<String, String>();
                String code_virifi = et_identifying_code.getText().toString()
                        .trim();
                String phonenum = et_phonenum.getText().toString().trim();
                key.put(code_virifi, phonenum);
            }
        });
    }

    @Override
    protected void onDestroy() {
        timebtn_verification.onDestroy();
        super.onDestroy();
    }

    /**
     * 初始化控件
     */
    private void initViews(Bundle savedInstanceState) {
        et_identifying_code = (EditText) this
                .findViewById(R.id.et_identifying_code);
        timebtn_verification = (TimeButton) this
                .findViewById(R.id.timebtn_verification);
        timebtn_verification.onCreate(savedInstanceState);
        timebtn_verification.setTextAfter("秒").setTextBefore("验证码")
                .setLenght(60 * 1000);
        timebtn_verification.setOnClickListener(this);
        timebtn_verification.setClickable(false);
        select_city = (Spinner) this.findViewById(R.id.select_city);
        select_gove = (Spinner) this.findViewById(R.id.select_gove);
        select_sex = (Spinner) this.findViewById(R.id.select_sex);
        select_category = (Spinner) this.findViewById(R.id.select_category);
        detail_address = (EditText) findViewById(R.id.detail_address);
        et_regisname = (EditText) findViewById(R.id.et_regisname);// 用户名
        et_register_psw = (EditText) findViewById(R.id.et_register_psw);
        et_confirmPwdEditText = (EditText) findViewById(R.id.et_confirmPwdEditText);

        img_back_title = (Button) this.findViewById(R.id.img_back_title);
        et_regiswork = (Spinner) this.findViewById(R.id.et_regiswork);
        et_phonenum = (EditText) this.findViewById(R.id.et_phonenum);
        et_backs = (EditText) this.findViewById(R.id.et_backs);
        btn_submit = (ImageView) this.findViewById(R.id.btn_submit2);
        pd = new ProgressDialog(RegisterActivity.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(true);
        pd.setMessage("正在注册......");
    }

    /**
     * 注册
     * 
     * @param view
     * @throws IOException
     * @throws ClientProtocolException
     */

    public void register(View view) throws ClientProtocolException, IOException {
        String code_virifi = et_identifying_code.getText().toString().trim();// 输入的验证码
        String username = et_regisname.getText().toString().trim();// 用户名
        String gender = select_sex.getSelectedItem().toString();// 性别
        String category = et_regiswork.getSelectedItem().toString();// 具体职务
        String position = select_category.getSelectedItem().toString();// 职位
        String govement = select_gove.getSelectedItem().toString();// 省份
        String city = select_city.getSelectedItem().toString();// 地级市
        String location = govement + city;
        String address = detail_address.getText().toString().trim();// 详细地址
        String phonenum = et_phonenum.getText().toString().trim();// 电话号码
        String password = et_register_psw.getText().toString().trim();// 密码
        String configpsw = et_confirmPwdEditText.getText().toString().trim();// 确认密码
        String backs = et_backs.getText().toString().trim();// 备注
        boolean istrue = key.get(code_virifi).equals(phonenum);
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            et_regisname.requestFocus();
            return;
        } else if (gender.equals("性别")) {
            Toast.makeText(this, "请选择性别", Toast.LENGTH_SHORT).show();

            select_sex.requestFocus();
            return;
        } else if ("职务".equals(category)) {
            Toast.makeText(this, "具体职务不能为空", Toast.LENGTH_SHORT).show();
            et_regiswork.requestFocus();
            return;
        } else if ("角色".equals(position)) {
            Toast.makeText(RegisterActivity.this, "请选择角色", Toast.LENGTH_SHORT)
                    .show();
            select_category.requestFocus();
            return;
        } else if (govement.equals("省份")) {
            Toast.makeText(RegisterActivity.this, "请选择省份", Toast.LENGTH_SHORT)
                    .show();
            select_gove.requestFocus();
            return;
        } else if (city.equals("地级市")) {
            Toast.makeText(RegisterActivity.this, "请选择城市", Toast.LENGTH_SHORT)
                    .show();
            select_city.requestFocus();
            return;
        } else if (TextUtils.isEmpty(address)) {
            Toast.makeText(RegisterActivity.this, "具体地址不能为空",
                    Toast.LENGTH_SHORT).show();
            detail_address.requestFocus();
            return;
        } else if (TextUtils.isEmpty(phonenum) | phonenum.length() < 11) {
            Toast.makeText(RegisterActivity.this, "请正确输入手机号",
                    Toast.LENGTH_SHORT).show();
            et_phonenum.requestFocus();
            return;
        } else if (TextUtils.isEmpty(code_virifi)) {
            Toast.makeText(RegisterActivity.this, "请输入验证码", Toast.LENGTH_SHORT)
                    .show();
            et_identifying_code.requestFocus();
            return;
        } else if (!code_verification.equals(code_virifi)) {
            Toast.makeText(RegisterActivity.this, "验证码不正确", Toast.LENGTH_SHORT)
                    .show();
            et_identifying_code.requestFocus();
            return;
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(RegisterActivity.this, "请输入密码", Toast.LENGTH_SHORT)
                    .show();
            et_register_psw.requestFocus();
            return;
        } else if (password.length() < 6) {
            Toast.makeText(RegisterActivity.this, "密码的长度不能小于6个字符",
                    Toast.LENGTH_SHORT).show();
            et_register_psw.requestFocus();
            return;
        } else if (password.length() > 20) {
            Toast.makeText(RegisterActivity.this, "密码的长度不能大于20个字符",
                    Toast.LENGTH_SHORT).show();
            et_register_psw.requestFocus();
            return;
        } else if (TextUtils.isEmpty(configpsw)) {
            Toast.makeText(this, "确认密码不能为空", Toast.LENGTH_SHORT).show();
            et_confirmPwdEditText.requestFocus();
            return;
        } else if (!password.equals(configpsw)) {
            Toast.makeText(this, "2次密码不一致", Toast.LENGTH_SHORT).show();
            return;
        } else if (!istrue) {
            Toast.makeText(this, "手机短信校验码错误", Toast.LENGTH_SHORT).show();
            et_phonenum.requestFocus();
            return;
        }
        final String json = "{\"name\":\"" + username + "\",\"gender\":\""
                + gender + "\",\"category\":\"" + category
                + "\",\"position\":\"" + position + "\",\"location\":\""
                + location + "\",\"address\":\"" + address + "\",\"mobile\":\""
                + phonenum + "\",\"plainPassword\":\"" + password
                + "\",\"notes\":\"" + backs + "\" " + "}";
        Log.i(TAG, json);
        pd.show();
        new Thread() {
            public void run() {
                HttpParams httpParams = new BasicHttpParams();
                httpParams.setParameter("charset", "UTF-8");
                HttpConnectionParams.setConnectionTimeout(httpParams, 20000); // 毫秒
                HttpConnectionParams.setSoTimeout(httpParams, 201000);
                HttpClient httpClient = new DefaultHttpClient(httpParams);
                HttpPost post = new HttpPost(Url.URL_REGISTER);
                StringEntity postingString;
                try {
                    postingString = new StringEntity(json, "UTF-8");
                    post.setEntity(postingString);
                    post.setHeader("Content-type",
                            "application/json;charset=UTF-8");
                    HttpResponse response = httpClient.execute(post);
                    String content = EntityUtils.toString(response.getEntity());
                    // Log.i("test",content);
                    System.out.println(content);
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
        public void handleMessage(android.os.Message msg) {
            pd.cancel();
            @SuppressWarnings("unchecked")
            Result<User2> ret = (Result<User2>) JSONUtils.parseObject(result,
                    new TypeReference<Result<User2>>() {
                    });
            String toast = ret.getMsg();
            Log.i("RegisterActivity", "result:" + toast);
            Toast.makeText(RegisterActivity.this, toast, Toast.LENGTH_SHORT)
                    .show();
            if (!toast.contains("手机号")) {

                finish();
            } else {
                // et_phonenum.setText("请输入你的手机号");
                // et_phonenum.requestFocus();
                // return;
            }
        };

    };

    public void back(View view) {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.img_back_title:// 返回按钮,点击回到登录界面
            onBackPressed();
            break;
        case R.id.btn_submit2:// 注册按钮
            try {
                register(btn_submit);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            break;
        case R.id.timebtn_verification:
            getDate();
            break;
        }
    }

    /**
     * 获取验证码
     */
    private void getDate() {
        String phone = et_phonenum.getText().toString().trim();
        if (TextUtils.isEmpty(phone) | phone.length() < 11) {
            Toast.makeText(RegisterActivity.this, "请正确输入手机号",
                    Toast.LENGTH_SHORT).show();
            et_phonenum.requestFocus();
            return;
        }
        int x = (int) (Math.random() * 10000);
        code_verification = String.format("%04d", x);
        Log.i("huwenbao", code_verification);
        AbRequestParams params = new AbRequestParams();
        params.put("mobile", phone);
        params.put("message", "尊敬的注册用户，手机验证码：" + code_verification
                + ",感谢您的使用！如非本人操作，请忽略。");
        abHttpUtil.get(Url.URL_CODE_VERYFICATION, params,
                new AbStringHttpResponseListener() {

                    @Override
                    public void onStart() {
                        AbDialogUtil.showLoadDialog(RegisterActivity.this,
                                R.drawable.progress_circular, "正在发送验证码...");
                    }

                    @Override
                    public void onFinish() {
                        AbDialogUtil.removeDialog(RegisterActivity.this);
                    }

                    @Override
                    public void onFailure(int statusCode, String content,
                            Throwable error) {

                    }

                    @Override
                    public void onSuccess(int statusCode, String content) {

                    }
                });

    }

    /**
     * 描述：获取字符串的长度.
     * 
     * @param str
     *            指定的字符串
     * @return 字符串的长度（中文字符计2个）
     */
    public static int strLength(String str) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        if (TextUtils.isEmpty(str)) {
            // 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
            for (int i = 0; i < str.length(); i++) {
                // 获取一个字符
                String temp = str.substring(i, i + 1);
                // 判断是否为中文字符
                if (temp.matches(chinese)) {
                    // 中文字符长度为2
                    valueLength += 2;
                } else {
                    // 其他字符长度为1
                    valueLength += 1;
                }
            }
        }
        return valueLength;
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
            long arg3) {
        String[][] city = initCity();

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
                RegisterActivity.this, android.R.layout.test_list_item,
                city[arg2]); // 绑定给控件
        select_city.setAdapter(adapter2);
    }

    /**
     * 初始化city
     * 
     * @param city
     */
    private String[][] initCity() {
        String[][] city = new String[][] {

                { "安庆", "蚌埠", "巢湖", "池州", "滁州", "阜阳", "合肥", "淮北", "淮南", "黄山",
                        "六安", "马鞍山", "宿州", "铜陵", "芜湖", "宣城", "亳州" },

                { "东城区", "西城区", "朝阳区", "海淀区", "丰台区", "石景山区", "门头沟区", "房山区",
                        "大兴区", "通州区", "顺义区", "昌平区", "平谷区", "怀柔区", "密云县", "延庆县" },

                { "福州", "龙岩", "南平", "宁德", "莆田", "泉州", "三明", "厦门", "漳州" },
                { "白银", "定西", "甘南藏族自治州", "嘉峪关", "金昌", "酒泉", "兰州", "临夏回族自治州",
                        "陇南", "平凉", "庆阳", "天水", "武威", "张掖" },
                { "潮州", "东莞", "佛山", "广州", "河源", "惠州", "江门", "揭阳", "茂名", "梅州",
                        "清远", "汕头", "汕尾", "韶关", "深圳", "阳江", "云浮", "湛江", "肇庆",
                        "中山", "珠海" },
                { "百色", "北海", "崇左", "防城港", "桂林", "贵港", "河池", "贺州", "来宾", "柳州",
                        "南宁", "钦州", "梧州", "玉林" },
                { "安顺", "毕节", "贵阳", "六盘水", "黔东南苗族侗族自治州", "黔南布依族苗族自治州",
                        "黔西南布依族苗族自治州", "铜仁", "遵义" },
                { "白沙黎族自治县", "保亭黎族苗族自治县", "昌江黎族自治县", "澄迈县", "定安县", "东方", "海口",
                        "乐东黎族自治县", "临高县", "陵水黎族自治县", "琼海", "琼中黎族苗族自治县", "三亚",
                        "屯昌县", "万宁", "文昌", "五指山", "儋州" },
                { "保定", "沧州", "承德", "邯郸", "衡水", "廊坊", "秦皇岛", "石家庄", "唐山", "邢台",
                        "张家口" },
                { "郑州市 ", "洛阳市 ", "开封市 ", "安阳市 ", "新乡市 ", "濮阳市 ", "焦作市 ",
                        "鹤壁市 ", "三门峡 ", "商丘市 ", "许昌市 ", "漯河市", "平顶山", "驻马店",
                        "周口市", "南阳市", "信阳市", "济源市" },
                { "大庆", "大兴安岭", "哈尔滨", "鹤岗", "黑河", "鸡西", "佳木斯", "牡丹江", "七台河",
                        "齐齐哈尔", "双鸭山", "绥化", "伊春" },
                { "鄂州", "恩施土家族苗族自治州", "黄冈", "黄石", "荆门", "荆州", "潜江", "神农架林区",
                        "十堰", "随州", "天门", "武汉", "仙桃", "咸宁", "襄樊", "孝感", "宜昌" },

                { "常德", "长沙", "郴州", "衡阳", "怀化", "娄底", "邵阳", "湘潭", "湘西土家族苗族自治州",
                        "益阳", "永州", "岳阳", "张家界", "株洲" },

                { "白城", "白山", "长春", "吉林", "辽源", "四平", "松原", "通化", "延边朝鲜族自治州" },
                { "常州", "淮安", "连云港", "南京", "南通", "苏州", "宿迁", "泰州", "无锡", "徐州",
                        "盐城", "扬州", "镇江" },
                { "抚州", "赣州", "吉安", "景德镇", "九江", "南昌", "萍乡", "上饶", "新余", "宜春",
                        "鹰潭" },
                { "鞍山", "本溪", "朝阳", "大连", "丹东", "抚顺", "阜新", "葫芦岛", "锦州", "辽阳",
                        "盘锦", "沈阳", "铁岭", "营口" },
                { "阿拉善盟", "巴彦淖尔盟", "包头", "赤峰", "鄂尔多斯", "呼和浩特", "呼伦贝尔", "通辽",
                        "乌海", "乌兰察布盟", "锡林郭勒盟", "兴安盟" },
                { "固原", "石嘴山", "吴忠", "银川" },
                { "果洛藏族自治州", "海北藏族自治州", "海东", "海南藏族自治州", "海西蒙古族藏族自治州",
                        "黄南藏族自治州", "西宁", "玉树藏族自治州" },
                { "滨州", "德州", "东营", "菏泽", "济南", "济宁", "莱芜", "聊城", "临沂", "青岛",
                        "日照", "泰安", "威海", "潍坊", "烟台", "枣庄", "淄博" },
                { "长治", "大同", "晋城", "晋中", "临汾", "吕梁", "朔州", "太原", "忻州", "阳泉",
                        "运城" },
                { "安康", "宝鸡", "汉中", "商洛", "铜川", "渭南", "西安", "咸阳", "延安", "榆林" },
                { "静安区", "长宁区", "徐汇区", "黄浦区", "虹口区", "普陀区", "杨浦区", "闸北区",
                        "浦东新区", "宝山区", "松江区", "嘉定区", "金山区", "青浦区", "奉贤区",
                        "崇明县", "闵行区" },
                { "阿坝藏族羌族自治州", "巴中", "成都", "达州", "德阳", "甘孜藏族自治州", "广安", "广元",
                        "乐山", "凉山彝族自治州", "眉山", "绵阳", "南充", "内江", "攀枝花", "遂宁",
                        "雅安", "宜宾", "资阳", "自贡", "泸州" },
                { "和平区", "河西区", "南开区", "河东区", "河北区", "红桥区", "东丽区", "津南区",
                        "西青区", "北辰区", "滨海新区", "武清区", "宝坻区", "蓟县", "宁河县", "静海县" },
                { "阿里", "昌都", "拉萨", "林芝", "那曲", "日喀则", "山南" },
                { "阿克苏", "阿拉尔", "巴音郭楞蒙古自治州", "博尔塔拉蒙古自治州", "昌吉回族自治州", "哈密",
                        "和田", "喀什", "克拉玛依", "克孜勒苏柯尔克孜自治州", "石河子", "图木舒克",
                        "吐鲁番", "乌鲁木齐", "五家渠", "伊犁哈萨克自治州" },
                { "保山", "楚雄彝族自治州", "大理白族自治州", "德宏傣族景颇族自治州", "迪庆藏族自治州",
                        "红河哈尼族彝族自治州", "昆明", "丽江", "临沧", "怒江僳僳族自治州", "曲靖", "思茅",
                        "文山壮族苗族自治州", "西双版纳傣族自治州", "玉溪", "昭通" },
                { "杭州", "湖州", "嘉兴", "金华", "丽水", "宁波", "绍兴", "台州", "温州", "舟山",
                        "衢州" },
                { "渝中区", " 大渡口区", " 江北区", " 沙坪坝区 ", "九龙坡区 ", "南岸区 ", "北碚区",
                        " 万盛区", " 双桥区", " 渝北区 ", "巴南区万州区 ", "涪陵区 ", "黔江区",
                        " 长寿区 ", "江津区", " 合川区 ", "永川区 ", "南川区", " 綦江县", " 潼南县",
                        " 铜梁县", " 大足县", " 荣昌县", " 璧山县", " 垫江县", " 武隆县", " 丰都县",
                        " 城口县", " 梁平县", " 开县", " 巫溪县", " 巫山县", " 奉节县", " 云阳县",
                        " 忠县 ", "石柱土家族自治县", " 彭水苗族土家族自治县", " 酉阳土家族苗族自治县 ",
                        "秀山土家族苗族自治县" },

        };
        return city;
    }
}
