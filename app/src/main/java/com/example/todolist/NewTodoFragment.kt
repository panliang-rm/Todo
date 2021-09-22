package com.example.todolist

import android.app.Activity
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import cn.leancloud.LCObject
import cn.leancloud.LCUser
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_new_todo.*
import java.util.*


class AddNewTask : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.fragment_new_todo, container, false)
        dialog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragment_newTaskText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString() == "") {
                    fragment_newTaskText?.isEnabled = false
                    fragment_newTaskText.setTextColor(Color.GRAY)
                } else {
                    fragment_newTaskText?.isEnabled = true
                    fragment_newTaskText.setTextColor(ContextCompat.getColor(Objects.requireNonNull(context)!!, R.color.colorPrimaryDark))
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        frangment_newTaskButton.setOnClickListener{
            val title = fragment_newTaskText.text.toString()
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
                        //val _todo = Todo(t.getString("title"), t.getBoolean("isChecked"), t.objectId)
                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
                    }

                    override fun onComplete() {
                    }
                })
                dismiss()
            }

        }
    }

    //当按下save按钮，调用onDismiss输入栏消失，回调handleDialogClose（接口方法，在需要接受的类中继承接口，可执行函数）
    override fun onDismiss(dialog: DialogInterface) {
        val activity: Activity? = activity
        if (activity is DialogCloseListener) (activity as DialogCloseListener).handleDialogClose(dialog)
    }

    companion object {
        const val TAG = "ActionBottomDialog"
        fun newInstance(): AddNewTask {
            return AddNewTask()
        }
    }
}