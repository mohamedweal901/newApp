package com.example.thenewsapp.utils

sealed class Resourse<T>(
    val date:T?=null,
    val message:String?=null,
){

    class Success<T>(date: T):Resourse<T>(date)
    class Error<T>(message: String,date: T?=null):Resourse<T>(date,message)
    class Loading<T>:Resourse<T>()


}