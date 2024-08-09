package com.libonline.system

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.libonline.module.Database

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.lingala.zip4j.ZipFile
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.Locale
import java.util.zip.ZipException

object DownloadSystem  {
    external fun Loadthelibfile(filepath: String)

    fun fetchData(url: String): Database? {
        val okhttpclint: OkHttpClient = OkHttpClient()
         var jsonobject : JSONObject? = null

        if (url == "")
            return null

        return try {
            val request = Request.Builder()
                .url(url)
                .build()
            okhttpclint.newCall(request).execute().use { responce ->
                if (!responce.isSuccessful)
                    return@use
                if (responce.body == null)
                    return@use
                val jsonString = responce.body?.string()
//                Log.d("TAG", "fetchData: $jsonString")
                jsonobject = JSONObject(jsonString)

            }

            jsonobject?.let { responce ->
//                Log.e("TAG",responce!!.getString("libs"))
                Database(
                    serverStatus = responce.getString("serverstatus"),
                    noticeMode = responce.getString("noticemode"),
                    dataClearMode = responce.getString("dataclearmode"),
                    serverMessage = responce.getString("servermeassage"),
                    openTime = responce.getString("opentime"),
                    libs = responce.getString("libs"),
                    noticeTitle = responce.getString("notice_title"),
                    noticeBody = responce.getString("notice_body")
                )
            }
        } catch ( error: Exception) {
            error.printStackTrace()
            Database(
                serverStatus = "Failed",
                noticeMode = "Failed",
                dataClearMode = "Failed",
                serverMessage = "Failed",
                openTime = "Failed",
                libs = "Failed",
                noticeTitle = "Failed",
                noticeBody = "Failed"
            )
        }

    }


    fun downloadZipfile (downloadlin : String,
                         isdownloadsucess : (filepath : String) -> Unit,
                         isdownloadfailed : (String) -> Unit)
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
                            outputStream.write(dataBuffer, 0, bytesRead) }
                    }
                    outputfile.setExecutable(true)
                    outputfile.setReadable(true)
                    outputfile.setWritable(true)
                    isdownloadsucess(outputfile.absoluteFile.toString())
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
        val scope = CoroutineScope(Dispatchers.Main)
        val files = context.cacheDir.listFiles()
        try {
            if (files != null) {
                for (file in files) {
                    val filename = file.name.lowercase(Locale.getDefault())
                    if (filename.endsWith(".so")) {
                        Loadthelibfile(file.absolutePath)
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
            scope.launch {
                Toast.makeText(context, "LIB Load Started ", Toast.LENGTH_LONG).show()
            }
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