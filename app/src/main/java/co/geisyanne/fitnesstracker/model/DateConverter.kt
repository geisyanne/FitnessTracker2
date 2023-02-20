package co.geisyanne.fitnesstracker.model

import androidx.room.TypeConverter
import java.util.*

object DateConverter {

    @TypeConverter
    fun toDate(dateLong: Long?): Date? {   // BUSCAR DATA
        return if (dateLong != null) Date(dateLong) else null
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {  // GRAVAR DATA
        return  date?.time
    }

}