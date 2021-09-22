package com.example.todolist

import android.content.DialogInterface


interface DialogCloseListener {
    fun handleDialogClose(dialog: DialogInterface?)
}