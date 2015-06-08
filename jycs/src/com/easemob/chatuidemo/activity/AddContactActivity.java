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

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.util.AbLogUtil;
import com.ab.util.AbSharedUtil;
import com.ab.util.AbToastUtil;
import com.alibaba.fastjson.TypeReference;
import com.easemob.chat.EMContactManager;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.adapter.AddContactAdapter;
import com.easemob.chatuidemo.domain.IM;
import com.easemob.chatuidemo.domain.LocalIM;
import com.easemob.chatuidemo.domain.Result;
import com.easemob.chatuidemo.domain.UploadedFile;
import com.easemob.chatuidemo.domain.User2;
import com.easemob.chatuidemo.http.Url;
import com.easemob.chatuidemo.utils.jsonutils.JSONUtils;

public class AddContactActivity extends BaseActivity implements
        OnItemSelectedListener {

    private static final String TAG = "AddContactActivity";
    private EditText editText;
    private LinearLayout searchedUserLayout;
    private TextView nameText, mTextView;
    private Button searchBtn;
    private ImageView avatar;
    private InputMethodManager inputMethodManager;
    private String toAddUsername;
    private ProgressDialog progressDialog;
    private AbHttpUtil abHttpUtil;
    private Result<User2> ret;
    private String url;
    private DemoApplication application;
    private Spinner spinner_select;
    private Spinner spinner_select_detail;
    private Spinner spinner_select_detail2;
    private String[] selects;
    private String[] mGoves;
    private ListView list_contact;
    private List<LocalIM> list_im;
    private AddContactAdapter adapter;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        application = (DemoApplication) getApplication();
        if (!application.isNetworkConnected()) {
            AbToastUtil.showToast(AddContactActivity.this,
                    R.string.Connection_failure);
        }
        abHttpUtil = AbHttpUtil.getInstance(AddContactActivity.this);
        abHttpUtil.setTimeout(20000);
        mTextView = (TextView) findViewById(R.id.add_list_friends);
        spinner_select = (Spinner) this.findViewById(R.id.spinner_select);
        spinner_select_detail = (Spinner) this
                .findViewById(R.id.spinner_select_detail);
        editText = (EditText) findViewById(R.id.edit_note);
        spinner_select_detail2 = (Spinner) this
                .findViewById(R.id.spinner_select_detail2);
        String strAdd = getResources().getString(R.string.add_friend);
        mTextView.setText(strAdd);
        String strUserName = getResources().getString(R.string.user_name);
        editText.setHint(strUserName);
        searchedUserLayout = (LinearLayout) findViewById(R.id.ll_user);
        nameText = (TextView) findViewById(R.id.name);
        searchBtn = (Button) findViewById(R.id.search);
        avatar = (ImageView) findViewById(R.id.avatar);
        list_contact = (ListView) this.findViewById(R.id.list_contact);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        initAddress();
        initGov();
        // list_contact.get
        list_contact.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                toAddUsername = list_im.get(arg2).getImName();
                addContact();
                // addContact(s)
            }
        });
        // adapter = new AddContactAdapter(
        // AddContactActivity.this, list_im);
        // list_contact.setAdapter(adapter);
    }

    private void initAddress() {
        selects = new String[] { "地区", "手机号", "姓名", "所在单位" };
        ArrayAdapter<String> govAdapter = new ArrayAdapter<String>(
                AddContactActivity.this, android.R.layout.simple_spinner_item,
                selects);
        govAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner_select.setAdapter(govAdapter);
        spinner_select.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
            long arg3) {
        if (arg2 == 0) {
            spinner_select_detail.setVisibility(View.VISIBLE);
            editText.setVisibility(View.GONE);
            spinner_select_detail2.setVisibility(View.VISIBLE);
        } else {
            switch (arg2) {
            case 1:
                editText.setHint("请输入手机号");
                break;
            case 2:
                editText.setHint("请输入姓名");
                break;
            case 3:
                editText.setHint("请输入所在单位");
                break;

            default:
                break;
            }
            spinner_select_detail.setVisibility(View.GONE);
            editText.setVisibility(View.VISIBLE);
            spinner_select_detail2.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }

    /**
     * 初始化省份
     */
    private void initGov() {
        mGoves = new String[] { "安徽", "北京", "福建", "甘肃", "广东", "广西", "贵州", "海南",
                "河北", "河南", "黑龙江", "湖北", "湖南", "吉林", "江苏", "江西", "辽宁", "内蒙古",
                "宁夏", "青海", "山东", "山西", "陕西", "上海", "四川", "天津", "西藏", "新疆",
                "云南", "浙江", "重庆" };
        ArrayAdapter<String> govAdapter = new ArrayAdapter<String>(
                AddContactActivity.this, android.R.layout.simple_spinner_item,
                mGoves);
        govAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner_select_detail.setAdapter(govAdapter);
        spinner_select_detail
                .setOnItemSelectedListener(new OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                            int arg2, long arg3) {
                        String[][] city = initCity();

                        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
                                AddContactActivity.this,
                                android.R.layout.test_list_item, city[arg2]); // 绑定给控件
                        spinner_select_detail2.setAdapter(adapter2);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub

                    }
                });
    }

    /**
     * 查找contact
     * 
     * @param v
     */
    public void searchContact(View v) {

        String name = editText.getText().toString();
        String saveText = searchBtn.getText().toString();
        String params_select = spinner_select.getSelectedItem().toString()
                .trim();
        String select_gove = spinner_select_detail.getSelectedItem().toString()
                .trim();
        String select_city = spinner_select_detail2.getSelectedItem()
                .toString().trim();
        AbLogUtil.i(AddContactActivity.class, params_select);
        if (getString(R.string.button_search).equals(saveText)) {

            // if (TextUtils.isEmpty(name)) {
            // String st = getResources().getString(
            // R.string.Please_enter_a_username);
            // startActivity(new Intent(this, AlertDialog.class).putExtra(
            // "msg", st));
            // return;
            // } else {
            AbRequestParams params = new AbRequestParams();
           String userId =  AbSharedUtil.getString(application, "userId");
            params.put("userIdExc", userId);
            String requeset_params = null;
            String requeset_valuas = null;
            if (params_select.equals("地区")) {
                requeset_params = "location";
                if (select_city.equals("无")) {
                    requeset_valuas = select_gove;
                } else {
                    requeset_valuas = select_gove + select_city;
                }
            } else if (params_select.equals("手机号")) {
                editText.setHint("请输入手机号");
                requeset_params = "mobile";
                if (TextUtils.isEmpty(name)) {
                    String st = getResources().getString(
                            R.string.Please_enter_a_phonenumber);
                    startActivity(new Intent(this, AlertDialog.class).putExtra(
                            "msg", st));
                    return;
                }
                requeset_valuas = name;
            } else if (params_select.equals("姓名")) {
                editText.setHint("请输入姓名");
                requeset_params = "name";
                // String name = editText.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    String st = getResources().getString(
                            R.string.Please_enter_a_username);
                    startActivity(new Intent(this, AlertDialog.class).putExtra(
                            "msg", st));
                    return;
                }
                requeset_valuas = name;
            } else if (params_select.equals("所在单位")) {
                editText.setHint("请输入所在单位");
                requeset_params = "address";
                // String name = editText.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    String st = getResources().getString(
                            R.string.Please_enter_a_addresss);
                    startActivity(new Intent(this, AlertDialog.class).putExtra(
                            "msg", st));
                    return;
                }
                requeset_valuas = name;
            }
            AbLogUtil.i(AddContactActivity.class, "requeset_params"
                    + requeset_params + "requeset_valuas" + requeset_valuas);
            params.put(requeset_params, requeset_valuas);
            abHttpUtil.get(Url.URL_SEARCH, params,
                    new AbStringHttpResponseListener() {

                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onFinish() {

                        }

                        @Override
                        public void onFailure(int statusCode, String content,
                                Throwable error) {
                            AbToastUtil.showToast(AddContactActivity.this,
                                    statusCode + content);
                        }

                        @SuppressWarnings("unchecked")
                        @Override
                        public void onSuccess(int statusCode, String content) {
                            ret = (Result<User2>) JSONUtils.parseObject(
                                    content,
                                    new TypeReference<Result<User2>>() {
                                    });
                            if (ret != null) {
                                List<User2> list = ret.getResults();
                                list_im = new ArrayList<LocalIM>();
                                if (list != null && list.size() > 0) {
                                    LocalIM im;
                                    for (User2 user2 : list) {
                                        if (user2 != null) {
                                            im = new LocalIM();
                                            String name = user2.getName();

                                            if (!TextUtils.isEmpty(name)) {
                                                im.setUsername(name);
                                            }
                                            String phonenumber = user2
                                                    .getMobile();
                                            if (!TextUtils.isEmpty(phonenumber)) {
                                                im.setPhonenumber(phonenumber);
                                            }
                                            String category = user2
                                                    .getCategory();
                                            if (!TextUtils.isEmpty(category)) {
                                                im.setCategory(category);
                                            }
                                            String screenName = user2
                                                    .getScreenName();
                                            if (!TextUtils.isEmpty(screenName)) {
                                                im.setScreenName(screenName);
                                            }
                                            UploadedFile avater = user2
                                                    .getAvatar();
                                            if (avater != null) {
                                                String url = avater
                                                        .getThumbnailUrl();
                                                if (!TextUtils.isEmpty(url)) {
                                                    im.setUrl(url);
                                                }
                                            }
                                            IM im2 = user2.getIm();
                                            String imname = "";
                                            if (im2 != null) {
                                                imname = im2.getUsername();
                                                im.setImName(imname);
                                                Log.i(TAG, "当前用户环信用户名为："
                                                        + imname
                                                        + "---本地服务器用户名为" + name);
                                            }
                                            list_im.add(im);
                                        }
                                    }
                                    // adapter.notifyDataSetChanged();
                                    adapter = new AddContactAdapter(
                                            AddContactActivity.this, list_im);
                                    list_contact.setAdapter(adapter);
                                    // User2 user2 = list.get(0);
                                    // if (user2 != null) {
                                    // String name = user2.getName();
                                    // UploadedFile photo = user2.getAvatar();
                                    // if (photo != null) {
                                    // url = Url.url_base_img_news
                                    // + photo.getThumbnailUrl();
                                    // application.getLoader().display(
                                    // avatar, url);
                                    // }
                                    // // 服务器存在此用户，显示此用户和添加按钮
                                    // searchedUserLayout
                                    // .setVisibility(View.VISIBLE);
                                    // nameText.setText(name);
                                    // IM im = user2.getIm();
                                    // if (im != null) {
                                    // toAddUsername = im.getUsername();
                                    // }
                                    // }
                                } else {
                                    // TODO 从服务器获取此contact,如果不存在提示不存在此用户
                                    // searchedUserLayout.setVisibility(View.GONE);
                                    AbToastUtil.showToast(
                                            AddContactActivity.this, "找不到用户");
                                }
                            }
                        }
                    });
            // }
        }
    }

    /**
     * 添加contact
     * 
     * @param view
     */
    // public void addContact(View view) {
    private void addContact() {

        // toAddUsername = list_im.get(arg0)
        if (DemoApplication.getInstance().getUserName().equals(toAddUsername)) {
            String str = getString(R.string.not_add_myself);
            startActivity(new Intent(this, AlertDialog.class).putExtra("msg",
                    str));
            return;
        }

        if (DemoApplication.getInstance().getContactList()
                .containsKey(toAddUsername)) {
            // 提示已在好友列表中，无需添加
            if (EMContactManager.getInstance().getBlackListUsernames()
                    .contains(toAddUsername)) {
                startActivity(new Intent(this, AlertDialog.class).putExtra(
                        "msg", "此用户已是你好友(被拉黑状态)，从黑名单列表中移出即可"));
                return;
            }
            String strin = getString(R.string.This_user_is_already_your_friend);
            startActivity(new Intent(this, AlertDialog.class).putExtra("msg",
                    strin));
            return;
        }
        Builder builder = new android.app.AlertDialog.Builder(
                AddContactActivity.this);
        builder.setTitle("请输入添加好友信息");
        builder.setIcon(android.R.drawable.ic_dialog_info);
        final EditText editText = new EditText(AddContactActivity.this);
        builder.setView(editText);
        builder.setPositiveButton("确定", new OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                String s = editText.getText().toString().trim();
                addContact(s);
            }
        });
        builder.setNegativeButton("取消", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.cancel();
            }
        });
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 添加信息
     * 
     * @param 附加信息
     */
    private void addContact(final String s) {
        progressDialog = new ProgressDialog(this);
        String stri = getResources().getString(R.string.Is_sending_a_request);
        progressDialog.setMessage(stri);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        new Thread(new Runnable() {
            public void run() {

                try {
                    Log.i(TAG, "输入的加好友附加信息是:" + s);
                    EMContactManager.getInstance().addContact(toAddUsername, s);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s1 = getResources().getString(
                                    R.string.send_successful);
                            Toast.makeText(getApplicationContext(), s1, 1)
                                    .show();
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s2 = getResources().getString(
                                    R.string.Request_add_buddy_failure);
                            Toast.makeText(getApplicationContext(),
                                    s2 + e.getMessage(), 1).show();
                        }
                    });
                }
            }
        }).start();
    }

    public void back(View v) {
        finish();
    }

    /**
     * 初始化city
     * 
     * @param city
     */
    private String[][] initCity() {
        String[][] city = new String[][] {

                { "无", "安庆", "蚌埠", "巢湖", "池州", "滁州", "阜阳", "合肥", "淮北", "淮南",
                        "黄山", "六安", "马鞍山", "宿州", "铜陵", "芜湖", "宣城", "亳州" },

                { "无", "东城区", "西城区", "朝阳区", "海淀区", "丰台区", "石景山区", "门头沟区",
                        "房山区", "大兴区", "通州区", "顺义区", "昌平区", "平谷区", "怀柔区", "密云县",
                        "延庆县" },

                { "无", "福州", "龙岩", "南平", "宁德", "莆田", "泉州", "三明", "厦门", "漳州" },
                { "无", "白银", "定西", "甘南藏族自治州", "嘉峪关", "金昌", "酒泉", "兰州",
                        "临夏回族自治州", "陇南", "平凉", "庆阳", "天水", "武威", "张掖" },
                { "无", "潮州", "东莞", "佛山", "广州", "河源", "惠州", "江门", "揭阳", "茂名",
                        "梅州", "清远", "汕头", "汕尾", "韶关", "深圳", "阳江", "云浮", "湛江",
                        "肇庆", "中山", "珠海" },
                { "无", "百色", "北海", "崇左", "防城港", "桂林", "贵港", "河池", "贺州", "来宾",
                        "柳州", "南宁", "钦州", "梧州", "玉林" },
                { "无", "安顺", "毕节", "贵阳", "六盘水", "黔东南苗族侗族自治州", "黔南布依族苗族自治州",
                        "黔西南布依族苗族自治州", "铜仁", "遵义" },
                { "无", "白沙黎族自治县", "保亭黎族苗族自治县", "昌江黎族自治县", "澄迈县", "定安县", "东方",
                        "海口", "乐东黎族自治县", "临高县", "陵水黎族自治县", "琼海", "琼中黎族苗族自治县",
                        "三亚", "屯昌县", "万宁", "文昌", "五指山", "儋州" },
                { "无", "保定", "沧州", "承德", "邯郸", "衡水", "廊坊", "秦皇岛", "石家庄", "唐山",
                        "邢台", "张家口" },
                { "无", "郑州市 ", "洛阳市 ", "开封市 ", "安阳市 ", "新乡市 ", "濮阳市 ", "焦作市 ",
                        "鹤壁市 ", "三门峡 ", "商丘市 ", "许昌市 ", "漯河市", "平顶山", "驻马店",
                        "周口市", "南阳市", "信阳市", "济源市" },
                { "无", "大庆", "大兴安岭", "哈尔滨", "鹤岗", "黑河", "鸡西", "佳木斯", "牡丹江",
                        "七台河", "齐齐哈尔", "双鸭山", "绥化", "伊春" },
                { "无", "鄂州", "恩施土家族苗族自治州", "黄冈", "黄石", "荆门", "荆州", "潜江",
                        "神农架林区", "十堰", "随州", "天门", "武汉", "仙桃", "咸宁", "襄樊",
                        "孝感", "宜昌" },

                { "无", "常德", "长沙", "郴州", "衡阳", "怀化", "娄底", "邵阳", "湘潭",
                        "湘西土家族苗族自治州", "益阳", "永州", "岳阳", "张家界", "株洲" },

                { "无", "白城", "白山", "长春", "吉林", "辽源", "四平", "松原", "通化",
                        "延边朝鲜族自治州" },
                { "无", "常州", "淮安", "连云港", "南京", "南通", "苏州", "宿迁", "泰州", "无锡",
                        "徐州", "盐城", "扬州", "镇江" },
                { "无", "抚州", "赣州", "吉安", "景德镇", "九江", "南昌", "萍乡", "上饶", "新余",
                        "宜春", "鹰潭" },
                { "无", "鞍山", "本溪", "朝阳", "大连", "丹东", "抚顺", "阜新", "葫芦岛", "锦州",
                        "辽阳", "盘锦", "沈阳", "铁岭", "营口" },
                { "无", "阿拉善盟", "巴彦淖尔盟", "包头", "赤峰", "鄂尔多斯", "呼和浩特", "呼伦贝尔",
                        "通辽", "乌海", "乌兰察布盟", "锡林郭勒盟", "兴安盟" },
                { "无", "固原", "石嘴山", "吴忠", "银川" },
                { "无", "果洛藏族自治州", "海北藏族自治州", "海东", "海南藏族自治州", "海西蒙古族藏族自治州",
                        "黄南藏族自治州", "西宁", "玉树藏族自治州" },
                { "无", "滨州", "德州", "东营", "菏泽", "济南", "济宁", "莱芜", "聊城", "临沂",
                        "青岛", "日照", "泰安", "威海", "潍坊", "烟台", "枣庄", "淄博" },
                { "无", "长治", "大同", "晋城", "晋中", "临汾", "吕梁", "朔州", "太原", "忻州",
                        "阳泉", "运城" },
                { "无", "安康", "宝鸡", "汉中", "商洛", "铜川", "渭南", "西安", "咸阳", "延安",
                        "榆林" },
                { "无", "静安区", "长宁区", "徐汇区", "黄浦区", "虹口区", "普陀区", "杨浦区", "闸北区",
                        "浦东新区", "宝山区", "松江区", "嘉定区", "金山区", "青浦区", "奉贤区",
                        "崇明县", "闵行区" },
                { "无", "阿坝藏族羌族自治州", "巴中", "成都", "达州", "德阳", "甘孜藏族自治州", "广安",
                        "广元", "乐山", "凉山彝族自治州", "眉山", "绵阳", "南充", "内江", "攀枝花",
                        "遂宁", "雅安", "宜宾", "资阳", "自贡", "泸州" },
                { "无", "无", "和平区", "河西区", "南开区", "河东区", "河北区", "红桥区", "东丽区",
                        "津南区", "西青区", "北辰区", "滨海新区", "武清区", "宝坻区", "蓟县", "宁河县",
                        "静海县" },
                { "无", "阿里", "昌都", "拉萨", "林芝", "那曲", "日喀则", "山南" },
                { "无", "阿克苏", "阿拉尔", "巴音郭楞蒙古自治州", "博尔塔拉蒙古自治州", "昌吉回族自治州", "哈密",
                        "和田", "喀什", "克拉玛依", "克孜勒苏柯尔克孜自治州", "石河子", "图木舒克",
                        "吐鲁番", "乌鲁木齐", "五家渠", "伊犁哈萨克自治州" },
                { "无", "保山", "楚雄彝族自治州", "大理白族自治州", "德宏傣族景颇族自治州", "迪庆藏族自治州",
                        "红河哈尼族彝族自治州", "昆明", "丽江", "临沧", "怒江僳僳族自治州", "曲靖", "思茅",
                        "文山壮族苗族自治州", "西双版纳傣族自治州", "玉溪", "昭通" },
                { "无", "杭州", "湖州", "嘉兴", "金华", "丽水", "宁波", "绍兴", "台州", "温州",
                        "舟山", "衢州" },
                { "无", "渝中区", " 大渡口区", " 江北区", " 沙坪坝区 ", "九龙坡区 ", "南岸区 ",
                        "北碚区", " 万盛区", " 双桥区", " 渝北区 ", "巴南区万州区 ", "涪陵区 ",
                        "黔江区", " 长寿区 ", "江津区", " 合川区 ", "永川区 ", "南川区", " 綦江县",
                        " 潼南县", " 铜梁县", " 大足县", " 荣昌县", " 璧山县", " 垫江县", " 武隆县",
                        " 丰都县", " 城口县", " 梁平县", " 开县", " 巫溪县", " 巫山县", " 奉节县",
                        " 云阳县", " 忠县 ", "石柱土家族自治县", " 彭水苗族土家族自治县",
                        " 酉阳土家族苗族自治县 ", "秀山土家族苗族自治县" },

        };
        return city;
    }
}
