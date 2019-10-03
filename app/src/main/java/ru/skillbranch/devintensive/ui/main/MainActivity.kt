package ru.skillbranch.devintensive.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.resolveColor
import ru.skillbranch.devintensive.models.data.ChatType
import ru.skillbranch.devintensive.ui.adapters.ChatAdapter
import ru.skillbranch.devintensive.ui.adapters.ChatItemTouchHelperCallback
import ru.skillbranch.devintensive.ui.archive.ArchiveActivity
import ru.skillbranch.devintensive.ui.group.GroupActivity
import ru.skillbranch.devintensive.viewmodels.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initToolbar()
        initViews()
        initViewModel()
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
    }

    private fun initViews() {
        val mainActivity = this
        chatAdapter = ChatAdapter {
            //Snackbar.make(rv_chat_list, "Click on ${it.title}", Snackbar.LENGTH_LONG).show()
            val snackbar = Snackbar.make(rv_chat_list, "Click on ${it.title}", Snackbar.LENGTH_LONG)
            with(snackbar.view){
                setBackgroundColor(R.attr.colorSnackbarBackground.resolveColor(mainActivity))
                val textView =  findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                textView.setTextColor(R.attr.colorSnackbarTextColor.resolveColor(mainActivity))
            }
            snackbar.show()
            if (it.chatType == ChatType.ARCHIVE) {
                val intent = Intent(this, ArchiveActivity::class.java)
                startActivity(intent)
            }
        }
        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        val touchCallback= ChatItemTouchHelperCallback(chatAdapter) {
            val idChatItem = it.id
            viewModel.addToArchive(idChatItem)
            val snackbar = Snackbar.make(rv_chat_list, "Вы точно хотите добавить ${it.title} в архив?", Snackbar.LENGTH_LONG)
            snackbar.setAction("Отмена") { viewModel.restoreFromArchive(idChatItem) }
            with(snackbar.view){
                setBackgroundColor(R.attr.colorSnackbarBackground.resolveColor(this.context))
                val textView =  findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                textView.setTextColor(R.attr.colorSnackbarTextColor.resolveColor(this.context))
            }
            snackbar.show()
        }
        val touchHelper = ItemTouchHelper(touchCallback)
        touchHelper.attachToRecyclerView(rv_chat_list)

        with(rv_chat_list) {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            addItemDecoration(divider)
        }

        fab.setOnClickListener{
            val intent = Intent(this, GroupActivity::class.java ) //явный вызов активити
            startActivity(intent)
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.getChatDate().observe(this, Observer { chatAdapter.updateData(it) })
    }
}
