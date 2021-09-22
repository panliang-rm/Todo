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


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_acticity)
        progressBar_login.visibility = View.INVISIBLE
        val currentUser = LCUser.getCurrentUser()
        if (currentUser != null) {
            // 跳到首页
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        } else {
            initOnclick()
        }
    }

    private fun initOnclick() {
        button_login.setOnClickListener {
            val username = editText_login_username.text.toString().trim()
            val password = editText_login_password.text.toString().trim()
            if (username.isNotEmpty() and password.isNotEmpty()) {
                progressBar_login.visibility = View.VISIBLE
                LCUser.logIn(username, password).subscribe(object : Observer<LCUser?> {
                    override fun onComplete() {}
                    override fun onSubscribe(d: Disposable) {}

                    override fun onNext(t: LCUser) {
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }

                    override fun onError(e: Throwable) {
                        // 登录失败（可能是密码错误）
                        progressBar_login.visibility = View.INVISIBLE
                        Log.e(this@LoginActivity.toString(), e.toString())
                        Toast.makeText(this@LoginActivity, e.toString(), Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
        button_goregister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}