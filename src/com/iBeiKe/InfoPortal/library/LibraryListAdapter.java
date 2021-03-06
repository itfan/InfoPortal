package com.iBeiKe.InfoPortal.library;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iBeiKe.InfoPortal.R;
import com.iBeiKe.InfoPortal.database.Database;
import com.iBeiKe.InfoPortal.library.MyLibList;

/**
 * 已借图书列表显示控制。
 * 继承自BaseAdapter，提供对已借图书的保存、显示，
 * 获取已借图书数量，获取元素，
 * 获取元素ID以及对列表内元素以及动作进行维护支持，对显示数据的格式化。
 *
 */
public class LibraryListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private Database db;
	private Context context;
	private String[] columns;
	private String tableName, orderBy;
	
	public LibraryListAdapter(Context context) {
		this.context = context;
		db = new Database(context);
		tableName = "lib_my";
		orderBy = "returns ASC";
		columns = new String[] {"bar_code", "marc_no", "title",
				"author", "store", "borrow", "returns", "time"};
		mInflater = LayoutInflater.from(context);
	}
	
	private void saveData(ArrayList<MyLibList> myLibList) {
		int num = myLibList.size();
		long timeMillis = System.currentTimeMillis();
		ContentValues cv = new ContentValues();
		db.write();
		for(int i=0;i<num;i++) {
			cv.put(columns[0], myLibList.get(i).barCode);
			cv.put(columns[1], myLibList.get(i).marcNo);
			cv.put(columns[2], myLibList.get(i).title);
			cv.put(columns[3], myLibList.get(i).author);
			cv.put(columns[4], myLibList.get(i).store);
			cv.put(columns[5], myLibList.get(i).borrow);
			cv.put(columns[6], myLibList.get(i).returns);
			cv.put(columns[7], timeMillis);
			db.insert(tableName, cv);
			cv.clear();
		}
		String where = "time!=" + timeMillis;
		db.delete(tableName, where);
		db.close();
	}
	
	public void setData(ArrayList<MyLibList> myLibList) {
		saveData(myLibList);
		notifyDataSetChanged();
	}
	
	public int getCount() {
		db.read();
		Cursor cursor = db.getCursor(tableName, columns, null, orderBy);
		int count = cursor.getCount();
		db.close();
		return count;
	}

	public MyLibList getItem(int arg0) {
		db.read();
		Cursor cursor = db.getCursor(tableName, columns, null, orderBy);
		cursor.moveToPosition(arg0);
		MyLibList libList = new MyLibList(cursor.getString(0),
				cursor.getString(1), cursor.getString(2), cursor.getString(3),
				cursor.getString(4), cursor.getInt(5), cursor.getInt(6));
		db.close();
		return libList;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		View v = convertView;
		if((v == null) || (v.getTag() == null)) {
			v = mInflater.inflate(R.layout.library_row, null);
			holder = new ViewHolder();
			holder.title = (TextView)v.findViewById(R.id.lib_title);
			holder.author = (TextView)v.findViewById(R.id.lib_author);
			holder.borrow = (TextView)v.findViewById(R.id.lib_borrow);
			holder.returns = (TextView)v.findViewById(R.id.lib_returns);
			holder.store = (TextView)v.findViewById(R.id.lib_store);
			v.setTag(holder);
		} else {
			holder = (ViewHolder)v.getTag();
		}
		holder.libList = getItem(position);
		holder.title.setText(holder.libList.title);
		holder.author.setText(holder.libList.author);
		holder.borrow.setText(parseDate(holder.libList.borrow));
		holder.returns.setText(parseDate(holder.libList.returns));
		holder.store.setText(holder.libList.store);
		v.setTag(holder);
		return v;
	}
	
	public String parseDate(int date) {
		String result;
		int year = date/10000;
		result = year + "-";
		result += (date - (year*10000))/100 + "-";
		result += date % 100;
		return result;
	}
	
	public class ViewHolder {
		MyLibList libList;
		TextView title;
		TextView author;
		TextView borrow;
		TextView returns;
		TextView store;
	}
}