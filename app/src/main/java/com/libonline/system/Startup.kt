package com.libonline.system

import android.app.ProgressDialog
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.libonline.enc.AlexEnc
import com.libonline.enc.AlexEnc.decryptString
import com.libonline.util.alexUntil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class Startup {
//    val databseUrl = "https://raw.githubusercontent.com/Mama7818181/lib-online/main/update.json"
    val databseUrl = decryptString(
        "N7nKl9nlituqUXtSKZIU1i2lV/XNo/R5QB3Vo5Xc6veafzryTZGPW9L59VybpgUY2VE/hXhO/RnHhmjcdjBWV02nKq/so99ghtrp3nS/wNg=",
        "HZWxufL8jmwYh9FR1303Wg=="
    )

    val zipPassword = decryptString("pass_String" , "key_string")



    fun StartLoading(context : Context) {
        val linkk = AlexEnc.encryptString("https://raw.githubusercontent.com/Mama7818181/lib-online/main/update.json")

        Log.i("ALEX", "PassString : ${linkk.first} , key : ${linkk.second}")
        val scope = CoroutineScope(Dispatchers.Main)

        runBlocking {
            withContext(Dispatchers.IO){
                val wait1 = async {
                    DownloadSystem.fetchData(
                        url = databseUrl)
                }
                val results = wait1.await()

                Log.w("TAG" , results?.libs.toString())

                results?.let { result ->
                    if (result.serverStatus == null){
                        scope.launch {
                            Toast.makeText(context ,
                                "Database Error",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    if (result.serverStatus == "true") {
                        if (result.libs!= null) {
                            DownloadSystem.downloadZipfile(
                                downloadlin = result.libs,
                                isdownloadsucess = { filepath ->
                                    scope.launch {
                                        val wait2 = async {
                                            withContext(Dispatchers.IO) {
                                                DownloadSystem.ExtractZipfile(
                                                    zipfilepath = filepath,
                                                    password = zipPassword,
                                                    context = context
                                                )
                                            }
                                        }
                                        wait2.await()
                                        Toast.makeText(context, "Download Success", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                isdownloadfailed = { error ->
                                    scope.launch {
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                    }
                                }

                            )

                        }

                    }

                }
            }
        }
    }
}