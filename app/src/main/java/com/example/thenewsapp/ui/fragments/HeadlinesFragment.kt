package com.example.thenewsapp.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thenewsapp.R
import com.example.thenewsapp.adapters.NewsAdapters
import com.example.thenewsapp.databinding.FragmentHeadlinesBinding
import com.example.thenewsapp.ui.NewsActivity
import com.example.thenewsapp.ui.NewsViewModel
import com.example.thenewsapp.utils.Constants
import com.example.thenewsapp.utils.Resourse
import org.w3c.dom.Text

class HeadlinesFragment : Fragment(R.layout.fragment_headlines) {


    lateinit var newsViewModel: NewsViewModel
    lateinit var newAdapter: NewsAdapters
    lateinit var retryButton: Button
    lateinit var errorText: TextView
    lateinit var itemHeadLineError: CardView
    lateinit var binding: FragmentHeadlinesBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHeadlinesBinding.bind(view)
        itemHeadLineError=view.findViewById(R.id.itemHeadlinesError)
        val inflater=requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view:View=inflater.inflate(R.layout.item_error,null)

        retryButton=view.findViewById(R.id.retryButton)
        errorText=view.findViewById(R.id.errorText)
        newsViewModel=(activity as NewsActivity).newsViewModel
        setupHeadlinesRecycler()
        newAdapter.setOnItemClickListener {
            val bundle=Bundle()
            bundle.apply {
                putSerializable("article",it)
            }
            findNavController().navigate(R.id.action_headlinesFragment2_to_articleFragment,bundle)
        }
        newsViewModel.headLines.observe(viewLifecycleOwner) { response ->
            when(response){
                is Resourse.Error<*>-> {
                    hideProgrssBar()
                    response.message?.let { message->
                        Toast.makeText(activity,"An error occured:$message",Toast.LENGTH_LONG).show()
                        showErrorMessage(message)
                    }

                }
                is Resourse.Loading<*>-> {
                    showProgressBar()

                }
                is Resourse.Success<*>-> {
                    hideProgrssBar()
                    hideErrorMessage()
                    response.date?.let { newsResponse ->
                        newAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages=newsResponse.totalResults / Constants.QUERY_PAGE_SIZE +2
                        isLastPage=newsViewModel.headLinesPage==totalPages
                        if(isLastPage){
                            binding.recyclerHeadlines.setPadding(0,0,0,0)
                        }

                    }
                }
            }


            retryButton.setOnClickListener{
                newsViewModel.getHeadLines("us")
            }
        }

    }

    var isError = false
    var isLoading = false
    var isLastPage = false
    var isScrolling = false


    private fun hideProgrssBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideErrorMessage() {
        itemHeadLineError.visibility = View.INVISIBLE
        isError = false
    }

    private fun showErrorMessage(message: String) {
        itemHeadLineError.visibility = View.VISIBLE
        errorText.text = message
        isError = true
    }

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibelItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            val isNoErrors = !isError
            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibelItemCount >= totalItemCount
            val isNotAtBeging = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPagingnate =
                isNoErrors && isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeging && isTotalMoreThanVisible && isScrolling

            if (shouldPagingnate) {
                newsViewModel.getHeadLines("us")
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling == true
            }
        }
    }
    private fun setupHeadlinesRecycler() {
        newAdapter = NewsAdapters()
        binding.recyclerHeadlines.apply {
            adapter = newAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@HeadlinesFragment.scrollListener)

        }
    }
}