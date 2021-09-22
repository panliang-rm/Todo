package com.example.todolist

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import cn.leancloud.LCObject
import cn.leancloud.LCQuery
import cn.leancloud.LCUser
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity(), DialogCloseListener{

    private lateinit var todoAdapter: TodoAdapter
    private val todos: MutableList<Todo> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val layout = LinearLayoutManager(this)
        layout.stackFromEnd = true //列表再底部开始展示，反转后由上面开始展示
        layout.reverseLayout = true //列表翻转
        todoAdapter = TodoAdapter(todos)
        rv_TodoList.adapter = todoAdapter
        rv_TodoList.layoutManager = layout
        initOnClick()
        initData()
        mainactivity_swiperfresh.setOnRefreshListener {
            mainactivity_swiperfresh.isRefreshing = true
            initData()
        }
    }

    //从服务端获取数据
    private fun initData() {
        todos.clear()
        val query = LCQuery<LCObject>("Todo")
        query.whereEqualTo("user", LCUser.getCurrentUser())
        query.findInBackground().subscribe(object : Observer<List<LCObject>?> {
            override fun onSubscribe(disposable: Disposable) {}
            override fun onError(throwable: Throwable) {}
            override fun onComplete() {}
            override fun onNext(t: List<LCObject>) {
                // 服务端获取到的是此用户的所有Todo，即List<LCObject>
                // 本地需要的是List<Todo>，所以需要转换
                for (_t in t) {
                    val todo = Todo(_t.getString("title"), _t.getBoolean("isChecked"), _t.objectId)
                    todos.add(todo)
                }
                //更新列表
                todoAdapter.notifyDataSetChanged()
                mainactivity_swiperfresh.isRefreshing = false
            }
        })
    }

    private fun initOnClick() {
        main_floatingactionbutton.setOnClickListener {
            AddNewTask.newInstance().show(supportFragmentManager, AddNewTask.TAG)
        }

    }

    //创建主页右上角功能栏
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //主页右上角退出到登陆页面
        if (item.itemId == R.id.main_signout) {
            LCUser.logOut()
            startActivity(Intent(this, LoginActicity::class.java))
            finish()
        }
        //返回按钮退出桌面处理
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    //回调函数，当save Todo时执行
    override fun handleDialogClose(dialog: DialogInterface?) {
        //重新获取数据
        initData()
    }

}


