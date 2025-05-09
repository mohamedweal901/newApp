package com.example.thenewsapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.thenewsapp.models.Article


@Database(
    entities = [Article::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class ArticleDatabase:RoomDatabase() {
    abstract fun getArticleDao():ArticleDao
companion object{
    val DATABASE_NAME="article_db.db"
    @Volatile private var instanse:ArticleDatabase?=null

    private val Lock=Any()
    operator fun invoke(context:Context)= instanse?: synchronized(Lock){
        instanse?:createDatabase(context).also{ instanse=it}

    }

    private fun createDatabase(context: Context)= Room.databaseBuilder(
        context.applicationContext,
        ArticleDatabase::class.java,
        DATABASE_NAME
    ).build()
}
}