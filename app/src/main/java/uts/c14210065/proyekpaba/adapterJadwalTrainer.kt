package uts.c14210065.proyekpaba

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

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

//        Log.d("nama class", holder._nama.text.toString())
        val db = Firebase.firestore

        if(SesiT.userTrainerId == ""){
            holder._member.text = "No bookings made yet"
        }else {
            db.collection("UserTrainer").document(SesiT.userTrainerId).get()
                .addOnSuccessListener { documentSnapshot ->
                    var idUser = documentSnapshot.getString("idUser") ?: ""
                    db.collection("users").document(idUser).get()
                        .addOnSuccessListener { documentSnapshot ->
                            var namaUser = documentSnapshot.getString("nama") ?: ""
                            holder._member.text = "Booked by $namaUser"
                        }
                }
        }

    }
}