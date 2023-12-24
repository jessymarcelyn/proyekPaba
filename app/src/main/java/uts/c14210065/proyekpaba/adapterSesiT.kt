package uts.c14210065.proyekpaba

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Locale

class adapterSesiT(
    private val listSesiT: ArrayList<SesiT>,
    private val idLogin: String?
) : RecyclerView.Adapter<adapterSesiT.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data: SesiT)
        fun delData(pos: Int)
        fun bookSesi(data: SesiT)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var _tvStartSesiTrainer: TextView = itemView.findViewById(R.id.tvSessionOT)
        var _btnBookSesi: Button = itemView.findViewById(R.id.btnCancelGym)
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

        val db = Firebase.firestore
        val documentId = sesiTrainer.idJadwal

        Log.d("qqq", "-------------------------------------------")
        Log.d("qqqq", "jadwal id : ${sesiTrainer.idJadwal}")
        Log.d("qqqq", "sesiTrainer.userTrainerId : ${sesiTrainer.userTrainerId}")
        if (sesiTrainer.userTrainerIdSesi != "") {
            db.collection("UserTrainer").document(sesiTrainer.userTrainerIdSesi).get()
                .addOnSuccessListener { documentSnapshot ->
                    val idUser = documentSnapshot.getString("idUser") ?: ""
                    Log.d("qqqq", "idUser : ${idUser}")
                    if (idUser == idLogin) {
                        Log.d("qqqq", "masuk booked")
                        holder._btnBookSesi.text = "BOOKED"
                        holder._btnBookSesi.isActivated = false
                        holder._btnBookSesi.backgroundTintMode = PorterDuff.Mode.SRC_IN
                        holder._btnBookSesi.backgroundTintList =
                            ColorStateList.valueOf(Color.GRAY)
                    } else {
                        Log.d("qqqq", "masuk fully booked")
                        holder._btnBookSesi.text = "FULL BOOKED"
                        holder._btnBookSesi.isActivated = false
                        holder._btnBookSesi.backgroundTintMode = PorterDuff.Mode.SRC_IN
                        holder._btnBookSesi.backgroundTintList =
                            ColorStateList.valueOf(Color.GRAY)
                    }
                }
        } else {
            holder._btnBookSesi.text = "BOOK SEKARANG"
            holder._btnBookSesi.setOnClickListener {
                onItemClickCallback.bookSesi(sesiTrainer)

            }
            holder._btnBookSesi.isActivated = true  // Set to true if you want it to be interactive
            holder._btnBookSesi.backgroundTintMode = PorterDuff.Mode.SRC_IN
            holder._btnBookSesi.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#C9F24D"))


        }
    }


    fun showAlert(context: Context, title: String, message: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, which ->
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