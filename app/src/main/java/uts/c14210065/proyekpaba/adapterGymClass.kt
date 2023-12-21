package uts.c14210065.proyekpaba

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
<<<<<<< HEAD
=======
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
>>>>>>> 8638bcc63e56efe6b1fc386279bdfdf4aedf23b1

class adapterGymClass (
    private val listClass: ArrayList<GymClass>,
//    private val idLogin: String?,
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
        var _nama: TextView = itemView.findViewById(R.id.tvNameClassO)
        var _level : TextView = itemView.findViewById(R.id.tvLevelO)
        var _btnBook : Button = itemView.findViewById(R.id.btnJoinClass)
<<<<<<< HEAD
        var _pelatih : TextView = itemView.findViewById(R.id.tvCoachO)
        var _waktu : TextView =  itemView.findViewById(R.id.tvTimeO)
=======
        var _pelatih : TextView = itemView.findViewById(R.id.tvCoach)
        var _jam : TextView =  itemView.findViewById(R.id.tvTime)
>>>>>>> 8638bcc63e56efe6b1fc386279bdfdf4aedf23b1

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
        holder._btnBook.text = gymClass.capacity.toString() + " More left"
        holder._level.text = gymClass.level
        holder._pelatih.text = "With " + gymClass.coach

        val selectedDate = gymClass.timestamp?.toDate()?.time ?: 0
        val timeFormat = SimpleDateFormat("HH:mm ", Locale("id", "ID"))
        timeFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
        val waktu = timeFormat.format(Time(selectedDate))
        holder._jam.text = waktu

//        holder._jam.text = gymClass.jam
//        holder.itemView.setOnClickListener{
//            onItemClickCallback.onItemClicked(listClass[position])
//
//        }

        holder._btnBook.setOnClickListener{
            onItemClickCallback.bookClass(listClass[position])
        }
    }

}