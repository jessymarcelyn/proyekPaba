package uts.c14210065.proyekpaba

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.sql.Time
import java.util.Locale

class adapterHomeClass (private val listClass: ArrayList<GymClass>, ): RecyclerView.Adapter<adapterHomeClass.ListViewHolder>(){

    inner class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var _nama: TextView = itemView.findViewById(R.id.tvNameClassOCHome)
        var _level : TextView = itemView.findViewById(R.id.tvLevelOCHome)
        var _pelatih : TextView = itemView.findViewById(R.id.tvCoachOCHome)
        var _jam: TextView =  itemView.findViewById(R.id.tvTimeOCHome)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.list_home_class,parent,false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listClass.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var gymClass = listClass[position]

        holder._nama.text = gymClass.name

        holder._level.text = gymClass.level
        holder._pelatih.text = "With " + gymClass.coach


        val selectedDate = gymClass.timestamp?.toDate()?.time ?: 0
        val timeFormat = SimpleDateFormat("HH:mm ", Locale("id", "ID"))
        timeFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
        val waktu = timeFormat.format(Time(selectedDate))
        holder._jam.text = waktu



    }

}