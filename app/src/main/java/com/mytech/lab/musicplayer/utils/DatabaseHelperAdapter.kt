package com.mytech.lab.musicplayer.utils

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


import java.util.ArrayList

/**
 * Created by lnx on 1/3/18.
 */

class DatabaseHelperAdapter(internal var context: Context) {

    internal var database: DataHelper

    init {
        database = DataHelper(context)
    }

    @JvmOverloads
    fun insert_in_any_table(songname: String, artistname: String, url: String, albumid: String, albumname: String, position: Int, duration: String, count: Int? = null, table_name: String, playlist_name: String? = null): Long {
        var db : SQLiteDatabase?= null;
        try {
            db = database.readableDatabase
            val values = ContentValues()
            values.put(DataHelper.SONG_NAME, songname)
            values.put(DataHelper.ARTIST_NAME, artistname)
            values.put(DataHelper.URL, url)
            values.put(DataHelper.ALBUM_ID, albumid)
            values.put(DataHelper.ALBUM_NAME, albumname)
            values.put(DataHelper.POSITION, position)
            values.put(DataHelper.DURATION, duration)

            if (playlist_name != null) {
                values.put(DataHelper.Playlist_name, playlist_name)
            } else if (table_name.equals("RecentSong", ignoreCase = true) || table_name.equals("favourites", ignoreCase = true)) {
                values.put(DataHelper.COUNT, count)
            }
            return db.insert(table_name, null, values)
        } catch (e: Exception) {
        } finally {
            closedatabase(db)
        }
        return 0

    }

