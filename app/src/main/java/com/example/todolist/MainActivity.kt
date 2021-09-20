package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.leancloud.LCObject
import cn.leancloud.LCUser
import io.reactivex.Observer

import io.reactivex.disposables.Disposable




class MainActivity : AppCompatActivity() {

    private lateinit var todoAdapter: TodoAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var bt_Add: Button
    private lateinit var bt_Delete: Button
    private lateinit var et_title: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val layoutManager = LinearLayoutManager(this)
        todoAdapter = TodoAdapter(mutableListOf())
        recyclerView = findViewById(R.id.rv_TodoList)
        recyclerView.adapter = todoAdapter
        recyclerView.layoutManager = layoutManager
        et_title = findViewById(R.id.editText_Todo)
        bt_Add = findViewById(R.id.button_Add)
        bt_Delete = findViewById(R.id.button_Delete)
        bt_Add.setOnClickListener {
            val title = et_title.text.toString()
            if (title.isNotEmpty()) {
                // 构建对象
                val todo = LCObject("Todo")
                // 为属性赋值
                todo.put("title", title)
                todo.put("isChecked", false)
                // 将对象保存到云端
                todo.saveInBackground().subscribe(object : Observer<LCObject?> {
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(t: LCObject) {
                        todoAdapter.add(Todo(title))
                    }

                    override fun onError(e: Throwable) {
                        Log.e(this@MainActivity.toString(), e.toString())
                        Toast.makeText(this@MainActivity, "${e.toString()}", Toast.LENGTH_SHORT)
                    }

                    override fun onComplete() {
                    }
                })
                et_title.text.clear()
            }
        }
        bt_Delete.setOnClickListener {
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