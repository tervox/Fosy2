package org.fossify.gallery.models

import androidx.room.ColumnInfo

/** Resultado da query getVideoDurationsMap() — path + duração salva no BD */
data class PathDuration(
    @ColumnInfo(name = "full_path") val path: String,
    @ColumnInfo(name = "video_duration") val duration: Int
)
