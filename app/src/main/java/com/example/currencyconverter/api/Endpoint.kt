package com.example.currencyconverter.api

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface Endpoint {

    //Interface de consulta do Retrofit - Irá bater no Endpoint e retornar

    @GET("/gh/fawazahmed0/currency-api@1/latest/currencies.json") //URL Relativa
    fun getCurrencies() : Call<JsonObject>

    @GET("/gh/fawazahmed0/currency-api@1/latest/currencies/{from}/{to}.json") //URL Relativa parametrizada
    fun getCurrencyRate(@Path(value = "from", encoded = true)from : String, @Path(value = "to", encoded = true)to : String) : Call<JsonObject>
}