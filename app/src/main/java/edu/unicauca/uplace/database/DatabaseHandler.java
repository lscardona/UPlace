package edu.unicauca.uplace.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import edu.unicauca.uplace.models.UPlaceModel;

public class DatabaseHandler extends SQLiteOpenHelper {

    private final int DATABASE_VERSION = 1;
    private final String DATABASE_NAME = "UPlaceDatabase2";
    private final String TABLE_UPLACE = "UPlaceTable";


    private String KEY_ID ="_id" ;
    private String KEY_TITLE = "title" ;
    private String KEY_IMAGE = "image";
    private String KEY_DESCRIPTION = "description" ;
    private String KEY_DATE = "date" ;
    private String KEY_LOCATION = "location";
    private String KEY_LATITUDE = "latitude" ;
    private String KEY_LONGITUDE = "longitude" ;


    public DatabaseHandler(@Nullable Context context) {
        super(context, "UPlaceDatabase2", null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Crear tabla con campos
        String CREATE_UPLACE_TABLE = ("CREATE TABLE "+ TABLE_UPLACE+ "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_IMAGE + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_LOCATION + " TEXT,"
                + KEY_LATITUDE + " TEXT,"
                + KEY_LONGITUDE + " TEXT)"
        );
        db.execSQL(CREATE_UPLACE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_UPLACE);
        onCreate(db);
    }

    public  long addPlace(UPlaceModel place){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_TITLE, place.title);// UPlace TITLE
        contentValues.put(KEY_IMAGE, place.image); // UPlace IMAGE
        contentValues.put(KEY_DESCRIPTION, place.description); // UPlace DESCRIPTION
        contentValues.put(KEY_DATE, place.date);// UPlace DATE
        contentValues.put(KEY_LOCATION, place.location);// UPlace LOCATION
        contentValues.put(KEY_LATITUDE, place.latitude);// UPlace LATITUDE
        contentValues.put(KEY_LONGITUDE, place.longitude);// UPlace LONGITUDE


        //Insertar una fila
        long result = db.insert(TABLE_UPLACE,null,contentValues);
        db.close();

        return result;

    }

    public  long updatePlace(UPlaceModel place){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_TITLE, place.title);// UPlace TITLE
        contentValues.put(KEY_IMAGE, place.image); // UPlace IMAGE
        contentValues.put(KEY_DESCRIPTION, place.description); // UPlace DESCRIPTION
        contentValues.put(KEY_DATE, place.date);// UPlace DATE
        contentValues.put(KEY_LOCATION, place.location);// UPlace LOCATION
        contentValues.put(KEY_LATITUDE, place.latitude);// UPlace LATITUDE
        contentValues.put(KEY_LONGITUDE, place.longitude);// UPlace LONGITUDE


        //Insertar una fila
        long result = db.update(TABLE_UPLACE,contentValues, KEY_ID + "="+ place.id,null);
        db.close();

        return result;
    }



    public ArrayList<UPlaceModel> getPlaces(){
        ArrayList<UPlaceModel> uPlaceList = new ArrayList<>();
        String query = "SELECT * FROM "+TABLE_UPLACE;

        SQLiteDatabase db = this.getReadableDatabase();
        try{

            Cursor cursor = db.rawQuery(query,null);

            if(cursor.moveToFirst()){
                do {
                    @SuppressLint("Range") UPlaceModel  place = new UPlaceModel(
                            cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                            cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                            cursor.getString(cursor.getColumnIndex(KEY_IMAGE)),
                            cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                            cursor.getString(cursor.getColumnIndex(KEY_DATE)),
                            cursor.getString(cursor.getColumnIndex(KEY_LOCATION)),
                            cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)),
                            cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE))
                    );
                    uPlaceList.add(place);

                }while (cursor.moveToNext());
            }
            cursor.close();
        }catch (SQLiteException e){
            e.printStackTrace();
            return  new ArrayList<UPlaceModel>();
        }
        return uPlaceList;
    }


    public int deletePlace(UPlaceModel uPlaceModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        int success  = db.delete(TABLE_UPLACE,KEY_ID+" = "+uPlaceModel.id,null);
        db.close();
        return success;
    }
}
