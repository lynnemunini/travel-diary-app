package com.grayseal.traveldiaryapp.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.IOException

/**
 * Utility class for processing Android URIs and converting them into temporary files.
 */
object ProcessAndroidUri {

    /**
     * Converts an Android content URI to a temporary file.
     *
     * @param context The context used for accessing resources and the content resolver.
     * @param uri The Android content URI to be converted.
     * @return The temporary file containing the content from the provided URI.
     * @throws IOException If an I/O error occurs during the file conversion process.
     */
    @Throws(IOException::class)
    fun from(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IOException("Could not open input stream for URI: $uri")

        val fileName = getFileName(context, uri)
        val tempFile = File.createTempFile(
            splitFileName(fileName)[0], // Prefix
            splitFileName(fileName)[1], // Suffix
            context.cacheDir // Directory for the temporary file
        )
        tempFile.deleteOnExit()

        inputStream.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }

    /**
     * Splits a file name into its base name and extension.
     *
     * @param fileName The input file name.
     * @return An array containing the base name and extension.
     */
    fun splitFileName(fileName: String): Array<String> {
        val lastDotIndex = fileName.lastIndexOf(".")
        return if (lastDotIndex != -1) {
            arrayOf(fileName.substring(0, lastDotIndex), fileName.substring(lastDotIndex))
        } else {
            arrayOf(fileName, "")
        }
    }

    /**
     * Retrieves the display name associated with an Android content URI.
     *
     * @param context The context used for accessing the content resolver.
     * @param uri The Android content URI.
     * @return The display name of the content represented by the URI.
     */
    fun getFileName(context: Context, uri: Uri): String {
        val displayNameColumnIndex = context.contentResolver.query(
            uri, null, null, null, null
        )?.use { cursor ->
            cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        } ?: -1

        return if (displayNameColumnIndex != -1) {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    cursor.getString(displayNameColumnIndex) ?: uri.lastPathSegment ?: ""
                } else {
                    uri.lastPathSegment ?: ""
                }
            } ?: uri.lastPathSegment ?: ""
        } else {
            uri.lastPathSegment ?: ""
        }
    }
}
