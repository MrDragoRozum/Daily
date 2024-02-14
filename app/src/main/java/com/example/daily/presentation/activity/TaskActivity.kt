package com.example.daily.presentation.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.daily.R
import com.example.daily.databinding.ActivityTaskBinding
import com.example.daily.domain.models.Task
import com.example.daily.presentation.application.App
import com.example.daily.presentation.customView.InterfaceTaskView
import com.example.daily.presentation.models.TimeFromCalendarView
import com.example.daily.presentation.viewModel.states.StateTask
import com.example.daily.presentation.viewModel.TaskViewModel
import com.example.daily.presentation.viewModel.factory.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.Serializable
import javax.inject.Inject

typealias Mode = InterfaceTaskView.Mode

class TaskActivity : AppCompatActivity() {

    private val binding by lazy { ActivityTaskBinding.inflate(layoutInflater) }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val component by lazy { (application as App).component }

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[TaskViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        installActivity()
    }

    private fun installActivity() {
        defineMode()
        observes()
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

    private fun read(mode: Mode) {
        intent.getParcelable(TASK_EXTRA, Task::class.java).also {
            with(binding.interfaceTaskView) {
                this.task = it
                setMode(mode)
            }
        }
    }

    private fun add(mode: Mode) {
        with(binding.interfaceTaskView) {
            val time = intent.getParcelable(TIME_EXTRA, TimeFromCalendarView::class.java)
            setMode(mode)
            setListener { viewModel.add(it, time) }
        }
    }

    private fun returnOnMainActivity() {
        toast(R.string.unspecified_mode)
        finish()
    }

    private fun observes() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.state.collectLatest {
                    when (it) {
                        StateTask.Loading -> binding.interfaceTaskView.progressBar = true
                        StateTask.Success -> success()
                    }
                }
            }
        }
    }

    private fun success() {
        toast(R.string.success_message)
        finish()
    }

    private fun toast(resId: Int) {
        Toast.makeText(this@TaskActivity, resId, Toast.LENGTH_SHORT).show()
    }

    private fun <T : Serializable?> Intent.getSerializable(key: String, jClass: Class<T>): T =
        if (checkSDK()) this.getSerializableExtra(key, jClass)!!
        else this.getSerializableExtra(key) as T

    private fun <T : Parcelable?> Intent.getParcelable(key: String, jClass: Class<T>): T =
        if (checkSDK()) this.getParcelableExtra(key, jClass)!!
        else this.getParcelableExtra<Task>(key) as T

    private fun checkSDK() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    companion object {
        fun newIntent(
            context: Context,
            mode: Mode,
            task: Task? = null,
            time: TimeFromCalendarView? = null
        ) = Intent(context, TaskActivity::class.java).apply {
            putExtra(TASK_EXTRA, task)
            putExtra(MODE_EXTRA, mode)
            putExtra(TIME_EXTRA, time)
        }

        const val TASK_EXTRA = "task"
        const val MODE_EXTRA = "mode"
        const val TIME_EXTRA = "time"
    }
}