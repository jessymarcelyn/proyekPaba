package uts.c14210065.proyekpaba

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class adapterOngoingT(
    private val listOTrainer: ArrayList<SesiT>,
    private val idLogin: String?,
) : RecyclerView.Adapter<adapterOngoingT.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data: Gym)
        fun delData(pos: Int)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var _tvSessionOT: TextView = itemView.findViewById(R.id.tvSessionOT)
        var _tvCoachNameO: TextView = itemView.findViewById(R.id.tvCoachNameO)
        var _btnCancelGym: Button = itemView.findViewById(R.id.btnCancelGym)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_ongoing_trainer, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listOTrainer.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var sesi = listOTrainer[position]

        holder._tvSessionOT.text = sesi.sesi

        val db = Firebase.firestore
        db.collection("Trainer").get().addOnSuccessListener { result ->
            for (document in result) {

                if (document.id == sesi.trainerId) {
                    var namaTrainer = document.getString("nama") ?: ""
                    holder._tvCoachNameO.text = namaTrainer.toString()
                }
            }
        }

        holder._btnCancelGym.setOnClickListener {
            val timestamp = convertDateTimeToTimestamp(sesi.tanggal, sesi.sesi)
            Log.d("uuu", "timestamp : ${timestamp} ")
            if (isMoreThan24HoursBefore(timestamp)) {
                onItemClickCallback.delData(position)
            } else {
                showAlert(holder.itemView.context, "Pembatalan Gagal", "Booking sesi pt tidak bisa dibatalkan karena sudah lebih dari 24 jam.")
            }

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

    fun convertDateTimeToTimestamp(dateString: String, timeString: String): Timestamp {
        val dateTimeString = "$dateString $timeString"
        val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
        try {
            val date = dateFormat.parse(dateTimeString)
            if (date != null) {
                return Timestamp(date)
            } else {
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Timestamp.now()
    }

    fun isMoreThan24HoursBefore(tanggal: Timestamp?): Boolean {
        if (tanggal != null) {
            Log.d("uuu", "tanggal : $tanggal")
            val timeZone = TimeZone.getTimeZone("Asia/Jakarta")

            val currentTimeInTimeZone = Calendar.getInstance(timeZone).timeInMillis
            val twentyFourHoursInMillis = 24 * 60 * 60 * 1000

            // set timezone
            val targetTimeInTimeZone = Calendar.getInstance(timeZone).apply {
                time = tanggal.toDate() // Convert timestamp ke date
            }.timeInMillis

            val twentyFourHoursBefore = targetTimeInTimeZone - twentyFourHoursInMillis

            Log.d("uuu", "currentTime: $currentTimeInTimeZone")
            Log.d("uuu", "twentyFourHoursBefore: $twentyFourHoursBefore")

            return currentTimeInTimeZone < twentyFourHoursBefore
        }
        return false
    }





}