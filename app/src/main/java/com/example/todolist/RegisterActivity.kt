package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.leancloud.LCUser
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_login_acticity.*
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        progressBar_register.visibility = View.INVISIBLE
        button_register_.setOnClickListener {
            val username = editText_register_usename.text.toString()
            val password = editText_register_password.text.toString()
            if (username.isNotEmpty() and password.isNotEmpty()) {
                progressBar_register.visibility = View.VISIBLE
            // 创建实例
            var user = LCUser()
            // 等同于 user.put("username", "Tom")
            user.username = username
            user.password = password
            user.signUpInBackground().subscribe(object : Observer<LCUser?> {
                override fun onComplete() {}
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {
                    // 注册失败（通常是因为用户名已被使用
                    progressBar_register.visibility = View.INVISIBLE
                    Log.e(this@RegisterActivity.toString(), e.toString())
                    Toast.makeText(this@RegisterActivity, e.toString(), Toast.LENGTH_SHORT)
                }
                override fun onNext(t: LCUser) {
                    // 注册成功
                    LCUser.logIn(username, password).subscribe(object : Observer<LCUser?> {
                        override fun onComplete() {}
                        override fun onSubscribe(d: Disposable) {}

                        override fun onNext(t: LCUser) {
                            startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                            finish()
                        }

                        override fun onError(e: Throwable) {
                            // 登录失败（可能是密码错误）
                            Log.e(this@RegisterActivity.toString(), e.toString())
                            Toast.makeText(this@RegisterActivity, e.toString(), Toast.LENGTH_SHORT)
                        }
                    })
                }
            })
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}