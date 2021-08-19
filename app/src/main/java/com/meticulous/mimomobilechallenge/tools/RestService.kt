package com.meticulous.mimomobilechallenge.tools

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RestService {
    companion object {
        private var restInterface: RestInterface? = null

        /**
         * Get an instance of the RestInterface to make an http calls.
         */
        fun getRetrofit(): RestInterface {
            restInterface?.let {
                return it
            }
            val retrofit = Retrofit.Builder()
                .baseUrl("https://mimochallenge.azurewebsites.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            restInterface = retrofit.create(RestInterface::class.java)
            restInterface?.let {
                return it
            }
            return retrofit.create(RestInterface::class.java)
        }
    }
}