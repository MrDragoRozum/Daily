package com.example.daily.presentation.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.daily.R
import com.example.daily.databinding.ActivityMainBinding
import com.example.daily.presentation.application.App
import com.example.daily.presentation.customView.InterfaceTaskView
import com.example.daily.presentation.customView.TaskView
import com.example.daily.presentation.models.TimeFromCalendarView
import com.example.daily.presentation.viewModel.MainViewModel
import com.example.daily.presentation.viewModel.states.StateMain
import com.example.daily.presentation.viewModel.factory.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

typealias mode = InterfaceTaskView.Mode

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val component by lazy { (application as App).component }

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
    }

    private lateinit var resultLauncherImport: ActivityResultLauncher<Array<String>>
    private lateinit var resultLauncherExport: ActivityResultLauncher<String>

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var time: TimeFromCalendarView

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        installActivity()
    }

    private fun installActivity() {
        setSupportActionBar(binding.toolbar)
        registersActivityResults()
        observes(viewModel)
        listeners()
    }

    private fun registersActivityResults() {
        resultLauncherExport =
            registerForActivityResult(ActivityResultContracts.CreateDocument(JSON_FILE)) {
                viewModel.export(it)
            }

        resultLauncherImport = registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            viewModel.import(it)
        }
    }

    private fun observes(viewModel: MainViewModel) {
        lifecycleScope.launch {
            viewModel.state.flowWithLifecycle(lifecycle).collectLatest { state ->
                when (state) {
                    is StateMain.Error -> toast(R.string.error_message)
                    is StateMain.Success -> toast(R.string.success_message)
                    is StateMain.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is StateMain.Result -> installTaskViews(state)
                }
            }
        }
    }

    private fun listeners() {
        with(binding) {
            calendarViewTasks.setOnDateChangeListener { _, cYear, cMonth, cDayOfMonth ->
                time.apply {
                    year = cYear
                    month = cMonth
                    dayOfMonth = cDayOfMonth
                }
                viewModel.refreshTasks(cYear, cMonth, cDayOfMonth)
            }

            floatingActionButtonAddingTask.setOnClickListener {
                TaskActivity.newIntent(
                    context = this@MainActivity,
                    mode = mode.ADDING,
                    time = time
                ).also { startActivity(it) }
            }
        }
    }

    private fun installTaskViews(state: StateMain.Result) {
        with(binding) {
            tableTasksLayout.removeAllViews()
            progressBar.visibility = View.GONE

            tableTasksLayout.apply {
                state.list.map { task ->
                    TaskView(this@MainActivity).apply {
                        title = task.name
                        startTime = task.dateStart.toInt()
                        endTime = task.dateFinish.toInt()

                        setOnClickListener {
                            TaskActivity.newIntent(
                                context = this@MainActivity,
                                mode = mode.READING,
                                task = task,
                            ).also { startActivity(it) }
                        }
                    }
                }.also { addListTaskView(it) }
            }
        }
    }

    private fun toast(resId: Int) {
        Toast.makeText(this@MainActivity, resId, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.itemImportTasks -> resultLauncherImport.launch(arrayOf(JSON_FILE))
            R.id.itemExportTasks -> resultLauncherExport.launch(DEFAULT_NAME_FILE)
        }
        return super.onOptionsItemSelected(item)
    }
}

private const val JSON_FILE = "application/json"
private const val DEFAULT_NAME_FILE = "tasks"