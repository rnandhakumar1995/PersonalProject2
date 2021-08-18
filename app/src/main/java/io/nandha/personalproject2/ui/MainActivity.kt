package io.nandha.personalproject2.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import io.nandha.personalproject2.databinding.ActivityMainBinding
import io.nandha.personalproject2.viewmodel.MainActivityViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        requestPermission()
    }
    private fun requestPermission() {
        if (!hasReadPermission()) {
            requestReadPermission()
        }
        val result = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (result == 0) {
            initializeApp()
        }
    }

    private fun hasReadPermission() =
        ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestReadPermission() = ActivityCompat.requestPermissions(
        this,
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
        1
    )


    private fun initializeApp() {
        val viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        fillDb(viewModel)
        initializeUI(viewModel)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                initializeApp()
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "Permission denied to read your External storage",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun fillDb(viewModel: MainActivityViewModel) {
        viewModel.initializeLocalDb()
        viewModel.readDbFiles()
    }

    private fun initializeUI(viewModel: MainActivityViewModel) {
        val songsAdapter = SongsAdapter { song ->
            viewModel.toggleLike(song)
        }
        activityMainBinding.songsList.adapter = songsAdapter
        lifecycleScope.launch {
            viewModel.songs.collectLatest {
                songsAdapter.setData(it)
            }
        }
    }
}