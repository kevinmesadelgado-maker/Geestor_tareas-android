package com.example.gestortareasapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TaskManagerApplication : Application() {
    // Esta clase enciende el contenedor global de Hilt para toda la app.
}