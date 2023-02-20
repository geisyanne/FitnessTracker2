package co.geisyanne.fitnesstracker.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CalcDao {

    @Insert
    fun insertRegister(calc: Calc)

    @Query("SELECT * FROM Calc WHERE type = :type")
    fun getRegisterByType(type: String) : List<Calc>

    @Update
    fun updateRegister(calc: Calc)

    @Delete
    fun deleteRegister(calc: Calc): Int  // RETORNA 1 SE DEU SUCESSO

}