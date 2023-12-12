package uts.c14210065.proyekpaba

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Locale

class adapterGym(
    private val listGym: ArrayList<Gym>,
    private val idLogin: String?,
    private val context: Context
) : RecyclerView.Adapter<adapterGym.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data: User)
        fun delData(pos: Int)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var _tvStartGym: TextView = itemView.findViewById(R.id.tvStartGym)
        var _tvSlot: TextView = itemView.findViewById(R.id.tvSlot)
        var _btnBookGym: Button = itemView.findViewById(R.id.btnBookGym)
        var _tvSlotMax: TextView = itemView.findViewById(R.id.tvSlotMax)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.itemgym, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listGym.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var gym = listGym[position]

        holder._tvStartGym.text = gym.sesi
//        holder._tvEndGym.text = gym.endTime
        holder._tvSlot.text = gym.kuotaSisa.toString()
        holder._tvSlotMax.text = gym.kuotaMax.toString() + " Slot"

        holder._btnBookGym.setOnClickListener {
            // Assuming you have a reference to the Firestore database
            val db = Firebase.firestore

            val gymTanggal = convertDateToTimestamp(gym.tanggal)

            val gymSesi = hashMapOf(
                "sesi" to gym.sesi, "tanggal" to gymTanggal, "userId" to idLogin
            )

            if (idLogin != null) {
                Log.d("iduser", idLogin)
            }

            // booking gym
            if (idLogin != "0") {
                val documentRef = db.collection("GymSesi").document(gym.idGym)
                documentRef.get()
                    .addOnSuccessListener { documentSnapshot ->
                        val userIds = documentSnapshot.get("userId") as? List<String> ?: emptyList()
                        //cek user sudah ada atau belum
                        //JANGAN LUPA BALIKIN
                        if (!userIds.contains(idLogin)) {
//                        if (userIds.contains(idLogin)) {

                            val documentRef = db.collection("users").document(gym.idGym)
                            documentRef.get()
                                .addOnSuccessListener { documentSnapshot ->
                                    val member = documentSnapshot.getBoolean("member")

                                    if(member == true){
                                        db.collection("GymBooking")
                                            .add(gymSesi)
                                            .addOnSuccessListener { documentReference ->
                                                Log.d(
                                                    "BookingGym",
                                                    "berhasil tambahkan user ke database GymBooking"
                                                )

                                                // Update GymSesi untuk kuota dan userId
                                                val documentId = gym.idGym
                                                val newKuotaSisa = gym.kuotaSisa - 1
                                                val fieldName1 = "kuotaSisa"
                                                val fieldName2 = "userId"

                                                val updateData = mapOf(
                                                    fieldName1 to newKuotaSisa,
                                                    fieldName2 to FieldValue.arrayUnion(idLogin)
                                                )

                                                showAlert(context, "Booking Berhasil", "Booking Gym anda pada tanggal ${gym.tanggal} " +
                                                        " jam ${gym.sesi} telah berhasil, Salam sehat! ")

                                                db.collection("GymSesi").document(documentId)
                                                    .update(updateData)
                                                    .addOnSuccessListener {
                                                        Log.d(
                                                            "BookingGym",
                                                            "berhasil update"
                                                        )
                                                    }
                                                    .addOnFailureListener { e ->
                                                        Log.d(
                                                            "BookingGym",
                                                            "gagal update"
                                                        )
                                                    }
                                            }
                                            .addOnFailureListener { e ->
                                                Log.e("TAG", "Error adding document", e)
                                            }
                                    }
                                    else{
                                        showAlert(context, "Booking Gagal", "Anda belum berlangganan")
                                    }
                                }

                        } else {
                            Log.d("BookingGym", "userId sudah terdaftar di sesi tersebut")
                            showAlert(context, "Booking Gagal", "Anda sudah terdaftar pada gym tanggal ${gym.tanggal} " +
                                    " jam ${gym.sesi}.")
                        }
                    }
                    .addOnFailureListener { e ->
                        // Handle the failure to get the document
                        Log.e("TAG", "Error getting document: $e")
                    }
            } else {
                Log.d(
                    "BookingGym", "user belum login")
                showAlert(context, "Booking Gagal", "Untuk melakukan booking, silahkan login terlebih dahulu.")
            }
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