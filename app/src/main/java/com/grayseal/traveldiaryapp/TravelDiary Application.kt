package com.grayseal.traveldiaryapp

import android.app.Application
import android.support.multidex.MultiDexApplication
import dagger.hilt.android.HiltAndroidApp

/**
This class represents the Travel Diary Application class.
It is annotated with @HiltAndroidApp to enable Hilt dependency injection.
 */
@HiltAndroidApp
class TravelDiaryApplication: MultiDexApplication()