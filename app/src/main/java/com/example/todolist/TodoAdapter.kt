package com.example.todolist

import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TodoAdapter(private val todos : MutableList<Todo>) :
    RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    class TodoViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val tv_title : TextView = itemview.findViewById(R.id.textView_todo_title)
        var cd_DOne : CheckBox = itemview.findViewById(R.id.checkBox_todo_done)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(LayoutInflater.from(parent.context).inflate(
                R.layout.recycleview_todo,
                parent,
                false
            )
        )
    }

    private fun titleSpecialEffect(tv_title : TextView, isChecked : Boolean) {
        if (isChecked)
            tv_title.paintFlags = tv_title.paintFlags or STRIKE_THRU_TEXT_FLAG
        else
            tv_title.paintFlags = tv_title.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
    }
    fun add(todo : Todo) {
        todos.add(todo)
        notifyItemInserted(todos.size-1)
    }

    fun delete() {
        todos.removeAll { todo ->
            todo.isChecked
        }
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val curTodo = todos[position]
        holder.apply {
            tv_title.text = curTodo.title
            cd_DOne.isChecked = curTodo.isChecked
            titleSpecialEffect(tv_title, curTodo.isChecked)
            cd_DOne.setOnCheckedChangeListener {_, isChecked ->
                titleSpecialEffect(tv_title, isChecked)
                curTodo.isChecked = !curTodo.isChecked
            }
        }
    }

    override fun getItemCount(): Int {
        return todos.size
    }
}