package uts.c14210065.proyekpaba

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class adapterJadwalTrainer (
    private val listClass: ArrayList<SesiT>
): RecyclerView.Adapter<adapterJadwalTrainer.ListViewHolder>(){
    private lateinit var  onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun  onItemClicked(data : GymClass)
        fun delData(pos:Int)
    }
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }


    inner class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var _jam: TextView = itemView.findViewById(R.id.tvSesiJam)
        var _tanggal = itemView.findViewById<TextView>(R.id.tvSesiTanggal)
        var _member = itemView.findViewById<TextView>(R.id.tvMember)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.itemjadwaltrainer, parent,false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listClass.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var SesiT = listClass[position]

        holder._jam.text = SesiT.sesi

        holder._tanggal.text = SesiT.tanggal
        holder._member.text = ""
//        Log.d("nama class", holder._nama.text.toString())

    }
}