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

class adapterGymClass (
    private val listClass: ArrayList<GymClass>,
    private val idLogin: String?
//    ,
//    private val context: Context
): RecyclerView.Adapter<adapterGymClass.ListViewHolder>(){
    private lateinit var  onItemClickCallback: OnItemClickCallback


    interface OnItemClickCallback {
        fun  onItemClicked(data : GymClass)
        fun delData(pos:Int)

        fun bookClass(data : GymClass)
    }
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }


    inner class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var _nama: TextView = itemView.findViewById(R.id.tvNameClassOC)
        var _level : TextView = itemView.findViewById(R.id.tvLevelOC)
        var _btnBook : Button = itemView.findViewById(R.id.btnCancelClass)
        var _pelatih : TextView = itemView.findViewById(R.id.tvCoachOC)
        var _jam: TextView =  itemView.findViewById(R.id.tvTimeOC)

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
        if(gymClass.capacity == 0){
            holder._btnBook.text = "FULL BOOKED"
            holder._btnBook.isActivated = false
            holder._btnBook.backgroundTintMode = PorterDuff.Mode.SRC_IN
            holder._btnBook.backgroundTintList = ColorStateList.valueOf(Color.GRAY)

        }else if(gymClass.arrUser.contains(idLogin)){
            holder._btnBook.text = "BOOKED"
            holder._btnBook.isActivated = false
            holder._btnBook.backgroundTintMode = PorterDuff.Mode.SRC_IN
            holder._btnBook.backgroundTintList = ColorStateList.valueOf(Color.GRAY)

        }
        else{
            holder._btnBook.text = gymClass.capacity.toString() + " More Left"
            holder._btnBook.setOnClickListener{
                onItemClickCallback.bookClass(listClass[position])
            }
            holder._btnBook.isActivated = true
            val backgroundColor = Color.parseColor("#C9F24D")
            holder._btnBook.setBackgroundColor(backgroundColor)
        }

        holder._level.text = gymClass.level
        holder._pelatih.text = "With " + gymClass.coach


        val selectedDate = gymClass.timestamp?.toDate()?.time ?: 0
        val timeFormat = SimpleDateFormat("HH:mm ", Locale("id", "ID"))
        timeFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
        val waktu = timeFormat.format(Time(selectedDate))
        holder._jam.text = waktu


    }

}