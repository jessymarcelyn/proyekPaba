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
        fun onItemClicked(data: User)
        fun delData(pos: Int)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var _tvStartSesiTrainer: TextView = itemView.findViewById(R.id.tvSessionO)
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
                            ColorStateList.valueOf(Color.WHITE)
                    } else {
                        Log.d("qqqq", "masuk fully booked")
                        holder._btnBookSesi.text = "FULLY BOOKED"
                        holder._btnBookSesi.isActivated = false
                        holder._btnBookSesi.backgroundTintMode = PorterDuff.Mode.SRC_IN
                        holder._btnBookSesi.backgroundTintList =
                            ColorStateList.valueOf(Color.GRAY)
                    }
                }
        } else {
            holder._btnBookSesi.text = "BOOK SEKARANG"
            holder._btnBookSesi.setOnClickListener {

                // Update GymSesi untuk kuota dan userId
                val updateData = mapOf(
                    "userTrainerId" to sesiTrainer.userTrainerId
                )
                db.collection("JadwalTrainer").document(sesiTrainer.idJadwal)
                    .update(updateData)
                    .addOnSuccessListener {
                        Log.d(
                            "BookingSesi",
                            "berhasil update usertrainerId di jadwaltrainer"
                        )
                        val documentRef = db.collection("UserTrainer").document(sesiTrainer.userTrainerId)
                        documentRef.get()
                            .addOnSuccessListener { documentSnapshot ->
                                Log.d("BookingSesi", "berhasil update kuota sesi")

                                var sisaSesi =
                                    documentSnapshot.getLong("sisaSesi")?.toInt() ?: 0
                                val updatedSisaSesi = sisaSesi - 1

                                val updateData = mapOf(
                                    "sisaSesi" to updatedSisaSesi
                                )
                                db.collection("UserTrainer").document(sesiTrainer.userTrainerId)
                                    .update(updateData)
                                    .addOnSuccessListener {
                                        Log.d("BookingGym", "berhasil update")
                                        showAlert(
                                            holder.itemView.context,
                                            "Booking Berhasil",
                                            "Booking sesi dengan personal trainer anda pada tanggal ${sesiTrainer.tanggal} " +
                                                    " jam ${sesiTrainer.sesi} telah berhasil, Salam sehat! "
                                        )
                                    }
                                    .addOnFailureListener { e ->
                                        Log.d("BookingGym", "gagal update")
                                    }
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.d(
                            "BookingGym",
                            "gagal update"
                        )
                    }

            }
            holder._btnBookSesi.isActivated = true  // Set to true if you want it to be interactive
            holder._btnBookSesi.backgroundTintMode = PorterDuff.Mode.SRC_IN
            holder._btnBookSesi.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#C9F24D"))


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