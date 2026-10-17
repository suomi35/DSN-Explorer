package com.holdenisacat.dsnexplorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.holdenisacat.dsnexplorer.ui.MainScreen
import com.holdenisacat.dsnexplorer.ui.theme.DSNExplorerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DSNExplorerTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.startUpdates()
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopUpdates()
    }
}
