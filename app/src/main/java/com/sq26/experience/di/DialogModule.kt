package com.sq26.experience.di

import android.content.Context
import androidx.appcompat.app.AlertDialog
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped

@InstallIn(ActivityComponent::class)
@Module
class DialogModule {
    @ActivityScoped
    @Provides
    fun provideAlertDialogBuilder(@ActivityContext context: Context): AlertDialog.Builder {
        return AlertDialog.Builder(context)
    }
}