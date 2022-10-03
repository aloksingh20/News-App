package com.example.newsapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.AttrRes
import android.support.annotation.ColorInt
import android.util.TypedValue
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapplication.adapter.CustomAdapter
import com.example.newsapplication.architecture.NewsModel
import com.example.newsapplication.architecture.NewsViewModel

class SavedNewsActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: NewsViewModel
    private lateinit var newsData: MutableList<NewsModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_news)
        recyclerView = findViewById(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        newsData = mutableListOf()

        val adapter = CustomAdapter(newsData)


        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProvider(this)[NewsViewModel::class.java]

//        Get Saved News
        viewModel.getNewsFromDB(context = applicationContext)?.observe(this) {
            newsData.clear()
            newsData.addAll(it)
            adapter.notifyDataSetChanged()
        }

        adapter.setOnItemClickListener(object : CustomAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {

                val intent = Intent(this@SavedNewsActivity, ReadNewsActivity::class.java)
                intent.putExtra(MainActivity.NEWS_URL, newsData[position].url)
                intent.putExtra(MainActivity.NEWS_TITLE, newsData[position].headLine)
                intent.putExtra(MainActivity.NEWS_IMAGE_URL, newsData[position].image)
                intent.putExtra(MainActivity.NEWS_DESCRIPTION, newsData[position].description)
                intent.putExtra(MainActivity.NEWS_SOURCE, newsData[position].source)
                intent.putExtra(MainActivity.NEWS_PUBLICATION_TIME, newsData[position].time)
                intent.putExtra(MainActivity.NEWS_CONTENT, newsData[position].content)
                startActivity(intent)

            }
        })

        adapter.setOnItemLongClickListener(object : CustomAdapter.OnItemLongClickListener {
            override fun onItemLongClick(position: Int) {
//              delete news
                recyclerView.findViewHolderForAdapterPosition(position)?.itemView?.setBackgroundColor(
                    getThemeColor(androidx.constraintlayout.widget.R.attr.theme)
                )

                val alertBuilder: AlertDialog.Builder = AlertDialog.Builder(this@SavedNewsActivity)
                alertBuilder.setMessage("Delete this News?")
                alertBuilder.setTitle("Alert!")
                alertBuilder.setCancelable(false)

                alertBuilder.setPositiveButton(
                    "Yes"
                ) { dialog, which ->
                    this@SavedNewsActivity.let {
                        viewModel.deleteNews(
                            it,
                            news = newsData[position]
                        )
                    }
                    adapter.notifyItemRemoved(position)
                    Toast.makeText(this@SavedNewsActivity, "Deleted!", Toast.LENGTH_SHORT)
                        .show()
                }
                alertBuilder.setNegativeButton(
                    "No"
                ) { dialog, which ->
                    recyclerView.findViewHolderForAdapterPosition(position)?.itemView?.setBackgroundColor(
                        getThemeColor(androidx.appcompat.R.attr.theme)
                    )
                }

                val alertDialog = alertBuilder.create()
                alertDialog.show()


            }
        })

        recyclerView.adapter = adapter

    }

    @ColorInt
    fun Context.getThemeColor(@AttrRes attribute: Int) = TypedValue().let {
        theme.resolveAttribute(attribute, it, true)
        it.data
    }

}