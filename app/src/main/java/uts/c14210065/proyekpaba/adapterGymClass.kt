package uts.c14210065.proyekpaba

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class adapterGymClass (
    private val listClass: ArrayList<GymClass>,
//    private val idLogin: String?,
//    private val context: Context
): RecyclerView.Adapter<adapterGymClass.ListViewHolder>(){
    private lateinit var  onItemClickCallback: OnItemClickCallback


    interface OnItemClickCallback {
        fun  onItemClicked(data : GymClass)
        fun delData(pos:Int)
    }
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }


    inner class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var _nama: TextView = itemView.findViewById(R.id.tvNameClass)
        var _level : TextView = itemView.findViewById(R.id.tvLevel)
        var _btn : Button = itemView.findViewById(R.id.btnJoinClass)
        var _pelatih : TextView = itemView.findViewById(R.id.tvCoach)
        var _waktu : TextView =  itemView.findViewById(R.id.tvTime)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.itemgymclass,parent,false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listClass.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var gymClass = listClass[position]

        holder._nama.text = gymClass.name
        holder._btn.text = gymClass.capacity.toString() + " More left"
        holder._level.text = gymClass.level
        holder._pelatih.text = "With " + gymClass.coach
        holder._waktu.text = gymClass.waktu
        holder.itemView.setOnClickListener{
            onItemClickCallback.onItemClicked(listClass[position])

        }
    }

}