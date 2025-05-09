package com.example.thenewsapp.repository

import com.example.thenewsapp.api.RetrofitInstanse
import com.example.thenewsapp.database.ArticleDatabase
import com.example.thenewsapp.models.Article

class NewsRepository(val db: ArticleDatabase){

    suspend fun getHeadLines(countryCode:String,pageNumber:Int)=
        RetrofitInstanse.api.getHeadLines(countryCode,pageNumber)

    suspend fun searchNews(searchQuery:String,pageNumber: Int)=
        RetrofitInstanse.api.searchForNews(searchQuery,pageNumber)

    suspend fun insertArticle(article: Article)=db.getArticleDao().insert(article)
    fun getFavouriteArtical()=db.getArticleDao().getAllArticles()
    suspend fun deleteArtical(article: Article)=db.getArticleDao().deleteArticle(article)

}