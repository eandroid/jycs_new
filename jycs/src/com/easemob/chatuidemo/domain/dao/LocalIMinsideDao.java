package com.easemob.chatuidemo.domain.dao;

import android.content.Context;

import com.ab.db.orm.dao.AbDBDaoImpl;
import com.easemob.chatuidemo.domain.LocalIM;
import com.easemob.chatuidemo.domain.helper.DBInsideHelper;
/**
 * im本地存储用户信息dao层
 * @author 让雨灭了火
 *
 */
public class LocalIMinsideDao extends AbDBDaoImpl<LocalIM>{

    public LocalIMinsideDao(Context context) {
        super(new DBInsideHelper(context),LocalIM.class);
    }
}
