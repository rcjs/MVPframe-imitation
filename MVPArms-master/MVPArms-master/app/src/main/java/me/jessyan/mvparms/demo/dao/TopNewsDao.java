package me.jessyan.mvparms.demo.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by luoliwen on 16/4/28.
 * 完成对数据库的CRUD的操作
 */
public class TopNewsDao {
    private Context context;
    private Dao<TopNews, Integer> userDao;
    private DataBaseHelper helper;
    private static TopNewsDao sDBUtis;
    public TopNewsDao(Context context) {
        this.context = context;
        helper = DataBaseHelper.getInstance(context);
        try {
            userDao = helper.getDao(TopNews.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static synchronized TopNewsDao getDB(Context context) {
        if (sDBUtis == null)
            sDBUtis = new TopNewsDao(context);
        return sDBUtis;
    }

    public void addUser(TopNews user) {
        try {
            List<TopNews> list = new ArrayList<>();
             if(listAll() == null){
                 return;
             }
            list = listAll();
            if(listAll().size() > 200){
                deleteMulUser(list);
            }
            userDao.create(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * select * from user_info
     *
     * @return
     */
    public List<TopNews> listAll() {
        try {
            return userDao.queryForAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteMulUser(List<TopNews> users) {
        try {
            userDao.delete(users);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void insertHasRead(String key, int value) {
        TopNews user = new TopNews();
        user.setKey(key);
        user.setIs_read(value);
        addUser(user);
    }
    public boolean isRead(String key, int value) {
        boolean isRead = false;
        for(TopNews topNews : queryBuilder(key)){
            if(topNews.getIs_read() == value){
                isRead = true;
            }
        }
        return isRead;
    }


    /**
     * 查询条件 一
     * select * from user_info where  (age> 23 and name like ? ) and XXXX
     *
     * @return
     */
    public List<TopNews> queryBuilder(String key) {
        List<TopNews> list = new ArrayList<>();
        QueryBuilder<TopNews, Integer> queryBuilder = userDao.queryBuilder();
        //声明的是一个where 条件
        try {
            list = userDao.queryBuilder().where().eq("key", key).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

}
