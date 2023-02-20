package co.geisyanne.fitnesstracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import co.geisyanne.fitnesstracker.model.Calc


class NdcActivity : AppCompatActivity() {

    private lateinit var editWeight: EditText
    private lateinit var editHeight: EditText
    private lateinit var editAge: EditText
    private lateinit var lifestyle: AutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ndc)

        editWeight = findViewById(R.id.edit_ndc_weight)
        editHeight = findViewById(R.id.edit_ndc_height)
        editAge = findViewById(R.id.edit_ndc_age)

        lifestyle = findViewById(R.id.auto_lifestyle)
        val items = resources.getStringArray(R.array.ndc_lifestyle)
        lifestyle.setText(items.first())
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        lifestyle.setAdapter(adapter)


        val btnSend: Button = findViewById(R.id.btn_ndc_send)
        btnSend.setOnClickListener {
            if (!validate()) {
                Toast.makeText(this, R.string.fields_message, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val weight = editWeight.text.toString().toInt()
            val height = editHeight.text.toString().toInt()
            val age = editAge.text.toString().toInt()

            val calcTmb = calculateTmb(weight, height, age)
            val resultNdc = calculateNdc(calcTmb)

            AlertDialog.Builder(this)
                .setTitle(getString(R.string.ndc_result_title))
                .setMessage(getString(R.string.ndc_result, resultNdc))
                .setPositiveButton(android.R.string.ok) {dialog, which ->
                }
                .setNegativeButton(R.string.save) { dialog, which ->
                    Thread {
                        val app = application as App
                        val dao = app.db.calcDao()

                        val updateRegister = intent.extras?.getInt("updateRegister")
                        if (updateRegister != null) {
                            dao.updateRegister(Calc(id = updateRegister, type = "ndc", res = resultNdc))
                        } else {
                            dao.insertRegister((Calc(type = "ndc", res = resultNdc)))
                        }

                        runOnUiThread {
                            openListActivity()
                            finish()
                        }
                    }.start()
                }
                .create()
                .show()

            val service = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            service.hideSoftInputFromWindow(currentFocus?.windowToken,0)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_search) {
//            finish()
            openListActivity()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openListActivity() {
        val intent = Intent(this, ListCalcActivity::class.java)
        intent.putExtra("type", "ndc")
        startActivity(intent)
    }

    private fun calculateNdc(calcTmb: Double): Double {
        val items = resources.getStringArray(R.array.ndc_lifestyle)
        return when {
            lifestyle.text.toString() == items[0] -> calcTmb * 1.2
            lifestyle.text.toString() == items[1]  -> calcTmb * 1.375
            lifestyle.text.toString() == items[2]  -> calcTmb * 1.55
            lifestyle.text.toString() == items[3]  -> calcTmb * 1.725
            lifestyle.text.toString() == items[4]  -> calcTmb * 1.9
            else -> 0.0
        }
    }

    private fun calculateTmb(weight: Int, height: Int, age: Int): Double {
        return 66 + (weight * 13.8) + (5 * height) - (6.8 * age)
    }

    private fun validate(): Boolean {
        return (editWeight.text.toString().isNotEmpty()
                && editHeight.text.toString().isNotEmpty()
                && editAge.text.toString().isNotEmpty()
                && !editWeight.text.toString().startsWith("0")
                && !editHeight.text.toString().startsWith("0")
                && !editAge.text.toString().startsWith("0"))
    }



}