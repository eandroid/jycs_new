package com.easemob.chatuidemo.domain.dao;

import android.content.Context;

import com.ab.db.orm.dao.AbDBDaoImpl;
import com.easemob.chatuidemo.domain.Square;
import com.easemob.chatuidemo.domain.helper.DBInsideHelper;

/**
 * 广场dao
 * @author 让雨灭了火
 *
 */
public class SquareInsideDao extends AbDBDaoImpl<Square>{

    public SquareInsideDao(Context context) {
        super(new DBInsideHelper(context),Square.class);
    }
    
}
