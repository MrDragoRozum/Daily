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
import com.example.daily.presentation.viewModel.MainViewModel
import com.example.daily.presentation.viewModel.StateMain
import com.example.daily.presentation.viewModel.ViewModelFactory
import com.example.daily.presentation.application.App
import com.example.daily.presentation.customView.InterfaceTaskView
import com.example.daily.presentation.models.TimeFromCalendarView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.days

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val component by lazy {
        (application as App).component
    }

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
    }

    private lateinit var resultLauncherImport: ActivityResultLauncher<Array<String>>

    private lateinit var resultLauncherExport: ActivityResultLauncher<String>

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        listeners()
        observes(viewModel)
        registersActivityResults()
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

    private fun listeners() {
        with(binding) {
            val time = TimeFromCalendarView()
            calendarViewTasks.setOnDateChangeListener { _, year, month, dayOfMonth ->
                time.apply {
                    this.year = year
                    this.month = month
                    this.dayOfMonth = dayOfMonth
                }
                viewModel.refreshTasks(year, month, dayOfMonth)
            }
            floatingActionButtonAddingTask.setOnClickListener {
                TaskActivity.newIntent(
                    context = this@MainActivity,
                    mode = InterfaceTaskView.Mode.ADDING,
                    time = time
                ).also {
                    startActivity(it)
                }
            }
        }
    }

    private fun observes(viewModel: MainViewModel) {
        lifecycleScope.launch {
            viewModel.stateMain.flowWithLifecycle(lifecycle).collectLatest {
                when (it) {
                    is StateMain.Error -> toast(R.string.error_message)
                    is StateMain.Success -> toast(R.string.success_message)
                    is StateMain.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is StateMain.Result -> {
                        binding.progressBar.visibility = View.GONE
                    }
                }
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

    companion object {
        private const val JSON_FILE = "application/json"
        private const val DEFAULT_NAME_FILE = "tasks"
    }
}