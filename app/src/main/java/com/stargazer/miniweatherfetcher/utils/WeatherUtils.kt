package com.stargazer.miniweatherfetcher.utils

fun getWeatherEmoji(code: Int): String {
    return when (code) {
        0 -> "☀️"
        1, 2 -> "🌤️"
        3 -> "☁️"
        45, 48 -> "🌫️"
        51, 53, 55, 56, 57 -> "🌧️"
        61, 63, 65, 66, 67 -> "🌧️"
        71, 73, 75, 77 -> "❄️"
        80, 81, 82 -> "🌦️"
        85, 86 -> "🌨️"
        95, 96, 99 -> "⛈️"
        else -> "❓"
    }
}

fun getWeatherDescription(code: Int): String {
    return when (code) {
        0 -> "Açık / Güneşli"
        1, 2, 3 -> "Parçalı Bulutlu"
        45, 48 -> "Sisli"
        51, 53, 55 -> "Çiseleyen Yağmur"
        61, 63, 65 -> "Sağanak Yağmurlu"
        71, 73, 75 -> "Kar Yağışlı"
        95, 96, 99 -> "Gök Gürültülü Fırtına"
        else -> "Bilinmeyen Durum"
    }
}

fun formatDateToTurkish(dateString: String): String {
    val parts = dateString.split("-")
    if (parts.size == 3) {
        val day = parts[2]
        val month = when (parts[1]) {
            "01" -> "Oca"; "02" -> "Şub"; "03" -> "Mar"; "04" -> "Nis"
            "05" -> "May"; "06" -> "Haz"; "07" -> "Tem"; "08" -> "Ağu"
            "09" -> "Eyl"; "10" -> "Eki"; "11" -> "Kas"; "12" -> "Ara"
            else -> ""
        }
        return "$day $month"
    }
    return dateString
}