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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class adapterOngoingG(
    private val listOGym: ArrayList<Gym>,
    private val idLogin: String?,
) : RecyclerView.Adapter<adapterOngoingG.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data: Gym)
        fun delData(pos: Int)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var _tvSession: TextView = itemView.findViewById(R.id.tvSession)
        var _btnCancelGym: Button = itemView.findViewById(R.id.btnCancelGym)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_ongoing, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listOGym.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var gym = listOGym[position]

        holder._tvSession.text = gym.sesi

        val db = Firebase.firestore

        holder._btnCancelGym.setOnClickListener {
            // BLM DIBERI IF KALAU CUMA BISA  LEBIH DARI 24 JAM

            // Get the timestamp for the formattedDate and sesi
            Log.d("TimeComparison", "gym tanggal : ${gym.tanggal} ")
            Log.d("TimeComparison", "gym sesi : ${gym.sesi} ")
            val timestamp = convertDateTimeToTimestamp(gym.tanggal, gym.sesi)
            Log.d("TimeComparison", "timestamp : ${timestamp} ")
            if (isMoreThan24HoursBefore(timestamp)) {
                onItemClickCallback.delData(position)
            } else {
                showAlert(holder.itemView.context, "Pembatalan Gagal", "Booking sesi gym tidak bisa dibatalkan karena sudah lebih dari 24 jam.")
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
                Log.d("Conversion", "Parsed date: $date")
                return Timestamp(date)
            } else {
                Log.e("Conversion", "Failed to parse date: $dateTimeString")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Conversion", "Exception: $e")
        }
        return Timestamp.now() // Return a default timestamp in case of an error
    }


    //sek blm paham cara kerja
    fun isMoreThan24HoursBefore(tanggal: Timestamp?): Boolean {
        if (tanggal != null) {
            Log.d("TimeComparison", "tanggal : $tanggal")
            val timeZone = TimeZone.getTimeZone("Asia/Jakarta") // Set the time zone to Indonesia

            val currentTimeInTimeZone = Calendar.getInstance(timeZone).timeInMillis
            val twentyFourHoursInMillis = 24 * 60 * 60 * 1000

            // Set the time zone for the target time
            val targetTimeInTimeZone = Calendar.getInstance(timeZone).apply {
                time = tanggal.toDate() // Convert the Timestamp to Date
            }.timeInMillis

            val twentyFourHoursBefore = targetTimeInTimeZone - twentyFourHoursInMillis // Corrected calculation

            Log.d("TimeComparison", "currentTime: $currentTimeInTimeZone")
            Log.d("TimeComparison", "twentyFourHoursBefore: $twentyFourHoursBefore")

            return currentTimeInTimeZone < twentyFourHoursBefore
        }
        return false
    }





}