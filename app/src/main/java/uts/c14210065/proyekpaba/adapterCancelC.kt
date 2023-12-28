package uts.c14210065.proyekpaba

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import java.sql.Time
import java.util.Calendar
import java.util.Date
import java.util.Locale

class adapterCancelC (
    private val listClass: ArrayList<GymClass>,
    private val idLogin: String?
//    ,
//    private val context: Context
): RecyclerView.Adapter<adapterCancelC.ListViewHolder>(){
    private lateinit var  onItemClickCallback: OnItemClickCallback


    interface OnItemClickCallback {
        fun  onItemClicked(data : GymClass)
        fun delData(pos:Int)
        fun recancel(post : Int)
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
        var _tanggal: TextView =  itemView.findViewById(R.id.tvTanggalCancelC)
        var _cancelClass: ConstraintLayout = itemView.findViewById(R.id.cancelClass)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_cancel_class,parent,false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listClass.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var gymClass = listClass[position]

        val timestamp: Timestamp? = gymClass.timestamp// Your Timestamp object


        val date: Date = timestamp!!.toDate()
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
        val formattedDate = dateFormat.format(date)
        val currentTimestamp = Timestamp(Calendar.getInstance().time)

        holder._cancelClass.layoutParams.height = 500
        holder._btnBook.visibility = View.VISIBLE

        holder._nama.text = gymClass.name
        holder._tanggal.text = formattedDate


        if(gymClass.timestamp!! < currentTimestamp){
            Log.d("xzxz", "masuk1")
            holder._btnBook.visibility = View.GONE
            holder._cancelClass.layoutParams.height = 350
        }
        else if(gymClass.capacity == 0){
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
//                onItemClickCallback.bookClass(listClass[position])
                if (isMoreThan24HoursBefore(timestamp)) {
                    onItemClickCallback.recancel(position)
                } else {
                    showAlert(holder.itemView.context, "Pembatalan Gagal", "Booking ulang sesi gym tidak bisa dibatalkan karena kurang dari 24 jam.")
                }

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

    fun convertDateTimeToTimestamp(dateString: String, timeString: String): Timestamp {
        val dateTimeString = "$dateString $timeString"
        val dateFormat = java.text.SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
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
            val timeZone = java.util.TimeZone.getTimeZone("Asia/Jakarta")

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
}