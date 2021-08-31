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

class MainViewModel : ViewModel() {
    val listUser = MutableLiveData<ArrayList<User>>()

    fun setListUser(username: String) {
        val listItems = ArrayList<User>()
        val url = "https://api.github.com/search/users?q=$username"
        val client = AsyncHttpClient()
        client.addHeader("Authorization", "f95f5280dc8ff3f867c3fbc88e029cb56d645898")
        client.addHeader("User-Agent", "request")
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                try {
                    //parsing json
                    val result = String(responseBody)
                    val responseObject = JSONObject(result)
                    val list = responseObject.getJSONArray("items")
                    for (i in 0 until list.length()) {
                        val user = list.getJSONObject(i)
                        val userItems = User()
                        userItems.username = user.getString("login")
                        userItems.avatar = user.getString("avatar_url")

                        listItems.add(userItems)
                    }
                    listUser.postValue(listItems)
                } catch (e: Exception) {
                    Log.d("Exception", e.message.toString())
                }
            }
            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                Log.d("onFailure", error.message.toString())
            }
        })
    }
    fun getUsers(): LiveData<ArrayList<User>> {
        return listUser
    }

}