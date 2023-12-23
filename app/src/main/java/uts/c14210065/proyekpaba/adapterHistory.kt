package uts.c14210065.proyekpaba

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class adapterHistory  (
    private val listClass: ArrayList<History>
): RecyclerView.Adapter<adapterHistory.ListViewHolder>(){
    private lateinit var  onItemClickCallback: OnItemClickCallback


    interface OnItemClickCallback {
        fun  onItemClicked(data : GymClass)
        fun delData(pos:Int)
    }
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }


    inner class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var _nama: TextView = itemView.findViewById(R.id.tvNamaH)
        var _tanggal = itemView.findViewById<TextView>(R.id.tvTanggal)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.itemhistory, parent,false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listClass.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var gymClass = listClass[position]

        holder._nama.text = gymClass.nama


//        val selectedDate = gymClass.waktu?.toDate()?.time ?: 0
//        val timeFormat = android.icu.text.SimpleDateFormat("HH:mm ", Locale("id", "ID"))
//        timeFormat.timeZone = android.icu.util.TimeZone.getTimeZone("Asia/Jakarta")
//        val waktu = timeFormat.format(Time(selectedDate))
//
//        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
//        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
//        val formattedDate = dateFormat.format(gymClass.waktu)
//        val currentDate = dateFormat.format(Date())
//        Log.d("formatted tanggal : ", formattedDate)

        holder._tanggal.text = gymClass.timestamp.toString()
        Log.d("nama class", holder._nama.text.toString())
//        holder._btn.text = gymClass.capacity.toString() + " More left"
//        holder._level.text = gymClass.level
//        holder._pelatih.text = "With " + gymClass.coach
//        holder._waktu.text = gymClass.waktu
//        holder.itemView.setOnClickListener{
//            onItemClickCallback.onItemClicked(listClass[position])
//
//        }
    }
}