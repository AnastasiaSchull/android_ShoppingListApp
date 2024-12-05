package com.example.android_shoppinglistapp.data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
	public enum Names {
		TABLE_SHOPPING,
		ID,
		NAME
	}
	
	public DBHelper(@Nullable Context context) {
		super(context, "shopping_db", null, 1);
		createTable();
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
	
	public void createTable() {
		String sql = "create table if not exists %s(%s integer primary key autoincrement, %s text)";
		sql = String.format(sql, Names.TABLE_SHOPPING, Names.ID, Names.NAME);
		getWritableDatabase().execSQL(sql);
	}
	
	public void dropTable() {
		String sql = "drop table if exists " + Names.TABLE_SHOPPING;
		getWritableDatabase().execSQL(sql);
	}
	
	public Shopping insert(Shopping shopping) {
		ContentValues values = new ContentValues();
		values.put(Names.NAME.toString(), shopping.getName());

		long id = getWritableDatabase()
			.insert(Names.TABLE_SHOPPING.toString(), null, values);
		//
		return selectById((int) id);
	}
	
	@SuppressLint("Range")
	public Shopping selectById(Integer id) {
		String sql = "select * from %s where %s='%s'";
		sql = String.format(sql, Names.TABLE_SHOPPING, Names.ID, id);
		try (Cursor cursor = getReadableDatabase().rawQuery(sql, null)) {
			Shopping shopping;
			//while
			if (cursor.moveToNext()) {
				shopping = new Shopping(
					cursor.getInt(cursor.getColumnIndex(Names.ID.toString())),
					cursor.getString(cursor.getColumnIndex(Names.NAME.toString()))
				);
				return shopping;
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}
	
	@SuppressLint("Range")
	public List<Shopping> selectAll() {
		String sql = "select * from " + Names.TABLE_SHOPPING;
		List<Shopping> list = new ArrayList<>();
		try (Cursor cursor = getReadableDatabase().rawQuery(sql, null)) {
			Shopping shopping;
			//while
			while (cursor.moveToNext()) {
				shopping = new Shopping(
					cursor.getInt(cursor.getColumnIndex(Names.ID.toString())),
					cursor.getString(cursor.getColumnIndex(Names.NAME.toString()))
				);
				list.add(shopping);
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		return list;
	}
	
	public void deleteById(Integer id) {
		String sql = "delete from %s where %s='%s'";
		sql = String.format(sql, Names.TABLE_SHOPPING, Names.ID, id);
		getWritableDatabase().execSQL(sql);
	}
	
	public void update(Shopping shoppingList) {
		String sql = "update %s set %s='%s' where %s='%s'";
		sql = String.format(sql, Names.TABLE_SHOPPING,
			Names.NAME, shoppingList.getName(),
			Names.ID, shoppingList.getId()
		);
		getWritableDatabase().execSQL(sql);
	}
	
	public void insertAll(List<Shopping> list){
		for (Shopping shopping: list) {
			insert(shopping);
		}
	}
}
