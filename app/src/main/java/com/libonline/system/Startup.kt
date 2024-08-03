package com.libonline.system

import android.app.ProgressDialog
import android.content.Context
import android.widget.Toast
import com.libonline.MainObjects
import com.libonline.util.alexUntil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class Startup {

    fun StartLoading(context : Context){
        val progressDialog : ProgressDialog = ProgressDialog(context)

        val scope = CoroutineScope(Dispatchers.Main)
        val await1 = runBlocking {
            val results = async { DownloadSystem.FetchDatabase(MainObjects.databseUrl) }

            val serverdata = results.await()

            if (serverdata.serverStatus == "true") {
                DownloadSystem.downloadZipfile(serverdata.libs!!,
                    isdownloadsucess = {
                        scope.launch {
                            progressDialog.dismiss()
                            Toast.makeText(context, "Download Successful", Toast.LENGTH_LONG).show()
                        }

                    },
                    isdownloadfailed = {
                        scope.launch {
                            progressDialog.dismiss()
                            Toast.makeText(context, "Download Failed : $it", Toast.LENGTH_LONG).show()
                        }

                    },
                    downloadedfilepath = {
                        scope.launch {
                            val password = MainObjects.zipPassword
                            DownloadSystem.ExtractZipfile(it, password, context)
                        }
                    },
                    isdownloadprogress = {
                        scope.launch {
                            progressDialog.setMessage("Downloading Libs : $it%")
                            progressDialog.show()
                            if (it == 100) {
                                progressDialog.dismiss()
                            }
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                            progressDialog.max = 100
                            progressDialog.setCancelable(false)
                            progressDialog.isIndeterminate = false
                            progressDialog.setProgress(it)
                        }

                    }

                )
                if (serverdata.noticeMode == "true") {
                    Toast.makeText(context, "${serverdata.noticeTitle} ${serverdata.noticeBody}", Toast.LENGTH_LONG).show()
                }
                if (serverdata.dataClearMode == "true") {
                    alexUntil.clearlogs(context)
                }
            }else {
                Toast.makeText(context, "${serverdata.serverMessage} ${serverdata.openTime} ", Toast.LENGTH_LONG).show()
            }

        }
    }

}