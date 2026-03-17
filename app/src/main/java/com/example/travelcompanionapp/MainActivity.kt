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

        // 验证：防止非温度单位出现负数
        val isTemperature = sourceUnit in listOf("Celsius", "Fahrenheit", "Kelvin")
        if (!isTemperature && inputValue < 0) {
            return "Error: Cannot be negative"
        }

        val key = "$sourceUnit to $destUnit"

        val result = when (key) {
            // 货币转换
            "USD to AUD" -> inputValue * 1.55
            "USD to EUR" -> inputValue * 0.92
            "USD to JPY" -> inputValue * 148.50
            "USD to GBP" -> inputValue * 0.78

            // 燃油与距离
            "mpg to km/L" -> inputValue * 0.425
            "Gallon to Liters" -> inputValue * 3.785
            "Nautical Mile to Kilometers" -> inputValue * 1.852

            // 温度
            "Celsius to Fahrenheit" -> (inputValue * 1.8) + 32
            "Fahrenheit to Celsius" -> (inputValue - 32) / 1.8
            "Celsius to Kelvin" -> inputValue + 273.15

            else -> return "Conversion route not supported"
        }

        // 保留两位小数
        return String.format("%.2f", result)
    }
}