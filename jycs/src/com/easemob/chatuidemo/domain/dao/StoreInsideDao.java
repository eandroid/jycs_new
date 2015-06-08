package com.easemob.chatuidemo.domain.dao;

import android.content.Context;

import com.ab.db.orm.dao.AbDBDaoImpl;
import com.easemob.chatuidemo.domain.helper.DBInsideHelper;
/**
 * 门店的dao
 * @author 让雨灭了火
 * 
 *
 */
public class StoreInsideDao extends AbDBDaoImpl<Store> {

    public StoreInsideDao(Context context) {
        super(new DBInsideHelper(context),Store.class);
    }

}
