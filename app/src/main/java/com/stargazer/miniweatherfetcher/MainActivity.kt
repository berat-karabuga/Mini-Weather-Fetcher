package com.stargazer.miniweatherfetcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.stargazer.miniweatherfetcher.ui.theme.MiniWeatherFetcherTheme
import io.ktor.client.HttpClient
import io.ktor.client.engine.ProxyBuilder.http
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.launch
import com.google.gson.Gson


class MainActivity : ComponentActivity() {

    private var data = MutableLiveData<String>()
    val http = HttpClient(Android)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiniWeatherFetcherTheme {

                val deneme by data.observeAsState("bilmem")
                var cityName by remember { mutableStateOf("") }

                Column(modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {

                    TextField(value = cityName,
                        onValueChange = {cityName = it },
                        label = { Text("Şehir gir")})

                    Button(onClick = {

                        lifecycleScope.launch {
                            if(cityName.isNotBlank()){
                                val gelenveri: String = fetchWeather(cityName)
                                data.value = gelenveri
                            }else{data.value= "Lütfen geçerli bir şehir girin"}
                        }
                    }) {
                        Text("Hava nası olcak")
                    }

                    Text(text = deneme ?:"")
                    ShowCatImage()
                }

            }
        }
    }
    suspend fun fetchWeather(cityName: String): String{
        val url = "https://api.weatherapi.com/v1/current.json?key=f5a87c368e7b40e78fa135605252910&q=$cityName"
        val response: String = http.get(url).bodyAsText()
        val gson = Gson()
        val weatherResponse = gson.fromJson(response,WeatherResponse::class.java)

        return "${weatherResponse.location.name}: ${weatherResponse.current.condition.text}, ${weatherResponse.current.temp_c}"
    }
}

data class WeatherResponse(
    val location: Location,
    val current: Current
)

data class Location(
    val name: String,
    val region: String,
    val country: String,

)

data class Current(
    val condition: Condition,
    val temp_c: Double
)

data class Condition(
    val text: String
)

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ShowCatImage() {
    GlideImage(
        model = "https://picsum.photos/300",
        contentDescription = "Cute cat",
        modifier = Modifier
            .padding(16.dp)
            .size(200.dp),
        contentScale = ContentScale.Crop
    )
}
