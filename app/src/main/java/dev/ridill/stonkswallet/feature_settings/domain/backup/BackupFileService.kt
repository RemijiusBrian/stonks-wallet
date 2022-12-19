package dev.ridill.stonkswallet.feature_settings.domain.backup

import android.content.Context
import android.net.Uri
import androidx.core.net.toFile
import dev.ridill.stonkswallet.core.data.local.db.SWDatabase
import dev.ridill.stonkswallet.core.util.DispatcherProvider
import dev.ridill.stonkswallet.core.util.log
import kotlinx.coroutines.withContext
import java.io.File

class BackupFileService(
    private val context: Context,
    private val dispatcherProvider: DispatcherProvider
) {

    suspend fun exportFile(exportPath: Uri) = withContext(dispatcherProvider.io) {
        val backupFile = exportPath.path?.let { File("$it/stonks.backup") } ?: return@withContext
        /*if (!backupFile.exists()) {
            backupFile.createNewFile()
        }
        val downloadFolder = File("/storage/emulated/0/Download")
        val outputFile = File("${downloadFolder.path}/stonks.backup")
        val databaseFile = context.getDatabasePath(SWDatabase.NAME)
        databaseFile.inputStream().use { inputStream ->
            backupFile.outputStream().use { os ->
                inputStream.copyTo(os)
            }
        }
        outputFile.inputStream().use { inputStream ->
            databaseFile.outputStream().use { os ->
                inputStream.copyTo(os)
            }
        }*/
    }
}