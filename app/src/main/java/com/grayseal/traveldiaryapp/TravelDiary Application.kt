package com.grayseal.traveldiaryapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
This class represents the Travel Diary Application class.
It is annotated with @HiltAndroidApp to enable Hilt dependency injection.
 */
@HiltAndroidApp
class TravelDiaryApplication : Application()