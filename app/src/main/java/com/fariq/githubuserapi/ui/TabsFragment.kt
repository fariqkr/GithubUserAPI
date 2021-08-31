package com.fariq.githubuserapi.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fariq.githubuserapi.adapter.UserAdapter
import com.fariq.githubuserapi.databinding.FragmentTabsBinding
import com.fariq.githubuserapi.model.User
import com.fariq.githubuserapi.viewModel.TabsViewModel

class TabsFragment : Fragment() {
    private lateinit var adapter: UserAdapter
    private lateinit var tabsViewModel: TabsViewModel
    private var _binding: FragmentTabsBinding? = null
    private var username : String? = null

    companion object {
        private const val ARG_USERNAME = "username"
        private const val ARG_SECTION_NUMBER = "section_number"

        @JvmStatic
        fun newInstance(position : Int, username: String) =
            TabsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, position)
                    putString(ARG_USERNAME, username)
                }
            }
    }

    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTabsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val index = arguments?.getInt(ARG_SECTION_NUMBER, 0)
        Log.d("a", index.toString())
        val section = if (index == 0) "followers" else "following"
        username = arguments?.getString(ARG_USERNAME,"")
        tabsViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
            TabsViewModel::class.java)
        showLoading(true)
        tabsViewModel.setListUser(username!!, section)

        tabsViewModel.getUsers().observe(viewLifecycleOwner, { userItems ->
            if (userItems != null) {
                adapter.setData(userItems)
                showLoading(false)
                if (userItems.size == 0){
                    binding.txtInfo.text = "0 $section"
                }
            }
        })


        adapter = UserAdapter()
        adapter.notifyDataSetChanged()
        binding.rvListUser.layoutManager = LinearLayoutManager(context)
        binding.rvListUser.adapter = adapter
        adapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
            override fun onItemClicked(user: User) {

            }

        })

    }
    private fun showLoading(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}