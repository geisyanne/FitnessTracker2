package co.geisyanne.fitnesstracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.geisyanne.fitnesstracker.model.Calc
import java.text.SimpleDateFormat
import java.util.*

class ListCalcActivity : AppCompatActivity(), OnListClickListener {

    private lateinit var rvListCalc: RecyclerView

    private lateinit var adapter: ListCalcAdapter
    private lateinit var result: MutableList<Calc>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_calc)

        result = mutableListOf<Calc>()
        adapter = ListCalcAdapter(result, this)

        rvListCalc = findViewById(R.id.rv_listCalc)
        rvListCalc.layoutManager = LinearLayoutManager(this)
        rvListCalc.adapter = adapter

        val type =
            intent?.extras?.getString("type") ?: throw IllegalStateException("type not found")

        Thread {
            val app = application as App
            val dao = app.db.calcDao()
            val response = dao.getRegisterByType(type)

            runOnUiThread {
                result.addAll(response)
                result.reverse()
                adapter.notifyDataSetChanged()
            }

        }.start()
    }


    override fun OnClick(id: Int, type: String) {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.edit_calc_title))
            .setPositiveButton(R.string.no) { dialog, which ->
            }
            .setNegativeButton(R.string.yes) { dialog, which ->
                when (type) {
                    "imc" -> {
                        val intent = Intent(this, ImcActivity::class.java)
                        intent.putExtra("updateRegister", id)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                    }
                    "ndc" -> {
                        val intent = Intent(this, NdcActivity::class.java)
                        intent.putExtra("updateRegister", id)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                    }
                }
            }
            .create()
            .show()
    }

    override fun OnLongClick(position: Int, calc: Calc) {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.delete_calc_title))
            .setPositiveButton(R.string.no) { dialog, which ->
            }
            .setNegativeButton(R.string.yes) { dialog, which ->
                Thread {
                    val app = application as App
                    val dao = app.db.calcDao()
                    val response = dao.deleteRegister(calc)

                    if (response > 0) {
                        runOnUiThread {
                            result.removeAt(position)
                            adapter.notifyItemRemoved(position)
                        }
                    }
                }.start()
            }
            .create()
            .show()
    }

    private inner class ListCalcAdapter(
        private val listCalc: List<Calc>,
        private val listener: OnListClickListener
    ) : RecyclerView.Adapter<ListCalcAdapter.ListCalcViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListCalcViewHolder {
            val view = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false)
            return ListCalcViewHolder(view)
        }

        override fun onBindViewHolder(holder: ListCalcViewHolder, position: Int) {
            val itemCurrent = listCalc[position]
            holder.bind(itemCurrent)
        }

        override fun getItemCount(): Int {
            return listCalc.size
        }

        private inner class ListCalcViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(item: Calc) {
                val txtResult = itemView as TextView

                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))
                val date = sdf.format(item.createdDate)
                val res = item.res

                txtResult.text = getString(R.string.list_calc_response, res, date)

                txtResult.setOnClickListener {
                    listener.OnClick(item.id, item.type)
                }

                txtResult.setOnLongClickListener {
                    listener.OnLongClick(adapterPosition, item)
                    true
                }
            }
        }
    }
}