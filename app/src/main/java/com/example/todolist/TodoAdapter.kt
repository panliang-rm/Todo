package com.example.todolist

import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import cn.leancloud.LCObject
import cn.leancloud.LCQuery
import cn.leancloud.LCSaveOption
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.util.*


class TodoAdapter(private val todos: MutableList<LCObject>) :
    RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    class TodoViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val tv_title : TextView = itemview.findViewById(R.id.textView_todo_title)
        var cd_DOne : CheckBox = itemview.findViewById(R.id.checkBox_todo_done)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.recycleview_todo,
                parent,
                false
            )
        )
    }

    private fun titleSpecialEffect(tv_title: TextView, isChecked: Boolean) {
        if (isChecked)
            tv_title.paintFlags = tv_title.paintFlags or STRIKE_THRU_TEXT_FLAG
        else
            tv_title.paintFlags = tv_title.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
    }

    fun add(todo: LCObject) {
        todos.add(todo)
        notifyItemInserted(todos.size - 1)
    }

    fun delete() {
        todos.removeAll { todo ->
            todo.get("isChecked") as Boolean
        }

        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val curTodo = todos[position]
        holder.apply {
            var isChecked = curTodo.get("isChecked") as Boolean
            tv_title.text = curTodo.get("title") as CharSequence
            cd_DOne.isChecked = isChecked
            titleSpecialEffect(tv_title, isChecked)
            cd_DOne.setOnCheckedChangeListener { _, isChecke ->

                titleSpecialEffect(tv_title, isChecke)
                isChecked = !isChecked
                Thread{
                    val todo = LCObject.createWithoutData("Todo", curTodo.objectId)
                    todo.put("isChecked", isChecke)
                    todo.save()
                }.start()
            }
        }
    }

    override fun getItemCount(): Int {
        return todos.size
    }
}