package com.grayseal.traveldiaryapp.utils

import android.content.Context
import android.os.Environment
import java.io.File

object FileUtils {

    const val FILE_AUTHORITY = "com.grayseal.traveldiaryapp.fileProvider"

    fun createExternalStorageFile(
        context: Context,
        fileName: String?,
        preDefinedScope: String?
    ): File {
        var definedScope = preDefinedScope
        definedScope = definedScope ?: Environment.DIRECTORY_DOCUMENTS
        return File(context.getExternalFilesDir(definedScope), fileName)
    }
}