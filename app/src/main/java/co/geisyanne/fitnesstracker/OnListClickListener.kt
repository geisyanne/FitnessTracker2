package co.geisyanne.fitnesstracker

import co.geisyanne.fitnesstracker.model.Calc

interface OnListClickListener {
    fun OnClick(id: Int, type: String)
    fun OnLongClick(position: Int, calc: Calc)
}