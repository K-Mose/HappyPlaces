package com.example.happyplaces.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.happyplaces.models.HappyPlaceModel

class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    companion object{
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "HappyPlaceDatabase"
        private const val TABLE_HAPPY_PLACE = "HappyPlaceTable"

        private const val KEY_ID = "id"
        private const val KEY_TITLE: String = "title"
        private const val KEY_IMAGE: String = "image"
        private const val KEY_DESCRIPTION: String = "description"
        private const val KEY_DATE = "date"
        private const val KEY_LOCATION = "location"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_HAPPY_PLACE_TABLE = ("create table if not exists "+ TABLE_HAPPY_PLACE+"("
                + KEY_ID + " integer primary key,"
                + KEY_TITLE + " text,"
                + KEY_IMAGE + " text,"
                + KEY_DESCRIPTION + " text"
                + KEY_LOCATION + " text"
                + KEY_LATITUDE + " text"
                + KEY_LONGITUDE + " text )")
        db?.execSQL(CREATE_HAPPY_PLACE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("Drop table if exists $TABLE_HAPPY_PLACE")
        onCreate(db)
    }

    fun addHappyPlace(happyplace: HappyPlaceModel): Long{
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_DATE, happyplace.date)
        contentValues.put(KEY_DESCRIPTION, happyplace.description)
        contentValues.put(KEY_IMAGE, happyplace.image)
        contentValues.put(KEY_LATITUDE, happyplace.latitude.toString())
        contentValues.put(KEY_LOCATION, happyplace.location)
        contentValues.put(KEY_LONGITUDE, happyplace.longitude.toString())
        contentValues.put(KEY_TITLE, happyplace.title)

        val success = db.insert(TABLE_HAPPY_PLACE, null, contentValues)

        db.close()
        return success
    }

}