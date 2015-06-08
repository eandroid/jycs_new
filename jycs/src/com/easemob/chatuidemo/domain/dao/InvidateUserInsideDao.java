package com.easemob.chatuidemo.domain.dao;

import android.content.Context;

import com.ab.db.orm.dao.AbDBDaoImpl;
import com.easemob.chatuidemo.domain.InvidateUser;
import com.easemob.chatuidemo.domain.helper.DBInsideHelper;

public class InvidateUserInsideDao extends AbDBDaoImpl<InvidateUser> {

    public InvidateUserInsideDao(Context context) {
        super(new DBInsideHelper(context),InvidateUser.class);
    }
    
}
