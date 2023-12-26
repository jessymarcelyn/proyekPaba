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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

class adapterGym(
    private val listGym: ArrayList<Gym>,
    private val idLogin: String?
) : RecyclerView.Adapter<adapterGym.ListViewHolder>() {

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
        var _btnBookGym: Button = itemView.findViewById(R.id.btnCancelGym)
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
        holder._tvSlot.text = gym.kuotaSisa.toString() + " / " + gym.kuotaMax.toString() + " Slot"

        val db = Firebase.firestore

        if (gym.kuotaSisa == 0) {
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


//        holder._btnBookGym.setOnClickListener {
//            // booking gym
//            if (idLogin != "0") {
//                val documentRef = db.collection("GymSesi").document(gym.idGym)
//                documentRef.get()
//                    .addOnSuccessListener { documentSnapshot ->
//                        val userIds = documentSnapshot.get("userId") as? List<String> ?: emptyList()
//
//                        //cek user sudah ada atau belum
//                        if (!userIds.contains(idLogin)) {
//                            val documentRef = db.collection("users").document(idLogin.toString())
//                            documentRef.get()
//                                .addOnSuccessListener { documentSnapshot ->
//                                    val member = documentSnapshot.getBoolean("member")
//                                    Log.d("MEMBERR", member.toString())
//                                    Log.d("IDLOGIN", idLogin.toString())
//                                    if (member == true) {
//                                        // Update GymSesi untuk kuota dan userId
//                                        val documentId = gym.idGym
//                                        val newKuotaSisa = gym.kuotaSisa - 1
//                                        val fieldName1 = "kuotaSisa"
//                                        val fieldName2 = "userId"
//
//                                        val updateData = mapOf(
//                                            fieldName1 to newKuotaSisa,
//                                            fieldName2 to FieldValue.arrayUnion(idLogin)
//                                        )
//
//                                        showAlert(
//                                            context,
//                                            "Booking Berhasil",
//                                            "Booking Gym anda pada tanggal ${gym.tanggal} " +
//                                                    " jam ${gym.sesi} telah berhasil, Salam sehat! "
//                                        )
//                                        holder._btnBookGym.isEnabled = false
//                                        db.collection("GymSesi").document(documentId)
//                                            .update(updateData)
//                                            .addOnSuccessListener {
//                                                Log.d(
//                                                    "BookingGym",
//                                                    "berhasil update"
//                                                )
//                                            }
//                                            .addOnFailureListener { e ->
//                                                Log.d(
//                                                    "BookingGym",
//                                                    "gagal update"
//                                                )
//                                            }
//
//                                            .addOnFailureListener { e ->
//                                                Log.e("TAG", "Error adding document", e)
//                                            }
//                                    } else {
//                                        showAlert(
//                                            context,
//                                            "Booking Gagal",
//                                            "Anda belum berlangganan"
//                                        )
//                                    }
//                                }
//                        }else{
//                            Log.d("BookingGym", "userId sudah terdaftar di sesi tersebut")
//                            showAlert(context, "Booking Gagal", "Anda sudah terdaftar pada gym tanggal ${gym.tanggal} " +
//                                    " jam ${gym.sesi}.")
//                        }
//                    }
//                    .addOnFailureListener { e ->
//                        // Handle the failure to get the document
//                        Log.e("TAG", "Error getting document: $e")
//                    }
//            } else {
//                Log.d(
//                    "BookingGym", "user belum login")
//                showAlert(context, "Booking Gagal", "Untuk melakukan booking, silahkan login terlebih dahulu.")
//            }
//        }
//


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