package com.stargazer.miniweatherfetcher.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite_cities")
    fun getAllFavorites(): Flow<List<FavoriteCity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(city: FavoriteCity)

    @Delete
    suspend fun removeFavorite(city: FavoriteCity)


    @Query("SELECT EXISTS(SELECT * FROM favorite_cities WHERE cityName = :cityName)")
    suspend fun isFavorite(cityName: String): Boolean
}