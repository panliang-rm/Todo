package com.example.todolist

import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.leancloud.LCObject
import cn.leancloud.LCQuery
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
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

    fun add(todo: Todo) {
        todos.add(todo)
        notifyItemInserted(todos.size - 1)
    }

    //删除已勾选的Todo
    fun delete() {
        todos.removeAll { todo ->
            todo.isChecked
        }
        notifyDataSetChanged()
        Log.e("Tag2", todos.toString())
        Thread{
            val query = LCQuery<LCObject>("Todo")
            query.findInBackground().subscribe(object : Observer<List<LCObject>> {
                override fun onSubscribe(disposable: Disposable) {}
                override fun onNext(todos: List<LCObject>) {
                    // 获取需要更新的todo
                    for (todo in todos) {

                        if (todo.getBoolean("isChecked"))
                            todo.delete()
                    }
                    // 批量更新
                    LCObject.saveAll(todos)
                }

                override fun onError(throwable: Throwable) {
                    System.out.println(throwable)
                }

                override fun onComplete() {}
            })
        }.start()

    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        var curTodo = todos[position]
       /* var isChecked = curTodo.getBoolean("isChecked")
        val title = curTodo.getString("title")*/
        var isChecked = curTodo.isChecked
        val title = curTodo.title
        holder.itemView.apply {
            textView_todo_title.text = title
            checkBox_todo_done.isChecked = isChecked
            titleSpecialEffect(textView_todo_title, isChecked)
            checkBox_todo_done.setOnCheckedChangeListener { _, isChecke ->
                //Log.e("Tag1", curTodo.getBoolean("isChecked").toString())
                titleSpecialEffect(textView_todo_title, isChecke)
                curTodo.isChecked = !curTodo.isChecked
                //Log.e("Tag2", curTodo.getBoolean("isChecked").toString())
                //更新CheckeBox状态到服务端
                //在主线程使用会产生死锁
                Thread{
                    val todo = LCObject.createWithoutData("Todo", curTodo.objectId)
                    todo.put("isChecked", isChecke)
                    todo.save()
                }.start()
                //Log.e("Tag3", curTodo.getBoolean("isChecked").toString())
            }
        }
    }

    override fun getItemCount(): Int {
        return todos.size
    }
}