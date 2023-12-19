package uts.c14210065.proyekpaba

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class adapterSesiT(
    private val listSesiT: ArrayList<SesiT>,
    private val idLogin: String?
) : RecyclerView.Adapter<adapterSesiT.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data: User)
        fun delData(pos: Int)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var _tvStartSesiTrainer: TextView = itemView.findViewById(R.id.tvStartSesiTrainer)
        var _btnBookSesi: Button = itemView.findViewById(R.id.btnBookSesi)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.itemsesitrainer, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listSesiT.size
    }


    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var sesiTrainer = listSesiT[position]

        holder._tvStartSesiTrainer.text = sesiTrainer.sesi

    }



    fun showAlert(context: Context, title: String, message: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, which ->
            // Handle the "OK" button click if needed
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun convertDateToTimestamp(dateString: String): Long {
        val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
        try {
            val date = dateFormat.parse(dateString)
            return date?.time ?: 0
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }
}