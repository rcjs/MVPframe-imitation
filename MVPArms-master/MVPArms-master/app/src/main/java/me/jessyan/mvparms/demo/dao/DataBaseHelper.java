package me.jessyan.mvparms.demo.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import me.jessyan.mvparms.demo.config.Config;


/**
 * Created by zjl on 2016/12/22.
 */

public class DataBaseHelper extends OrmLiteSqliteOpenHelper {
    private final static String DATABASE_NAME = Config.DB_IS_READ_NAME + ".db";//创建数据以.db结尾

    private final static int VERSION = 1;//数据库的版本号

    private Map<String, Dao> maps = new HashMap<>();
    //使用单例模式
    private static DataBaseHelper instance;

    public static synchronized DataBaseHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (DataBaseHelper.class) {
                if (instance == null) {
                    instance = new DataBaseHelper(context);
                }
            }
        }
        return instance;
    }


    /**
     * 获得数据库的访问对象
     *
     * @param cls
     * @return
     * @throws SQLException
     */
    public synchronized Dao getDao(Class cls) throws SQLException {
        Dao dao = null;
        String className = cls.getSimpleName();//通过反射获得类名
        if (maps.containsKey(className)) {
            dao = maps.get(className);
        }
        if (dao == null) {
            dao = super.getDao(cls);
            maps.put(className, dao);
        }
        return dao;
    }

    /**
     * 关闭所有操作
     */
    public void close() {
        super.close();
        for (String key : maps.keySet()) {
            Dao dao = maps.get(key);
            dao = null;
        }
    }

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        //完成对数据库的创建，以及表的建立
        try {
            TableUtils.createTable(connectionSource, Zhihu.class);
            TableUtils.createTable(connectionSource, TopNews.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        try {
            TableUtils.dropTable(connectionSource, Zhihu.class, true);
            TableUtils.dropTable(connectionSource, TopNews.class, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
