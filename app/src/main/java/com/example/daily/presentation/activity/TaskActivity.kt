package com.example.daily.presentation.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.daily.R
import com.example.daily.databinding.ActivityTaskBinding
import com.example.daily.domain.models.Task
import com.example.daily.presentation.customView.InterfaceTaskView
import java.io.Serializable

typealias Mode = InterfaceTaskView.Mode

class TaskActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityTaskBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        defineMode()
    }

    private fun defineMode() {
        intent.getSerializable(MODE_EXTRA, Mode::class.java).also {
            when (it) {
                Mode.READING -> read(it)
                Mode.ADDING -> add(it)
                Mode.UNSPECIFIED -> returnOnMainActivity()
            }
        }
    }

    private fun returnOnMainActivity() {
        Toast.makeText(
            this@TaskActivity,
            R.string.unspecified_mode,
            Toast.LENGTH_LONG
        ).show()
        finish()
    }

    private fun add(mode: Mode) {
        with(binding.interfaceTaskView) {
            setMode(mode)
            setListener {
                Log.d("TaskActivityTest", "$it")
            }
        }
    }

    private fun read(mode: Mode) {
        intent.getParcelable(TASK_EXTRA, Task::class.java).also {
            with(binding.interfaceTaskView) {
                this.task = it
                setMode(mode)
            }
        }
    }

    private fun <T : Serializable?> Intent.getSerializable(key: String, jClass: Class<T>): T =
        if (checkSDK()) this.getSerializableExtra(key, jClass)!!
        else this.getSerializableExtra(key) as T

    private fun <T : Parcelable> Intent.getParcelable(key: String, jClass: Class<T>): T =
        if (checkSDK()) this.getParcelableExtra(key, jClass)!!
        else this.getParcelableExtra<Task>(key) as T

    private fun checkSDK() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    companion object {
        fun newIntent(context: Context, mode: Mode, task: Task? = null) =
            Intent(context, TaskActivity::class.java).apply {
                putExtra(TASK_EXTRA, task)
                putExtra(MODE_EXTRA, mode)
            }

        const val TASK_EXTRA = "task"
        const val MODE_EXTRA = "mode"
    }
}