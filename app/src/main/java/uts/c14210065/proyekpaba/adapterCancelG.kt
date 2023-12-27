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
import java.util.Calendar
import java.util.Date

class adapterCancelG(
    private val listGym: ArrayList<Gym>,
    private val idLogin: String?
) : RecyclerView.Adapter<adapterCancelG.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data: Gym)
        fun delData(pos: Int)

        fun bookGym(data : Gym)
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

        } else if (gym.userId.contains(idLogin)) {
            holder._btnBookGym.text = "BOOKED"
            holder._btnBookGym.isActivated = false
            holder._btnBookGym.backgroundTintMode = PorterDuff.Mode.SRC_IN
            holder._btnBookGym.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
        } else {
            holder._btnBookGym.text = "BOOK SEKARANG"
            holder._btnBookGym.setOnClickListener {
                onItemClickCallback.bookGym(listGym[position])

            }

            holder._btnBookGym.isActivated = true
            holder._btnBookGym.backgroundTintMode = PorterDuff.Mode.SRC_IN
            holder._btnBookGym.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#C9F24D"))
        }
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