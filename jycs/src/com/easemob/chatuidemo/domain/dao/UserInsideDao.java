package com.easemob.chatuidemo.domain.dao;

import android.content.Context;

import com.ab.db.orm.dao.AbDBDaoImpl;
import com.easemob.chatuidemo.domain.User2;
import com.easemob.chatuidemo.domain.helper.DBInsideHelper;
/**
 * userdao 层
 * @author 让火灭了雨
 *
 */
public class UserInsideDao extends AbDBDaoImpl<User2>{

    public UserInsideDao(Context context) {
        super(new DBInsideHelper(context),User2.class);
    }

}
