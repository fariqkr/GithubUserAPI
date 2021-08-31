package com.fariq.githubuserapi


import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fariq.githubuserapi.adapter.UserAdapter
import com.fariq.githubuserapi.databinding.ActivityMainBinding
import com.fariq.githubuserapi.model.User
import com.fariq.githubuserapi.ui.DetailUserActivity
import com.fariq.githubuserapi.viewModel.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: UserAdapter
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    lateinit var search: SearchView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = UserAdapter()
        adapter.notifyDataSetChanged()
        binding.rvUser.layoutManager = LinearLayoutManager(this)
        binding.rvUser.adapter = adapter

        if (!isInternetAvailable(this)){
            Toast.makeText(this, resources.getString(R.string.network_not_available), Toast.LENGTH_SHORT).show()
        }

        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isNotEmpty()) {
                    if (!isInternetAvailable(applicationContext)) {
                        Toast.makeText(applicationContext, resources.getString(R.string.network_not_available), Toast.LENGTH_SHORT).show()
                    } else {
                        showLoading(true)
                        mainViewModel.setListUser(query)
                    }
                }
                closeKeyBoard()
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                if (query.isNotEmpty()) {
                    if (!isInternetAvailable(applicationContext)) {
                        Toast.makeText(applicationContext, resources.getString(R.string.network_not_available), Toast.LENGTH_SHORT).show()
                    } else {
                        mainViewModel.setListUser(query)
                        showLoading(true)
                    }
                }
                return false
            }

        })

        binding.search.setOnCloseListener {
            closeKeyBoard()
            showLoading(false)
            adapter.setData(ArrayList())
            true
        }

        mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
            MainViewModel::class.java)
        mainViewModel.getUsers().observe(this, { userItems ->
            showLoading(false)
            if (userItems != null) {
                adapter.setData(userItems)
                if (userItems.size == 0){
                    Toast.makeText(applicationContext, resources.getString(R.string.user) + resources.getString(R.string.not_found),
                        Toast.LENGTH_SHORT).show()
                }
            }
        })

        adapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
            override fun onItemClicked(user: User) {
                val intent = Intent(this@MainActivity, DetailUserActivity::class.java)
                intent.putExtra("user", user)
                startActivity(intent)
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_change_settings) {
            val mIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(mIntent)
        }
        return super.onOptionsItemSelected(item)
    }
    private fun showLoading(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
    private fun closeKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun isInternetAvailable(context: Context): Boolean {
        var result = false
        val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                    connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }

                }
            }
        }

        return result
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


}

