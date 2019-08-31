package com.app.instagramapp.DB

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build.ID
import android.util.Log
import com.app.instagramapp.model.Comment
import com.app.instagramapp.model.Post
import java.util.ArrayList
import javax.xml.datatype.DatatypeConstants.DATETIME

class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, DatabaseHandler.DB_NAME, null, DatabaseHandler.DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TABLE = "CREATE TABLE $TABLE_NAME ($ID INTEGER PRIMARY KEY, $NAME TEXT,$DESC TEXT,$IMAGEPATH TEXT,$DATETIME DATETIME DEFAULT CURRENT_TIMESTAMP);"

        val CREATE_TABLE_COM = "CREATE TABLE $TABLE_NAME_COMM ($ID INTEGER PRIMARY KEY, $LABEL TEXT,$POSTID INTEGER);"
        db.execSQL(CREATE_TABLE)
        db.execSQL(CREATE_TABLE_COM)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
        val DROP_TABLE1 = "DROP TABLE IF EXISTS $TABLE_NAME_COMM"
        db.execSQL(DROP_TABLE)
        db.execSQL(DROP_TABLE1)
        onCreate(db)
    }

    fun addPost(post: Post): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(NAME, post.name)
        values.put(DESC, post.desc)
        values.put(IMAGEPATH, post.imagepath)
        val _success = db.insert(TABLE_NAME, null, values)
        db.close()
        Log.v("InsertedId", "$_success")
        return (Integer.parseInt("$_success") != -1)
    }
    fun addComment(comment: Comment): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(LABEL, comment.label)
        values.put(POSTID, comment.postId)
        val _success = db.insert(TABLE_NAME_COMM, null, values)
        db.close()
        Log.v("InsertedIdComment", "$_success")
        return (Integer.parseInt("$_success") != -1)
    }

    fun getPost(_id: Int): Post {
        val post = Post()
        val db = writableDatabase
        val selectQuery = "SELECT  * FROM $TABLE_NAME WHERE $ID = $_id"
        val cursor = db.rawQuery(selectQuery, null)

        cursor?.moveToFirst()
        post.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ID)))
        post.name = cursor.getString(cursor.getColumnIndex(NAME))
        post.desc = cursor.getString(cursor.getColumnIndex(DESC))
        post.imagepath = cursor.getString(cursor.getColumnIndex(IMAGEPATH))
        post.datetime = cursor.getString(cursor.getColumnIndex(DATETIME))
        cursor.close()
        return post
    }

    fun getCommentByPost(_postId: Int): List<Comment> {
        val commentList = ArrayList<Comment>()
        val db = writableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NAME_COMM WHERE $POSTID = $_postId  ORDER BY $ID_COM ASC"
        val cursor = db.rawQuery(selectQuery, null)


        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val comment = Comment()
                    comment.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ID_COM)))
                    comment.label = cursor.getString(cursor.getColumnIndex(LABEL))
                    comment.postId = cursor.getInt(cursor.getColumnIndex(POSTID))
                    commentList.add(comment)
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        return commentList
    }

    fun getCommentCount(_postId: Int): Int {
        val db = writableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NAME_COMM WHERE $POSTID = $_postId"
        val cursor = db.rawQuery(selectQuery, null)
        val count = cursor.count
        cursor.close()
        return count
    }

    fun getLatestComment(_postId: Int): Comment {
        val comment = Comment()
        val db = writableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NAME_COMM WHERE $POSTID = $_postId  ORDER BY $ID_COM DESC LIMIT 0,1"
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    comment.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ID_COM)))
                    comment.label = cursor.getString(cursor.getColumnIndex(LABEL))
                    comment.postId = cursor.getInt(cursor.getColumnIndex(POSTID))
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        return comment
    }

    fun post(): List<Post> {
        val taskList = ArrayList<Post>()
        val db = writableDatabase
        val selectQuery = "SELECT  * FROM $TABLE_NAME ORDER BY $ID DESC"
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val post = Post()
                    post.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ID)))
                    post.name = cursor.getString(cursor.getColumnIndex(NAME))
                    post.desc = cursor.getString(cursor.getColumnIndex(DESC))
                    post.imagepath = cursor.getString(cursor.getColumnIndex(IMAGEPATH))
                    post.datetime = cursor.getString(cursor.getColumnIndex(DATETIME))
                    taskList.add(post)
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        return taskList
    }


    fun updatePost(post: Post): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(NAME, post.name)
        values.put(DESC, post.desc)
        values.put(IMAGEPATH, post.imagepath)
        val _success = db.update(TABLE_NAME, values, ID + "=?", arrayOf(post.id.toString())).toLong()
        db.close()
        return Integer.parseInt("$_success") != -1
    }

    fun deletePost(_id: Int): Boolean {
        val db = this.writableDatabase
        val _success = db.delete(TABLE_NAME, ID + "=?", arrayOf(_id.toString())).toLong()
        db.close()
        return Integer.parseInt("$_success") != -1
    }

    fun deleteAllPost(): Boolean {
        val db = this.writableDatabase
        val _success = db.delete(TABLE_NAME, null, null).toLong()
        db.close()
        return Integer.parseInt("$_success") != -1
    }

    companion object {
        private val DB_VERSION = 1
        private val DB_NAME = "MyPosts"
        private val TABLE_NAME = "Post"
        private val ID = "Id"
        private val NAME = "Name"
        private val DESC = "Desc"
        private val IMAGEPATH = "Imagepath"
        private val DATETIME = "Timestamp"

        private val TABLE_NAME_COMM = "Comment"
        private val ID_COM = "Id"
        private val LABEL = "label"
        private val POSTID = "Postid"
    }
}