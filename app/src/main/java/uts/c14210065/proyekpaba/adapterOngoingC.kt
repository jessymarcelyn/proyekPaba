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
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class adapterOngoingC(
    private val listOClass: ArrayList<GymClass>,
    private val idLogin: String?,
) : RecyclerView.Adapter<adapterOngoingC.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data: Gym)
        fun delData(pos: Int)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var _tvNameClassOC: TextView = itemView.findViewById(R.id.tvNameClassOC)
        var _tvCoachOC: TextView = itemView.findViewById(R.id.tvCoachOC)
        var _tvTimeOC: TextView = itemView.findViewById(R.id.tvTimeOC)
        var _tvLevelOC: TextView = itemView.findViewById(R.id.tvLevelOC)
        var _btnCancelGym: Button = itemView.findViewById(R.id.btnCancelClass)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_ongoing_class, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listOClass.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var classo = listOClass[position]

        holder._tvCoachOC.text = "With " + classo.coach
        holder._tvNameClassOC.text = classo.name
        holder._tvLevelOC.text = classo.level

        val db = Firebase.firestore


        val selectedDate = classo.timestamp?.toDate()?.time ?: 0
        val timeFormat = android.icu.text.SimpleDateFormat("HH:mm ", Locale("id", "ID"))
        timeFormat.timeZone = android.icu.util.TimeZone.getTimeZone("Asia/Jakarta")
        val waktu = timeFormat.format(Time(selectedDate))
        holder._tvTimeOC.text = waktu

        holder._btnCancelGym.setOnClickListener {
            if (isMoreThan24HoursBefore(classo.timestamp)) {
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