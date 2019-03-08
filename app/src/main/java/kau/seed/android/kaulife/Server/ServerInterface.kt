package kau.seed.android.kaulife.Server

import kau.seed.android.kaulife.TimeTableAlarm.Class.ClassData
import retrofit2.Call
import retrofit2.http.GET

interface ServerInterface {
    @GET("/class/classes")
    fun getClass(): Call<ArrayList<ClassData>>
}