package com.example.administrator.demon;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Administrator on 2016/9/30.
 */
public class PeopleContentProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher;
    private static final int MATCH_FIRST = 1;
    private static final int MATCH_SECOND = 2;
    public static final String AUTHORITY = "com.harvic.provider.PeopleContentProvider";
    public static final Uri CONTENT_URI_FIRST = Uri.parse("content://" + AUTHORITY + "/frist");
    public static final Uri CONTENT_URI_SECOND = Uri.parse("content://" + AUTHORITY + "/second");

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, "first", MATCH_FIRST);
        sUriMatcher.addURI(AUTHORITY, "second", MATCH_SECOND);
    }

    private DatabaseHelper mDbHelper;


    @Override
    public boolean onCreate() {
        mDbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)) {
            case MATCH_FIRST:
                // 设置查询的表
                queryBuilder.setTables(DatabaseHelper.TABLE_FIRST_NAME);
                break;

            case MATCH_SECOND:
                queryBuilder.setTables(DatabaseHelper.TABLE_SECOND_NAME);
                break;

            default:
                throw new IllegalArgumentException("Unknow URI: " + uri);
        }

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, null);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)){
            case MATCH_FIRST:{
                long rowID = db.insert(DatabaseHelper.TABLE_FIRST_NAME, null, values);
                if(rowID > 0) {
                    Uri retUri = ContentUris.withAppendedId(CONTENT_URI_FIRST, rowID);
                    return retUri;
                }
            }
            break;
            case MATCH_SECOND:{
                long rowID = db.insert(DatabaseHelper.TABLE_SECOND_NAME, null, values);
                if(rowID > 0) {
                    Uri retUri = ContentUris.withAppendedId(CONTENT_URI_SECOND, rowID);
                    return retUri;
                }
            }
            break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int count = 0;
        switch(sUriMatcher.match(uri)) {
            case MATCH_FIRST:
                count = db.update(DatabaseHelper.TABLE_FIRST_NAME, values, selection, selectionArgs);
                break;
            case MATCH_SECOND:
                count = db.update(DatabaseHelper.TABLE_SECOND_NAME, values, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknow URI : " + uri);
        }
        this.getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
