package com.example.errenteriaapp.database



import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    foreignKeys = [ForeignKey(
        entity = Ikasle::class,
        parentColumns = ["izenaAbizena"],
        childColumns = ["izenaAbizena"],
        onDelete = ForeignKey.CASCADE
    )]
)


data class IzenTaldea(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val izenaAbizena: String,
    val taldea: String

)