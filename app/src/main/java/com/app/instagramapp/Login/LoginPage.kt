package com.app.instagramapp.Login

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast
import com.app.instagramapp.Home.MainActivity
import com.app.instagramapp.Other.getUserName
import com.app.instagramapp.Other.setUserName
import com.app.instagramapp.R

import kotlinx.android.synthetic.main.activity_login_page.*
import kotlinx.android.synthetic.main.content_login_page.*

class LoginPage : AppCompatActivity() {

    var user:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        user = getUserName(this)
        if(!user.isNullOrEmpty()){
            val i = Intent(this,MainActivity::class.java)
            startActivity(i)
            finish()
        }

        signInBtn.setOnClickListener {
            val u_user = username.text.toString()
            if(u_user.isNullOrEmpty()){
                Toast.makeText(this,"Please enter a username",Toast.LENGTH_SHORT).show()
            }
            else{
                setUserName(this,u_user)
                val i = Intent(this,MainActivity::class.java)
                startActivity(i)
                finish()
            }
        }

    }

}
