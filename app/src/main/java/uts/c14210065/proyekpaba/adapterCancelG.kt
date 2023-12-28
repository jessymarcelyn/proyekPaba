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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class adapterCancelG(
    private val listGym: ArrayList<Gym>,
    private val idLogin: String?
) : RecyclerView.Adapter<adapterCancelG.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data: Gym)
        fun delData(pos: Int)

        fun recancel(post : Int)
        fun delGym(data : Gym)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var _tvStartGym: TextView = itemView.findViewById(R.id.tvSessionOT)
        var _tvSlot: TextView = itemView.findViewById(R.id.tvSlot)
        var _tvTanggalGym: TextView = itemView.findViewById(R.id.tvTanggalGym)
        var _btnBookGym: Button = itemView.findViewById(R.id.btnCancelGym)
        var _cancelGym: ConstraintLayout = itemView.findViewById(R.id.cancelGym)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_cancel_gym, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listGym.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var gym = listGym[position]

        holder._tvStartGym.text = gym.sesi

        holder._tvSlot.text = gym.kuotaSisa.toString() + " / " + gym.kuotaMax.toString() + " Slot"
        holder._tvTanggalGym.text = gym.tanggal
        val db = Firebase.firestore

        holder._btnBookGym.visibility = View.VISIBLE
        holder._cancelGym.layoutParams.height = 500

        val currentTimestamp = Timestamp(Calendar.getInstance().time)
        val dateNow = Date(currentTimestamp.seconds * 1000 + currentTimestamp.nanoseconds / 1000000)

        val dateGym = Date(gym.timestamp.seconds * 1000 + gym.timestamp.nanoseconds / 1000000)
        Log.d("klkl", "currentTimestamp : $dateNow")
        Log.d("klkl", "gym.timestamp : ${dateGym}")
        if(gym.timestamp < currentTimestamp){
            Log.d("xzxz", "masuk1")
            holder._btnBookGym.visibility = View.GONE
            holder._cancelGym.layoutParams.height = 350
        }
        else if (gym.kuotaSisa == 0) {

            holder._btnBookGym.text = "FULL BOOKED"
            holder._btnBookGym.isActivated = false
            holder._btnBookGym.backgroundTintMode = PorterDuff.Mode.SRC_IN
            holder._btnBookGym.backgroundTintList = ColorStateList.valueOf(Color.GRAY)

//        } else if (gym.userId.contains(idLogin)) {
//            onItemClickCallback.delGym(listGym[position])


//            holder._btnBookGym.text = "BOOKED"
//            holder._btnBookGym.isActivated = false
//            holder._btnBookGym.backgroundTintMode = PorterDuff.Mode.SRC_IN
//            holder._btnBookGym.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
        } else {

            holder._btnBookGym.text = "BOOK SEKARANG"
            holder._btnBookGym.isActivated = true
            holder._btnBookGym.backgroundTintMode = PorterDuff.Mode.SRC_IN
            holder._btnBookGym.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#C9F24D"))
            val timestamp = convertDateTimeToTimestamp(gym.tanggal, gym.sesi)
            holder._btnBookGym.setOnClickListener {
                if (isMoreThan24HoursBefore(timestamp)) {
                    onItemClickCallback.recancel(position)
                } else {
                    showAlert(holder.itemView.context, "Pembatalan Gagal", "Booking ulang class tidak bisa karena kurang dari 24 jam.")
                }
            }


        }
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

    fun showAlert(context: Context, title: String, message: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, which ->
            // Handle the "OK" button click if needed
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun setOnItemClickCallback(onItemClickCallback: adapterGym.OnItemClickCallback) {

    }

}