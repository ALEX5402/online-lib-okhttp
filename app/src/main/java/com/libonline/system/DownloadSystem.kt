package com.libonline.system

import android.annotation.SuppressLint
import android.content.Context
import android.net.http.HttpException
import android.widget.Toast
import com.libonline.module.Database
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.lingala.zip4j.ZipFile
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.Locale
import java.util.zip.ZipException

object DownloadSystem  {

    suspend fun FetchDatabase(url : String) : Database {
        val clint = OkHttpClient()
        return try {
            withContext(Dispatchers.IO) {
                val request = Request.Builder()
                    .url(url)
                    .build()
                val response = clint.newCall(request).execute()

                if (response.isSuccessful) {
                    if (response.body!= null) {
                        val moshi = Moshi.Builder().build()
                        val adapter = moshi.adapter(Database::class.java)
                        return@withContext response.body?.string()?.let {
                            adapter.fromJson(it)
                        }!!
                    } else{
                        Database(
                            serverStatus = "No data available",
                            noticeMode = "No data available",
                            dataClearMode = "No data available",
                            serverMessage = "No data available",
                            openTime = "No data available",
                            libs = "No data available",
                            noticeTitle = "No data available",
                            noticeBody = "No data available"
                        )
                    }
                } else {
                    Database(
                        serverStatus = "No data available",
                        noticeMode = "No data available",
                        dataClearMode = "No data available",
                        serverMessage = "No data available",
                        openTime = "No data available",
                        libs = "No data available",
                        noticeTitle = "No data available",
                        noticeBody = "No data available"
                    )
                }
            }
        } catch(@SuppressLint("NewApi") error :HttpException) {
            error.printStackTrace()
           Database(
               serverStatus = "Something went wrong",
               noticeMode = "Something went wrong",
               dataClearMode = "Something went wrong",
               serverMessage = "Something went wrong",
               openTime = "Something went wrong",
               libs = "Something went wrong",
               noticeTitle = "Something went wrong",
               noticeBody = "Something went wrong"
           )
        }
    }



    fun downloadZipfile (downloadlin : String,
                         isdownloadsucess : (String) -> Unit,
                         isdownloadfailed : (String) -> Unit,
                         isdownloadprogress : (Int) -> Unit,
                         downloadedfilepath : (String) -> Unit)
    {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            try {
                val downloadlink = downloadlin.let {
                    URL(it)
                }
                val tempDir = System.getProperty("java.io.tmpdir")
                val timestamp = System.currentTimeMillis()
                val outputfile = File(tempDir, "$timestamp.zip")
                BufferedInputStream(URL(downloadlink.toString()).openStream()).use { inputStream ->
                    FileOutputStream(outputfile).use { outputStream ->
                        val dataBuffer = ByteArray(1024)
                        var bytesRead: Int
                        while (inputStream.read(dataBuffer, 0, 1024).also { bytesRead = it } != -1) {
                            outputStream.write(dataBuffer, 0, bytesRead)
                            val calculatedProgress = outputStream.channel.position() / outputfile.length().toFloat() * 100
                            isdownloadprogress(calculatedProgress.toInt())
                        }
                    }
                    outputfile.setExecutable(true)
                    outputfile.setReadable(true)
                    outputfile.setWritable(true)
                    isdownloadsucess("Download successful")
                    downloadedfilepath(outputfile.absoluteFile.toString())
                }

            } catch (err : Exception)
            {
                isdownloadfailed(err.message.toString())
                err.printStackTrace()
            }
        }
    }

    fun ExtractZipfile(zipfilepath : String?,
                       password : String,
                       context : Context) {
        val scope = CoroutineScope(Dispatchers.IO)
        val file = zipfilepath?.let { File(it) }
        val destDirectory = File(System.getProperty("java.io.tmpdir") as String)
        scope.launch {
            try {
                if (file!= null) {
                    if (file.exists()) {
                        val Zip = ZipFile(file)
                        try {
                            if (Zip.isEncrypted) {
                                Zip.setPassword(password.toCharArray())
                                Zip.extractAll(destDirectory.toString())
                            }else{
                                Zip.extractAll(destDirectory.toString())
                                loadlib(context)
                            }
                        } catch (Err: ZipException) {
                            Err.printStackTrace()
                        }
                    }
                }

            } catch (err: Exception) {
                err.printStackTrace()
            }
        }
    }

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    fun loadlib(context: Context) {
        val files = context.cacheDir.listFiles()
        try {
            if (files != null) {
                for (file in files) {
                    val filename = file.name.lowercase(Locale.getDefault())
                    if (filename.endsWith(".so")) {
                        System.load(file.toString())
                    }
                }
            }
            if (files != null) {
                for (file in files) {
                    val filename = file.name.lowercase(Locale.getDefault())
                    if (filename.endsWith(".zip") || filename.endsWith(".so")) {
                        file.delete()
                    }
                }
            }
            Toast.makeText(context, "LIB Load Done ", Toast.LENGTH_LONG).show()
        } catch (err: Exception) {
            if (files != null) {
                for (file in files) {
                    val filename = file.name.lowercase(Locale.getDefault())
                    if (filename.endsWith(".zip") || filename.endsWith(".so")) {
                        file.delete()
                    }
                }
            }
            err.printStackTrace()
        }
    }


}