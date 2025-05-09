package com.example.thenewsapp.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thenewsapp.R
import com.example.thenewsapp.adapters.NewsAdapters
import com.example.thenewsapp.databinding.FragmentFavoritesBinding
import com.example.thenewsapp.ui.NewsActivity
import com.example.thenewsapp.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class FavoritesFragment : Fragment(R.layout.fragment_favorites) {
    lateinit var newsViewModel: NewsViewModel
    lateinit var newsAdapters: NewsAdapters
    lateinit var binding:FragmentFavoritesBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding=FragmentFavoritesBinding.bind(view)



        newsViewModel=(activity as NewsActivity).newsViewModel
        setupFavouriteRecycler()
        newsAdapters.setOnItemClickListener {
            val bundle=Bundle()
            bundle.apply {
                putSerializable("article",it)
            }
            findNavController().navigate(R.id.action_favoritesFragment_to_articleFragment,bundle)
        }


        val itemTouchHelperCallback= object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN , ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }


            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position=viewHolder.adapterPosition
                val article=newsAdapters.differ.currentList[position]
                newsViewModel.deleteArtical(article)
                Snackbar.make(view,"removed from favourite",Snackbar.LENGTH_LONG).apply {
                    setAction("undo"){
                        newsViewModel.addToFavourite(article)
                    }
                    show()

                }

            }

        }
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.recyclerFavourites)
        }
        newsViewModel.getFavouriteNews().observe(viewLifecycleOwner, Observer {article->
            newsAdapters.differ.submitList(article)
        })
    }
    private fun setupFavouriteRecycler(){
        newsAdapters = NewsAdapters()
        binding.recyclerFavourites.apply {
            adapter = newsAdapters
            layoutManager = LinearLayoutManager(activity)
        }
    }



}