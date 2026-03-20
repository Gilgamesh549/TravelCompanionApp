package com.example.travelcompanionapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. 找到 UI 元素
        val fromSpinner: Spinner = findViewById(R.id.fromSpinner)
        val toSpinner: Spinner = findViewById(R.id.toSpinner)
        val inputValue: EditText = findViewById(R.id.inputValue)
        val convertButton: Button = findViewById(R.id.convertButton)
        val resultText: TextView = findViewById(R.id.resultText)

        // 2. 填充下拉菜单内容
        val units = arrayOf(
            "USD", "AUD", "EUR", "JPY", "GBP",
            "mpg", "km/L", "Gallon", "Liters", "Nautical Mile", "Kilometers",
            "Celsius", "Fahrenheit", "Kelvin"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, units)
        fromSpinner.adapter = adapter
        toSpinner.adapter = adapter

        // 3. 按钮点击事件
        convertButton.setOnClickListener {
            val inputStr = inputValue.text.toString()

            // 验证：防止输入为空
            if (inputStr.isBlank()) {
                Toast.makeText(this, "Error: Input cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val value = inputStr.toDoubleOrNull()
            if (value == null) {
                Toast.makeText(this, "Error: Please enter a valid number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fromUnit = fromSpinner.selectedItem.toString()
            val toUnit = toSpinner.selectedItem.toString()

            // 验证：处理相同单位转换
            if (fromUnit == toUnit) {
                resultText.text = "Result: $value $toUnit"
                return@setOnClickListener
            }

            // 执行转换
            val resultStr = convertValue(fromUnit, toUnit, value)
            resultText.text = "Result: $resultStr"
        }
    }

    // 4. 核心转换逻辑 (已清除所有错误的引用标记)
    private fun convertValue(sourceUnit: String, destUnit: String, inputValue: Double): String {

        val isTemperature = sourceUnit in listOf("Celsius", "Fahrenheit", "Kelvin")
        if (!isTemperature && inputValue < 0) {
            return "Error: Cannot be negative"
        }

        val result = when {
            // Currency: convert via USD
            sourceUnit in listOf("USD", "AUD", "EUR", "JPY", "GBP") &&
                    destUnit in listOf("USD", "AUD", "EUR", "JPY", "GBP") -> {

                val rates = mapOf(
                    "USD" to 1.0,
                    "AUD" to 1.55,
                    "EUR" to 0.92,
                    "JPY" to 148.50,
                    "GBP" to 0.78
                )

                val usdValue = inputValue / rates[sourceUnit]!!
                usdValue * rates[destUnit]!!
            }

            // Fuel efficiency
            sourceUnit == "mpg" && destUnit == "km/L" -> inputValue * 0.425
            sourceUnit == "km/L" && destUnit == "mpg" -> inputValue / 0.425

            // Volume
            sourceUnit == "Gallon" && destUnit == "Liters" -> inputValue * 3.785
            sourceUnit == "Liters" && destUnit == "Gallon" -> inputValue / 3.785

            // Distance
            sourceUnit == "Nautical Mile" && destUnit == "Kilometers" -> inputValue * 1.852
            sourceUnit == "Kilometers" && destUnit == "Nautical Mile" -> inputValue / 1.852

            // Temperature
            sourceUnit == "Celsius" && destUnit == "Fahrenheit" -> (inputValue * 1.8) + 32
            sourceUnit == "Fahrenheit" && destUnit == "Celsius" -> (inputValue - 32) / 1.8
            sourceUnit == "Celsius" && destUnit == "Kelvin" -> inputValue + 273.15
            sourceUnit == "Kelvin" && destUnit == "Celsius" -> inputValue - 273.15
            sourceUnit == "Fahrenheit" && destUnit == "Kelvin" -> ((inputValue - 32) / 1.8) + 273.15
            sourceUnit == "Kelvin" && destUnit == "Fahrenheit" -> ((inputValue - 273.15) * 1.8) + 32

            else -> return "Conversion route not supported"
        }

        return String.format("%.2f", result)
    }
}