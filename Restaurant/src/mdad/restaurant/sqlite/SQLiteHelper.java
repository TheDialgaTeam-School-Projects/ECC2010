package mdad.restaurant.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public final class SQLiteHelper {
	private static final SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase("sdcard/Restaurant.db", null);
	
	public static final void initDatabase() {
		db.beginTransaction();
		db.execSQL("create table if not exists orderList(ID integer PRIMARY KEY autoincrement, menuID integer, menuQuantity integer, menuAdditionalRequest text)");
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	
	public static final Cursor getOrderItem() {
		final Cursor cursor = db.rawQuery("select * from orderList", null);
		return cursor;
	}
	
	public static final Boolean getMenuExists(int menuID) {
		final Cursor cursor = db.rawQuery("select * from orderList where menuID = ?", new String[] { String.valueOf(menuID) });
		if (cursor.getCount() > 0) {
			return true;
		} else
			return false;
	}
	
	public static final int getMenuQuantity(int menuID) {
		final Cursor cursor = db.rawQuery("select menuQuantity from orderList where menuID = ?", new String[] { String.valueOf(menuID) });
		if (cursor.getCount() > 0) {
			cursor.moveToNext();
			return cursor.getInt(0);
		} else
			return 0;
	}
	
	public static final String getMenuAdditionalRequest(int menuID) {
		final Cursor cursor = db.rawQuery("select menuAdditionalRequest from orderList where menuID = ?", new String[] { String.valueOf(menuID) });
		if (cursor.getCount() > 0) {
			cursor.moveToNext();
			return cursor.getString(0);
		} else
			return null;
	}
	
	public static final void setOrder(int menuID, int menuQuantity, String menuAdditionalRequest) {
		db.beginTransaction();
		if (getMenuExists(menuID))
			db.execSQL("update orderList set menuQuantity = ?, menuAdditionalRequest = ? where menuID = ?", new Object[] { String.valueOf(menuQuantity), menuAdditionalRequest, String.valueOf(menuID) });
		else
			db.execSQL("insert into orderList(menuID, menuQuantity, menuAdditionalRequest) values (?, ?, ?)", new Object[] { String.valueOf(menuID), String.valueOf(menuQuantity), menuAdditionalRequest });
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	
	public static final void deleteOrder(int menuID) {
		db.beginTransaction();
		db.execSQL("delete from orderList where menuID = ?", new Object[] { String.valueOf(menuID) });
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	
	public static final void resetOrder() {
		db.beginTransaction();
		db.execSQL("delete from orderList");
		db.setTransactionSuccessful();
		db.endTransaction();
	}
}
