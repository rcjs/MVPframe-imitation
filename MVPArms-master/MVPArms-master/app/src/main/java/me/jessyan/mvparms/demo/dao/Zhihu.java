package me.jessyan.mvparms.demo.dao;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by luoliwen on 16/4/27.
 */
@DatabaseTable(tableName = "zhihu")
public class Zhihu {
    @DatabaseField(generatedId = true)
    private int id;//generatedId = true   数据库的主键 primary key
    @DatabaseField(columnName = "key")
    private String key;
    @DatabaseField(columnName = "is_read")
    private  int is_read;

    public Zhihu(){

    }

    public Zhihu(int id, String key, int is_read) {
        this.id = id;
        this.key = key;
        this.is_read = is_read;
    }

    @Override
    public String toString() {
        return "Zhihu{" +
                "id=" + id +
                ", key='" + key + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIs_read() {
        return is_read;
    }

    public void setIs_read(int is_read) {
        this.is_read = is_read;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
