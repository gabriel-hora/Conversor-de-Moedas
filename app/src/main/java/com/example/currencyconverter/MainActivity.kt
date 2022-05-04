package com.example.currencyconverter

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.currencyconverter.api.Endpoint
import com.example.currencyconverter.databinding.ActivityMainBinding
import com.example.currencyconverter.util.NetworkUtils
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var layout = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getCurrencies()

        binding.btnConverter.setOnClickListener { errorEmpty() }
    }

    private fun convertMoney() {
        val retrofitClient = NetworkUtils.getRetrofitInstance("https://cdn.jsdelivr.net/")
        val endpoint = retrofitClient.create(Endpoint::class.java)

        endpoint.getCurrencyRate(
            binding.sp01.selectedItem.toString(),
            binding.sp02.selectedItem.toString()
        ).enqueue(object :
            Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val data = response.body()?.entrySet()
                var valor = 0.0
                var moeda = ""

                if (data != null) {
                    for (item in data) {
                        if (item.key.toString() == binding.sp02.selectedItem.toString()) {
                            valor = item.value.toString().toDouble()
                            moeda = item.key.toString()
                        }
                    }
                }

                val conversion = binding.textInputEditText.text.toString().toDouble() * valor

                val mensagem = "Sua moeda vale ${conversion.format(2)}$ em $moeda".format(2)

                binding.tvResultado.text = mensagem
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(layout, "Não foi possível converter", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //Função verifica campo Vazio
    fun errorEmpty(): Boolean {
        if (binding.textInputEditText.text.isNullOrEmpty()) {
            binding.textInputLayout.error = "campo vazio"
            return true
        } else {
            binding.textInputLayout.isErrorEnabled = false
            convertMoney()
            return false
        }
    }


    //Função para formatação de String
    fun Double.format(digits: Int) = "%.${digits}f".format(this)

    private fun getCurrencies() {
        val retrofitClient = NetworkUtils.getRetrofitInstance("https://cdn.jsdelivr.net/")
        val endpoint = retrofitClient.create(Endpoint::class.java)

        endpoint.getCurrencies().enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val data = mutableListOf<String>()

                response.body()?.keySet()?.iterator()?.forEach {
                    data.add(it)
                }

                val posBRL = data.indexOf("brl")
                val posUSD = data.indexOf("usd")

                val adapter =
                    ArrayAdapter(baseContext, android.R.layout.simple_spinner_dropdown_item, data)
                binding.sp01.adapter = adapter
                binding.sp02.adapter = adapter

                binding.sp01.setSelection(posBRL)
                binding.sp02.setSelection(posUSD)

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(layout, "Não foi possível converter", Toast.LENGTH_SHORT).show()
            }

        })
    }
}