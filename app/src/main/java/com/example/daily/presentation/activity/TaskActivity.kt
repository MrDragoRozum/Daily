package com.example.daily.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.daily.databinding.ActivityTaskBinding

class TaskActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityTaskBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}