package uts.c14210065.proyekpaba.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import uts.c14210065.proyekpaba.model.Gym
import uts.c14210065.proyekpaba.R
import uts.c14210065.proyekpaba.model.User
import java.text.SimpleDateFormat
import java.util.Locale

class adapterSesiT(
    private val listGym: ArrayList<Gym>,
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
        var _tvStartGym: TextView = itemView.findViewById(R.id.tvStartGym)
        var _tvSlot: TextView = itemView.findViewById(R.id.tvSlot)
        var _btnBookGym: Button = itemView.findViewById(R.id.btnBookGym)
        var _tvSlotMax: TextView = itemView.findViewById(R.id.tvSlotMax)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.itemgym, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listGym.size
    }


    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var gym = listGym[position]

        holder._tvStartGym.text = gym.sesi
//        holder._tvEndGym.text = gym.endTime
        holder._tvSlot.text = gym.kuotaSisa.toString()
        holder._tvSlotMax.text = gym.kuotaMax.toString() + " Slot"
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