package com.xktools.app

import android.os.RemoteException
import rikka.shizuku.Shizuku

class ShellExecutor {
    @Throws(RemoteException::class, InterruptedException::class)
    fun exec(command: String): String {
        val process = Shizuku.newProcess(arrayOf("sh", "-c", command), null, null)
        val exitCode = process.waitFor()
        val output = process.inputStream.bufferedReader().readText()
        val error = process.errorStream.bufferedReader().readText()
        return if (exitCode == 0) output else "Error: $error"
    }
}