    fun getalldata_table(table_name: String): ArrayList<Song_base> {
        val recent_song = ArrayList<Song_base>()
        var db : SQLiteDatabase?= null;
        try {
            db = database.readableDatabase
            val cursor = db.query(table_name, null, null, null, null, null, DataHelper.COUNT + " DESC")
            if (cursor.moveToFirst()) {
                do {

                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    val song_name = cursor.getString(cursor.getColumnIndexOrThrow("songName"))
                    val artist_name = cursor.getString(cursor.getColumnIndexOrThrow("artistName"))
                    val url = cursor.getString(cursor.getColumnIndexOrThrow("url"))
                    val albumid = cursor.getLong(cursor.getColumnIndexOrThrow("album_id"))
                    val albumname = cursor.getString(cursor.getColumnIndexOrThrow("album_name"))
                    val duration = cursor.getString(cursor.getColumnIndexOrThrow("duration"))

                    val s = Song_base(song_name, artist_name, url, albumid, albumname, duration, "Com", 0, context)
                    recent_song.add(s)

                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
        } finally {
            closedatabase(db)
        }
        return recent_song
    }

    fun countalldata_in_any_table(table_name: String): Int {
        var db : SQLiteDatabase?= null;
        try {
            db = database.readableDatabase
            val str = StringBuffer()
            str.append(" ")
            val col = arrayOf(DataHelper.ID, DataHelper.SONG_NAME, DataHelper.ARTIST_NAME)
            val cursor = db.query(table_name, col, null, null, null, null, null)
            return cursor.count
        } catch (e: Exception) {
        } finally {
            closedatabase(db)
        }
        return 0
    }

    fun getposition_in_table(table_name: String): ArrayList<Int> {
        val pos = ArrayList<Int>()
        var db : SQLiteDatabase?= null;
        try {
            db = database.readableDatabase
            val cursor = db.query(table_name, null, null, null, null, null, DataHelper.COUNT + " DESC")
            if (cursor.moveToFirst()) {
                do {

                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("position"))
                    pos.add(id)
                } while (cursor.moveToNext())
            }

        } catch (e: Exception) {
        } finally {
            closedatabase(db)
        }
        return pos
    }

    fun getalldata_favourite(): ArrayList<Song_base> {
        val recent_song = ArrayList<Song_base>()
        var db : SQLiteDatabase?= null;
        try {
            db = database.readableDatabase

            val cursor = db.query("favourite", null, null, null, null, null, null)
            if (cursor.moveToFirst()) {
                do {

                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    val song_name = cursor.getString(cursor.getColumnIndexOrThrow("songName"))
                    val artist_name = cursor.getString(cursor.getColumnIndexOrThrow("artistName"))
                    val url = cursor.getString(cursor.getColumnIndexOrThrow("url"))
                    val albumid = cursor.getLong(cursor.getColumnIndexOrThrow("album_id"))
                    val albumname = cursor.getString(cursor.getColumnIndexOrThrow("album_name"))
                    val duration = cursor.getString(cursor.getColumnIndexOrThrow("duration"))

                    val s = Song_base(song_name, artist_name, url, albumid, albumname, duration, "Com", 0, context)
                    recent_song.add(s)

                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
        } finally {
            closedatabase(db)
        }
        return recent_song
    }


    @JvmOverloads
    fun checkexists_for_song_in_table(song_name: String?, table_name: String, playlist_nme: String? = null): Int {
        var num = 0
        var db : SQLiteDatabase?= null;
        try {
            db = database.readableDatabase
            if (table_name.equals("RecentSong") || table_name.equals("favourites", ignoreCase = true)) {
                val col = arrayOf(DataHelper.COUNT, DataHelper.SONG_NAME)
                val selectionargs = arrayOf(song_name)
                val cursor = db.query(table_name, col, DataHelper.SONG_NAME + " =? ", selectionargs, null, null, null)
                if (cursor.moveToFirst()) {
                    num = cursor.getInt(cursor.getColumnIndexOrThrow("count"))
                }
            }
        } catch (e: Exception) {

        } finally {
            closedatabase(db)
        }
        return num
    }

    fun checkexist_for_playlist(playlist_nme: String? = null): Int {
        var db : SQLiteDatabase?= null;
        try {
            db = database.readableDatabase
            var num = 0
            val cursor = db.rawQuery("SELECT * FROM Playlist WHERE playlist_name = '" + playlist_nme + "'", null)
            return cursor.count
        } catch (e: Exception) {
        } finally {
            closedatabase(db)
        }
        return 0

    }

    fun updatecount(song_name: String, num: Int): Int {
        var db : SQLiteDatabase?= null;
        try {
            db = database.readableDatabase
            val values = ContentValues()
            values.put(DataHelper.COUNT, num)
            val selectionargs = arrayOf(song_name)
            return db.update(DataHelper.TABLE_NAME, values, DataHelper.SONG_NAME + " =?", selectionargs)
        } catch (e: Exception) {
        } finally {
            closedatabase(db)
        }
        return 0
    }

    fun deletelastrow(): Int {
        var db : SQLiteDatabase?= null;
        try {
            db = database.readableDatabase
            val col = arrayOf(DataHelper.ID)
            var id: Long? = null
            val cursor = db.query(DataHelper.TABLE_NAME, col, null, null, null, null, DataHelper.ID + " LIMIT 1")
            if (cursor.moveToFirst()) {
                id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
            }
            return db.delete(DataHelper.TABLE_NAME, DataHelper.ID + " = '" + id + "'", null)
        } catch (e: Exception) {
        } finally {
            closedatabase(db)
        }
        return 0

    }

    fun deletesong_for_table(name: String, table_name: String): Int {
        var db : SQLiteDatabase?= null;
        try {
            db = database.readableDatabase
            var num = 0
//        val col = arrayOf(DataHelper.ID, DataHelper.SONG_NAME)
//        val selectionargs = arrayOf(name)
//        val cursor = db.query(table_name, col, DataHelper.SONG_NAME + " =? ", selectionargs, null, null, null)
//        if (cursor.moveToFirst())
//        {
//            num = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
//        }
            return db.delete(table_name, DataHelper.SONG_NAME + " = '" + name + "'", null)
        } catch (e: Exception) {
        } finally {
            closedatabase(db)
        }
        return 0

    }

    fun deletesong_for_playlist(song_name: String, sub_playlist: String): Int {
        var db : SQLiteDatabase?= null;
        try {
            db = database.readableDatabase
            var num = 0
            val col = arrayOf(DataHelper.ID, DataHelper.SONG_NAME)
            val selectionargs = arrayOf(song_name, sub_playlist)
            if (sub_playlist.equals("all")) {
                // val cursor = db.query("Playlist", col, DataHelper.SONG_NAME + " =? ", selectionargs, null, null, null)
                return db.delete("Playlist", DataHelper.SONG_NAME + " = '" + song_name + "'", null)
            } else {
                return db.delete("Playlist", DataHelper.SONG_NAME + " =? AND " + DataHelper.Playlist_name + " =? ", selectionargs)
            }
        } catch (e: Exception) {
        } finally {
            closedatabase(db)
        }
        return 0

//        val cursor = db.query("Playlist", col, DataHelper.SONG_NAME + " =? ", selectionargs, null, null, null)
//        if (cursor.moveToFirst())
//        {
//            num = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
//        }


    }

    fun update_position(name: String, position: Int): Int {
        var db : SQLiteDatabase?= null;
        try {
            db = database.readableDatabase
            val values = ContentValues()
            values.put(DataHelper.POSITION, position)
            val selectionargs = arrayOf(name)
            return db.update(DataHelper.TABLE_NAME, values, DataHelper.SONG_NAME + " =?", selectionargs)
        } catch (e: Exception) {
        } finally {
            closedatabase(db)
        }
        return 0

    }

    fun fetchdistinctplaylist(): ArrayList<String> {
        var all_playlist = ArrayList<String>()
        var db : SQLiteDatabase?= null
        try {
            db = database.readableDatabase
            var col = arrayOf(DataHelper.Playlist_name)

            val cursor = db.rawQuery("SELECT DISTINCT playlist_name FROM Playlist;", null)

            if (cursor.moveToFirst()) {
                do {
                    var name: String = cursor.getString(cursor.getColumnIndexOrThrow("playlist_name"))
                    all_playlist.add(name)
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {Log.e("Error",e.message)
        } finally {
            closedatabase(db)
        }


        return all_playlist
    }

    fun getalldata_playlist(playlist_name: String): ArrayList<Song_base> {
        val playlist_song = ArrayList<Song_base>()
        var db : SQLiteDatabase?= null;
        try {
            db = database.readableDatabase
            val cursor = db.query("Playlist", null, DataHelper.Playlist_name + " = '" + playlist_name + "' AND " + DataHelper.SONG_NAME + " != 'sample' ", null, null, null, null)
            if (cursor.moveToFirst()) {
                do {

                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    val song_name = cursor.getString(cursor.getColumnIndexOrThrow("songName"))
                    val artist_name = cursor.getString(cursor.getColumnIndexOrThrow("artistName"))
                    val url = cursor.getString(cursor.getColumnIndexOrThrow("url"))
                    val albumid = cursor.getLong(cursor.getColumnIndexOrThrow("album_id"))
                    val albumname = cursor.getString(cursor.getColumnIndexOrThrow("album_name"))
                    val duration = cursor.getString(cursor.getColumnIndexOrThrow("duration"))


                    val s = Song_base(song_name, artist_name, url, albumid, albumname, duration, "Com", 0, context)
                    playlist_song.add(s)

                } while (cursor.moveToNext())
            }

        } catch (e: Exception) {
        } finally {
            closedatabase(db)
        }
        return playlist_song;
    }


    fun deleteplaylist(playlist: String): Int {
        var db : SQLiteDatabase?= null;
        try {
            db = database.readableDatabase
            return db.delete("Playlist", DataHelper.Playlist_name + " = '" + playlist + "'", null)
        } catch (e: Exception) {
        } finally {
            closedatabase(db)
        }
        return 0
    }

    fun checkexistance_of_song_in_playlist(song_name: String, playlist: String): Int {
        var db : SQLiteDatabase?= null;
        try {
            var num = 0
            db = database.readableDatabase
            val col = arrayOf(DataHelper.SONG_NAME)
            val selectionargs = arrayOf(song_name)
            val cursor = db.rawQuery("SELECT id FROM Playlist WHERE songName = '" + song_name + "' AND playlist_name = '" + playlist + "' ", null)
            return cursor.count
        } catch (e: Exception) {
        } finally {
            closedatabase(db)
        }
        return 0
    }

    internal class DataHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, VERSION) {

        override fun onCreate(db: SQLiteDatabase) {

            try {
                db.execSQL(CREATE_TABLE)
                db.execSQL(CREATE_TABLE1)
                db.execSQL(CREATE_TABLE2)
                //                Toast.makeText(context,"table create",Toast.LENGTH_SHORT).show();
            } catch (e: Exception) {
            }finally {

            }


        }

        override fun onUpgrade(db: SQLiteDatabase, i: Int, i1: Int) {

            //            Toast.makeText(context,"ENter in upgrade",Toast.LENGTH_SHORT).show();
            try {
                db.execSQL(DROP_TABLE)
                db.execSQL(DROP_TABLE1)
                db.execSQL(DROP_TABLE2)
                onCreate(db)
                //                Toast.makeText(context,"upgrade success",Toast.LENGTH_SHORT).show();

            } catch (e: Exception) {
            }

        }

        companion object {

            internal val DATABASE_NAME = "Localinfo.db"
            internal val TABLE_NAME = "RecentSong"
            internal val TABLE_NAME1 = "Playlist"
            internal val TABLE_NAME2 = "favourites"
            internal val Playlist_name = "playlist_name"
            internal val ID = "id"
            internal val SONG_NAME = "songName"
            internal val ARTIST_NAME = "artistName"
            internal val URL = "url"
            internal val VERSION = 24
            internal val ALBUM_ID = "album_id"
            internal val POSITION = "position"
            internal val ALBUM_NAME = "album_name"
            internal val DURATION = "duration"
            internal val COUNT = "count"
            internal val CREATE_TABLE = "create table IF NOT EXISTS $TABLE_NAME ($ID INTEGER PRIMARY KEY AUTOINCREMENT,$SONG_NAME TEXT,$ARTIST_NAME TEXT,$URL TEXT,$ALBUM_ID TEXT, $ALBUM_NAME TEXT,$POSITION INT,$DURATION TEXT, $COUNT INT);"
            internal val CREATE_TABLE1 = "create table IF NOT EXISTS $TABLE_NAME1 ($ID INTEGER PRIMARY KEY AUTOINCREMENT,$SONG_NAME TEXT,$ARTIST_NAME TEXT,$URL TEXT,$ALBUM_ID TEXT, $ALBUM_NAME TEXT,$POSITION INT,$DURATION TEXT, $Playlist_name TEXT);"
            internal val CREATE_TABLE2 = "create table IF NOT EXISTS $TABLE_NAME2 ($ID INTEGER PRIMARY KEY AUTOINCREMENT,$SONG_NAME TEXT,$ARTIST_NAME TEXT,$URL TEXT,$ALBUM_ID TEXT, $ALBUM_NAME TEXT,$POSITION INT,$DURATION TEXT,$COUNT INT);"
            internal val DROP_TABLE = "drop table IF EXISTS " + TABLE_NAME
            internal val DROP_TABLE1 = "drop table IF EXISTS " + TABLE_NAME1
            internal val DROP_TABLE2 = "drop table IF EXISTS " + TABLE_NAME2
        }

    }

    fun closedatabase(db: SQLiteDatabase?) {
        db?.close()
    }


}
