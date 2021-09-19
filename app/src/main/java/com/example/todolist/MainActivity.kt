package com.example.todolist

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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
                val todo = Todo(title)
                todoAdapter.add(todo)
                et_title.text.clear()
            }
        }
        bt_Delete.setOnClickListener {
            todoAdapter.delete()
        }
    }
}