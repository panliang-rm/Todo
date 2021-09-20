package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import cn.leancloud.LCObject
import cn.leancloud.LCQuery
import cn.leancloud.LCUser
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var todoAdapter: TodoAdapter
    private val todos: MutableList<LCObject> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val layoutManager = LinearLayoutManager(this)
        todoAdapter = TodoAdapter(todos)
        rv_TodoList.adapter = todoAdapter
        rv_TodoList.layoutManager = layoutManager

        initOnClick()
        initData()
    }

    private fun initData() {
        val query = LCQuery<LCObject>("Todo")
        query.whereEqualTo("user", LCUser.getCurrentUser())
        query.findInBackground().subscribe(object : Observer<List<LCObject>?> {
            override fun onSubscribe(disposable: Disposable) {}
            override fun onError(throwable: Throwable) {}
            override fun onComplete() {}
            override fun onNext(t: List<LCObject>) {
                // Todo是一个user的所有Todo对象的列表
                t.toMutableList().let { todos.addAll(it) }
                todoAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun initOnClick() {
        button_Add.setOnClickListener {
            val title = editText_Todo.text.toString()
            if (title.isNotEmpty()) {
                // 构建对象
                val todo = LCObject("Todo")
                // 为属性赋值
                todo.put("title", title)
                todo.put("isChecked", false)
                todo.put("user", LCUser.getCurrentUser())
                // 将对象保存到云端
                todo.saveInBackground().subscribe(object : Observer<LCObject?> {
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(t: LCObject) {
                        todoAdapter.add(t)
                    }

                    override fun onError(e: Throwable) {
                        Log.e(this@MainActivity.toString(), e.toString())
                        Toast.makeText(this@MainActivity, e.toString(), Toast.LENGTH_SHORT)
                    }

                    override fun onComplete() {
                    }
                })
                editText_Todo.text.clear()
            }
        }
        button_Delete.setOnClickListener {
            todoAdapter.delete()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.main_signout) {
            LCUser.logOut()
            startActivity(Intent(this, LoginActicity::class.java))
            finish()
        }
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

}


