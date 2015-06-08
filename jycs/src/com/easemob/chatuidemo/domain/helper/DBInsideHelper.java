package com.easemob.chatuidemo.domain.helper;

import android.content.Context;

import com.ab.db.orm.AbDBHelper;
import com.easemob.chatuidemo.domain.IM;
import com.easemob.chatuidemo.domain.InvidateUser;
import com.easemob.chatuidemo.domain.LocalIM;
import com.easemob.chatuidemo.domain.Square;
import com.easemob.chatuidemo.domain.Unit;
import com.easemob.chatuidemo.domain.UploadedFile;
import com.easemob.chatuidemo.domain.User2;
import com.easemob.chatuidemo.domain.dao.Store;

/**
 * 
 * ? 2012 amsoft.cn 名称：DBInsideHelper.java 描述：手机data/data下面的数据库
 * 
 * @author 还如一梦中
 * @date：2013-7-31 下午3:50:18
 * @version v1.0
 */
public class DBInsideHelper extends AbDBHelper {
    // 数据库名
    private static final String DBNAME = "jycs02.db";

    // 当前数据库的版本
    private static final int DBVERSION = 1;
    // 要初始化的表
    private static final Class<?>[] clazz = { User2.class, UploadedFile.class,
            IM.class, Unit.class, Square.class, Store.class, LocalIM.class,InvidateUser.class
             };

    public DBInsideHelper(Context context) {
        super(context, DBNAME, null, DBVERSION, clazz);
    }

}
