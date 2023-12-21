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
import java.util.Locale

class adapterOngoingG(
    private val listOGym: ArrayList<Gym>,
    private val idLogin: String?
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
        holder._btnCancelGym.setOnClickListener{
            onItemClickCallback.delData(position)
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


}