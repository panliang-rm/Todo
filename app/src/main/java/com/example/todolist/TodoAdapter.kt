package com.example.todolist

import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.leancloud.LCObject
import kotlinx.android.synthetic.main.recycleview_todo.view.*


class TodoAdapter(private val todos: MutableList<Todo>) :
    RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    class TodoViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(
                LayoutInflater.from(parent.context).inflate(
                        R.layout.recycleview_todo,
                        parent,
                        false
                )
        )
    }

    //checkbox勾选后title横线效果
    private fun titleSpecialEffect(tv_title: TextView, isChecked: Boolean) {
        if (isChecked)
            tv_title.paintFlags = tv_title.paintFlags or STRIKE_THRU_TEXT_FLAG
        else
            tv_title.paintFlags = tv_title.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val curTodo = todos[position]
        val isChecked = curTodo.isChecked
        val title = curTodo.title
        holder.itemView.apply {
            textView_todo_title.text = title
            checkBox_todo_done.isChecked = isChecked
            titleSpecialEffect(textView_todo_title, isChecked)
            checkBox_todo_done.setOnCheckedChangeListener { _, isChecke ->
                titleSpecialEffect(textView_todo_title, isChecke)
                curTodo.isChecked = !curTodo.isChecked
                //使本地修改同步到服务端
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