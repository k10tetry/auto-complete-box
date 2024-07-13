package com.k10tetry.autocompletebox

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.k10tetry.autocompletebox.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.autoCompleteType.items =
            listOf("Grocery", "Utility", "Fuel", "Food", "Travel").mapIndexed { index, s ->
                AutoCompleteBox.AutoCompleteBoxItem(index.plus(1).toLong(), s)
            }
    }
}