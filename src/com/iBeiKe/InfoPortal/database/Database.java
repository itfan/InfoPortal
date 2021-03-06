package com.iBeiKe.InfoPortal.database;

import java.util.Map;

import static android.provider.BaseColumns._ID;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 对数据库进行维护，
 * 提供数据库的重建、数据表清空，数据项删除、插入、获取cursor，
 * 获取查询字符串数组、获取查询结果整型数组，以及数据库的读取、写入、开启、关闭功能。
 * 
 */
public class Database extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "infoportal.db";
	private SQLiteDatabase db;
	private SQLiteCursor cursor;
	
	public Database(Context ctx) {
		super(ctx, DATABASE_NAME, null, 1);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db,
			int oldVersion, int newVersion) {
	}
	
	public void onUpdate(SQLiteDatabase db,
			int oldRevision, int newRevision) {
		//TODO 用于数据更新
	}
	
	public void onRebuild(Map<String,Map<String,String>> struct) {
		String SQLstring;
		for(String tableName : struct.keySet()) {
			SQLstring = "CREATE TABLE " + tableName +
					" ( " + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT";
			Map<String,String> col = struct.get(tableName);
			for(String colName : col.keySet()) {
				String attri = col.get(colName);
				SQLstring += ", " + colName + " " + attri;
			}
			SQLstring += ");";
			db.execSQL(SQLstring);
		}
	}
	
	public int clean(String table) {
		return db.delete(table, null, null);
	}
	
	public int delete(String table, String where) {
		return db.delete(table, where, null);
	}
	
	public void insert(String table, Map<String,String> content) {
    	ContentValues values = new ContentValues();
    	for(String key : content.keySet()) {
    		values.put(key, content.get(key));
    	}
    	db.insertOrThrow(table, null, values);
	}

	public void insert(String table, ContentValues contentValues) {
    	db.insertOrThrow(table, null, contentValues);
	}
	
	public SQLiteCursor getCursor(String table,
			String[] columns, String selection, String orderBy) {
		cursor = (SQLiteCursor) db.query(
				table, columns, selection, null, null, null, orderBy);
		return cursor;
	}
    
    public String[] getString(String table, String column,
    		String selection, String orderBy, int limit) {
		String[] columns = new String[]{column};
    	cursor = (SQLiteCursor) db.query(
    			table, columns, selection, null, null, null, orderBy);
    	int count = cursor.getCount();
    	String[] queryResult = new String[count];
    	int i=0;
    	if(count != 0) {
        	if(limit==0) {
        		limit = count;
        	}
    		while(cursor.moveToNext() && limit>0) {
    			limit--;
    			queryResult[i++] = cursor.getString(0); 
    		}
    		return queryResult;
    	} else {
    		return null;
    	}
    }
    
    public int[] getInt(String table, String column,
    		String selection, String orderBy, int limit) {
		String[] columns = new String[]{column};
    	cursor = (SQLiteCursor) db.query(
    			table, columns, selection, null, null, null, orderBy);
    	int count = cursor.getCount();
    	int[] queryResult = new int[count];
    	int i=0;
    	if(count != 0) {
        	if(limit==0) {
        		limit = count;
        	}
    		while(cursor.moveToNext() && limit>0) {
    			limit--;
    			queryResult[i++] = cursor.getInt(0); 
    		}
    		return queryResult;
    	} else {
    		return null;
    	}
    }
	/**
	 * <p>Get a writable/readable database.</p>
	 * <b>Warning:</b>
	 * <p>Remember to call close();</p>
	 */
	public void read() {
		db = this.getReadableDatabase();
	}
	public void write() {
		db = this.getWritableDatabase();
	}
	/**
	 * <p>Close the database.</p>
	 */
	@Override
	public void close() {
		super.close();
		db.close();
	}
}