package com.example.thenewsapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thenewsapp.models.Article
import com.example.thenewsapp.models.NewsResponse
import com.example.thenewsapp.repository.NewsRepository
import com.example.thenewsapp.utils.Resourse
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(application: Application,val newsRepository:NewsRepository) : AndroidViewModel(application) {

    val headLines: MutableLiveData<Resourse<NewsResponse>> = MutableLiveData()
    var headLinesPage = 1
    var headLinesResponse: NewsResponse? = null

    val searchNews: MutableLiveData<Resourse<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null
    var newSearchQuery: String? = null
    var oldSearchQuery: String? = null

    init {
        getHeadLines("us")
    }

    fun getHeadLines(countryCode: String)=viewModelScope.launch {
        headLinesInternet(countryCode)
    }

    fun searchNews(searchQuery:String)=viewModelScope.launch {
        searchNewsInternet(searchQuery)
    }

    private fun handleHeadLinesResponse(response: Response<NewsResponse>): Resourse<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                headLinesPage++
                if (headLinesResponse == null ) {
                    headLinesResponse = resultResponse
                } else {
                    val oldArticles = headLinesResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resourse.Success(headLinesResponse ?: resultResponse)
            }
        }
        return Resourse.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>):Resourse<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let { resultResponse->
                if(searchNewsResponse==null||newSearchQuery!=oldSearchQuery){
                    searchNewsPage=1
                    oldSearchQuery = newSearchQuery
                    searchNewsResponse = resultResponse
                }else{
                    searchNewsPage++
                    val oldArticals=searchNewsResponse?.articles
                    val newArticals=resultResponse.articles
                    oldArticals?.addAll(newArticals)
                }
                return Resourse.Success(searchNewsResponse?:resultResponse)
            }
        }
        return Resourse.Error(response.message())
    }
    fun addToFavourite(article: Article)=viewModelScope.launch {
        newsRepository.insertArticle(article)
    }

    fun getFavouriteNews()=newsRepository.getFavouriteArtical()
    fun deleteArtical(article: Article)=viewModelScope.launch {
        newsRepository.deleteArtical(article)
    }

    fun internetConnection(context:Context):Boolean{
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
            return getNetworkCapabilities(activeNetwork)?.run {
                when{
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ->true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)->true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)->true
                    else->false
                }

            }?: false
        }
    }


    private suspend fun headLinesInternet(countryCode:String){
        headLines.postValue(Resourse.Loading())
        try{
            if(internetConnection(this.getApplication())){
                val response=newsRepository.getHeadLines(countryCode,headLinesPage)
                headLines.postValue(handleHeadLinesResponse(response))
            }else{
                headLines.postValue(Resourse.Error("No internet connection"))
            }
        }catch (t:Throwable){
            when(t){
                is IOException->headLines.postValue(Resourse.Error("unable to connect"))
                else->headLines.postValue(Resourse.Error("no signal"))

            }
        }
    }

    private suspend fun searchNewsInternet(searchQuery:String){
        newSearchQuery=searchQuery
        searchNews.postValue(Resourse.Loading())
        try{
            if(internetConnection(this.getApplication())){
                val response=newsRepository.searchNews(searchQuery,searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            }else{
                searchNews.postValue(Resourse.Error("No internet connection"))
            }
        }catch (t:Throwable){
            when(t){
                is IOException->searchNews.postValue(Resourse.Error("unable to connect"))
                else->searchNews.postValue(Resourse.Error("no signal"))

            }
        }
    }
}
