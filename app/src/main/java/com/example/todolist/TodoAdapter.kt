package com.example.todolist

import android.content.Context
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.leancloud.LCObject
import kotlinx.android.synthetic.main.recycleview_todo.view.*


class TodoAdapter(private val todos: MutableList<Todo>, private val activity: MainActivity) :
    RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    fun getContext(): Context {
        return activity
    }

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

    fun deleteItem(position: Int) {
        //本地
        val _todo = todos.get(position)
        //服务端删除
        Thread{
            val todo = LCObject.createWithoutData("Todo", _todo.objectId)
            todo.delete()
        }.start()

        todos.removeAt(position)
        notifyItemRemoved(position)
    }

    fun editItem(position: Int) {
        val curTodo = todos[position]
        val bundle = Bundle()
        bundle.putString("objectid", curTodo.objectId)
        bundle.putString("title", curTodo.title)
        bundle.putBoolean("ischecked", curTodo.isChecked)
        val fragment = AddNewTask()
        fragment.arguments = bundle
        fragment.show(activity.supportFragmentManager, AddNewTask.TAG)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val curTodo = todos[position]
        holder.itemView.apply {
            textView_todo_title.text = curTodo.title
            checkBox_todo_done.isChecked = curTodo.isChecked
            titleSpecialEffect(textView_todo_title, curTodo.isChecked)
            checkBox_todo_done.setOnCheckedChangeListener { _, isChecked ->
                titleSpecialEffect(textView_todo_title, isChecked)
                curTodo.isChecked = !curTodo.isChecked
                //使本地修改同步到服务端
                Thread{
                    val todo = LCObject.createWithoutData("Todo", curTodo.objectId)
                    todo.put("isChecked", curTodo.isChecked)
                    todo.save()
                }.start()
            }
        }
    }

    override fun getItemCount(): Int {
        return todos.size
    }
}