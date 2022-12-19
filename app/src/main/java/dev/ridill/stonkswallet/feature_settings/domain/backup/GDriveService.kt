package dev.ridill.stonkswallet.feature_settings.domain.backup

import com.google.api.services.drive.DriveScopes
import java.io.File

class GDriveService(
) {
    companion object {
        const val BASE_URL = "https://www.googleapis.com/upload/drive/v3/"
        val SCOPES: List<String> = listOf(
            DriveScopes.DRIVE_METADATA_READONLY,
            DriveScopes.DRIVE_FILE
        )
    }

    suspend fun upload() {
        val file = File("")

    }
}