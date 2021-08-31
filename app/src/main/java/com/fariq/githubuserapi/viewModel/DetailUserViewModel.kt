package com.fariq.githubuserapi.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fariq.githubuserapi.model.User
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class DetailUserViewModel : ViewModel() {
    val userData = MutableLiveData<User>()
    fun setUserDetail(username: String) {

        val url = "https://api.github.com/users/$username"
        val client = AsyncHttpClient()
        client.addHeader("Authorization", "be4d189997ddeaa9951a3298fe05b227c6a1f886")
        client.addHeader("User-Agent", "request")
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                try {

                    //parsing json
                    val user = User()
                    val result = String(responseBody)
                    val userObject = JSONObject(result)
                    user.username = userObject.getString("login")
                    user.avatar = userObject.getString("avatar_url")
                    user.company = userObject.getString("company")
                    user.location = userObject.getString("location")
                    user.following = userObject.getInt("following")
                    user.followers = userObject.getInt("followers")
                    userData.postValue(user)
                } catch (e: Exception) {
                    Log.d("Exception", e.message.toString())
                }
            }
            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                Log.d("onFailure", error.message.toString())
            }
        })
    }
    fun getUserDetail(): LiveData<User> {
        return userData
    }
}