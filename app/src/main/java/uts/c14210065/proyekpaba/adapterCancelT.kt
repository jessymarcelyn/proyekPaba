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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class adapterCancelT(
    private val listOTrainer: ArrayList<SesiT>,
    private val idLogin: String?,
) : RecyclerView.Adapter<adapterCancelT.ListViewHolder>() {

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
        var _tvSessionOT: TextView = itemView.findViewById(R.id.tvSessionOT)
        var _tvCoachNameO: TextView = itemView.findViewById(R.id.tvCoachNameO)
        var _btnBookSesi: Button = itemView.findViewById(R.id.btnCancelGym)
        var _tanggalTrainer: TextView = itemView.findViewById(R.id.tanggalTrainer)
        var _cancelTrainer: ConstraintLayout = itemView.findViewById(R.id.cancelTrainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_cancel_trainer, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listOTrainer.size
    }

    private lateinit var userTrainerIdd: String
    private lateinit var idTrainer: String
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var sesiTrainer = listOTrainer[position]

        holder._tvSessionOT.text = sesiTrainer.sesi
        holder._tanggalTrainer.text = sesiTrainer.tanggal

        val db = Firebase.firestore
        val documentId = sesiTrainer.idJadwal

        val timestamp = convertDateTimeToTimestamp(sesiTrainer.tanggal, sesiTrainer.sesi)
        val currentTimestamp = Timestamp(Calendar.getInstance().time)

        holder._cancelTrainer.layoutParams.height = 470
        holder._btnBookSesi.visibility = View.VISIBLE

        db.collection("Trainer").get().addOnSuccessListener { result ->
            for (document in result) {
                if (document.id == sesiTrainer.trainerId) {
                    var namaTrainer = document.getString("nama") ?: ""
                    holder._tvCoachNameO.text = namaTrainer.toString()
                }
            }
        }

        if (timestamp < currentTimestamp) {
            holder._btnBookSesi.visibility = View.GONE
            holder._cancelTrainer.layoutParams.height = 350
        } else {
            db.collection("UserTrainer")
                .whereEqualTo("idUser", idLogin)
                .whereEqualTo("idTrainer", sesiTrainer.trainerId)
                .get()
                .addOnSuccessListener { userTrainerSnapshot ->
                    for (userTrainerDocument in userTrainerSnapshot) {
                        userTrainerIdd  = userTrainerDocument.id
                        queryJadwalTrainer(sesiTrainer, holder, userTrainerIdd)
                    }

                }
        }
    }


    private fun queryJadwalTrainer(sesiTrainer: SesiT, holder: ListViewHolder, userTrainerIddd :String) {
        val db = Firebase.firestore
        db.collection("JadwalTrainer").document(sesiTrainer.idJadwal).get()
            .addOnSuccessListener { documentSnapshot ->
                val idUser = documentSnapshot.getString("idUser") ?: ""
                val userTrainerId = documentSnapshot.getString("userTrainerId") ?: ""
                Log.d("qqqq", "idUser : ${idUser}")

                Log.d("fgfg", "userTrainerId : $userTrainerId")
                Log.d("fgfg", "userTrainerIdd2 : $userTrainerIdd")
                if (userTrainerId == userTrainerIddd) {
                    Log.d("qqqq", "masuk booked")
                    holder._btnBookSesi.text = "BOOKED"
                    holder._btnBookSesi.isActivated = false
                    holder._btnBookSesi.backgroundTintMode = PorterDuff.Mode.SRC_IN
                    holder._btnBookSesi.backgroundTintList = ColorStateList.valueOf(Color.GRAY)

                } else if (userTrainerId == "") {
                    holder._btnBookSesi.text = "BOOK SEKARANG"
                    holder._btnBookSesi.setOnClickListener {
                        onItemClickCallback.bookSesi(sesiTrainer)

                    }
                    holder._btnBookSesi.isActivated = true  // Set to true if you want it to be interactive
                    holder._btnBookSesi.backgroundTintMode = PorterDuff.Mode.SRC_IN
                    holder._btnBookSesi.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#C9F24D"))

                } else {
                    Log.d("qqqq", "masuk fully booked")
                    holder._btnBookSesi.text = "FULL BOOKED"
                    holder._btnBookSesi.isActivated = false
                    holder._btnBookSesi.backgroundTintMode = PorterDuff.Mode.SRC_IN
                    holder._btnBookSesi.backgroundTintList =
                        ColorStateList.valueOf(Color.GRAY)
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