package com.home.androidprotodatastoredemo.login

import android.content.Context
import androidx.datastore.CorruptionException
import androidx.datastore.DataStore
import androidx.datastore.Serializer
import androidx.datastore.createDataStore
import com.google.protobuf.InvalidProtocolBufferException
import com.home.androidprotodatastoredemo.proto.LoginPreferences
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

@Suppress("NAME_SHADOWING")
class LoginModel(context: Context) {

    private val dataStore: DataStore<LoginPreferences> =
        context.createDataStore(
            fileName = "login_preferences.pb",
            serializer = LoginPreferencesSerializer
        )

    val loginPreferencesFlow = dataStore.data.catch {
        if (it is IOException) {
            emit(LoginPreferences.getDefaultInstance())
        } else {
            throw it
        }
    }.map { preferences ->
        val isLogged = when (preferences.isLogged) {
            LoginPreferences.IsLogged.NOT_LOGGED_IN -> false
            LoginPreferences.IsLogged.ALREADY_LOGGED_IN -> true
            else -> false
        }
        LoginBean(isLogged)
    }

    suspend fun setIsLogged(isLogged: Boolean) {
        val isLogged = when (isLogged) {
            false -> LoginPreferences.IsLogged.NOT_LOGGED_IN
            true -> LoginPreferences.IsLogged.ALREADY_LOGGED_IN
        }
        dataStore.updateData { preferences ->
            preferences.toBuilder()
                .setIsLogged(isLogged)
                .build()
        }
    }
}

data class LoginBean(
    val isLogged: Boolean
)

object LoginPreferencesSerializer : Serializer<LoginPreferences> {
    override fun readFrom(input: InputStream): LoginPreferences {
        try {
            return LoginPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override fun writeTo(t: LoginPreferences, output: OutputStream) = t.writeTo(output)
}