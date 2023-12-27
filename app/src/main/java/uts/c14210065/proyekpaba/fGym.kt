package uts.c14210065.proyekpaba

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [fGym.newInstance] factory method to
 * create an instance of this fragment.
 */
class fGym : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_f_gym, container, false)
    }

    private lateinit var _rvGym: RecyclerView
    private var arGym = arrayListOf<Gym>()
    private var lastClickedButton: Button? = null
    lateinit var dayDate: Date
    lateinit var idLogin: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        idLogin = arguments?.getString("userId").toString()
        Log.d("trainer", "Gym" + idLogin)
        _rvGym = view.findViewById(R.id.rvGym)

        val _btnd1 = view.findViewById<Button>(R.id.btnd1)
        val _btnd2 = view.findViewById<Button>(R.id.btnd2)
        val _btnd3 = view.findViewById<Button>(R.id.btnd3)
        val _btnd4 = view.findViewById<Button>(R.id.btnd4)
        val _btnd5 = view.findViewById<Button>(R.id.btnd5)
        val _btnd6 = view.findViewById<Button>(R.id.btnd6)
        val _btnd7 = view.findViewById<Button>(R.id.btnd7)

        // Get the current date
        var currentDate = Calendar.getInstance().time
        dayDate = currentDate

        val buttons = arrayOf(_btnd1, _btnd2, _btnd3, _btnd4, _btnd5, _btnd6, _btnd7)

        // Ketika awal tombol dengan tanggal hari ini otomatis tertekan.
        TampilkanData()
        buttons[0].setBackgroundColor(Color.parseColor("#C9F24D"))
        lastClickedButton = buttons[0]

        for (i in buttons.indices) {
            val day = Calendar.getInstance()
            day.time = currentDate
            day.add(Calendar.DATE, i)

            val formattedDate = SimpleDateFormat("EEE\ndd MMM", Locale("id", "ID")).format(day.time)

            dayTextSet(buttons[i], formattedDate, size1, size2)

            buttons[i].setOnClickListener {
                // Reset the background color of the last clicked button to white
                lastClickedButton?.setBackgroundColor(Color.WHITE)

                // Set the background color of the current clicked button to purple
                buttons[i].setBackgroundColor(Color.parseColor("#C9F24D"))

                // Update the last clicked button
                lastClickedButton = buttons[i]

                // tanggal di button
                val calendar = Calendar.getInstance()
                calendar.time = currentDate
                calendar.add(Calendar.DATE, i)
                dayDate = calendar.time

                TampilkanData()
            }
        }

        _rvGym.layoutManager = LinearLayoutManager(context)
        val adapterP = adapterGym(arGym, idLogin)
        _rvGym.adapter = adapterP

        adapterP.setOnItemClickCallback(object : adapterGym.OnItemClickCallback {
            //            Log.d("toast", "dependee")
            override fun onItemClicked(data: Gym) {
//                Toast.makeText(this@fClass, data.name, Toast.LENGTH_LONG). show()
            }

            override fun delData(pos: Int) {
                TODO("Not yet implemented")
            }

            override fun bookGym(data: Gym) {
                if (idLogin != "0") {
                    val documentRef =
                        db.collection("users").document(idLogin.toString())
                    documentRef.get()
                        .addOnSuccessListener { documentSnapshot ->
                            val member = documentSnapshot.getBoolean("member")
                            Log.d("MEMBERR", member.toString())
                            Log.d("IDLOGIN", idLogin.toString())
                            if (member == true) {
                                // Update GymSesi untuk kuota dan userId
                                val documentId = data.idGym
                                val newKuotaSisa = data.kuotaSisa - 1
                                val fieldName1 = "kuotaSisa"
                                val fieldName2 = "userId"

                                val updateData = mapOf(
                                    fieldName1 to newKuotaSisa,
                                    fieldName2 to FieldValue.arrayUnion(idLogin)
                                )

                                showAlert(
                                    requireContext(),
                                    "Booking Berhasil",
                                    "Booking Gym anda pada tanggal ${data.tanggal} " +
                                            " jam ${data.sesi} telah berhasil, Salam sehat! "
                                )
//                                holder._btnBookGym.isEnabled = false
                                db.collection("GymSesi").document(documentId)
                                    .update(updateData)
                                    .addOnSuccessListener {
                                        Log.d(
                                            "BookingGym",
                                            "berhasil update"
                                        )
                                        TampilkanData()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.d(
                                            "BookingGym",
                                            "gagal update"
                                        )
                                    }

                                    .addOnFailureListener { e ->
                                        Log.e("TAG", "Error adding document", e)
                                    }
                            } else {
                                showAlert(
                                    requireContext(),
                                    "Booking Gagal",
                                    "Anda belum berlangganan membership gym"
                                )
                            }

                        }
                        .addOnFailureListener { e ->
                            // Handle the failure to get the document
                            Log.e("TAG", "Error getting document: $e")
                        }
                } else {
                    Log.d(
                        "BookingGym", "user belum login"
                    )
                    showAlert(
                        requireContext(),
                        "Booking Gagal",
                        "Silahkan login terlebih dahulu."
                    )
                }
            }
        })
    }

    fun dayTextSet(button: Button, text: String, size1: Int, size2: Int) {
        val spannableString = SpannableString(text)

        spannableString.setSpan(
            AbsoluteSizeSpan(size1, true),
            0,
            text.indexOf('\n'),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            AbsoluteSizeSpan(size2, true),
            text.indexOf('\n') + 1,
            spannableString.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        button.text = spannableString

        button.setOnClickListener {
            button.isEnabled = false
        }
    }

    val db = Firebase.firestore

    private fun TampilkanData() {
        db.collection("GymSesi").get().addOnSuccessListener { result ->
            arGym.clear()
            for (document in result) {
                var Gymid = document.id
                val tanggal = (document["tanggal"] as? Timestamp)?.toDate()
                val kuotaSisa = document.getLong("kuotaSisa")?.toInt() ?: 0

                if (cekDate(tanggal, dayDate)) {
                    val kuotaMax = document.getLong("kuotaMax")?.toInt() ?: 0
                    val timestamp = document.getTimestamp("tanggal")!!

                    val calendar = Calendar.getInstance()
                    calendar.time = tanggal

                    // ambil jam dan menit dari timestamp tanggal di database
                    val jam = calendar.get(Calendar.HOUR_OF_DAY)
                    val menit = calendar.get(Calendar.MINUTE)

                    //agar jadi 08.00 atau 17.00
                    val formatJam = String.format("%02d", jam)
                    val formatMenit = String.format("%02d", menit)

                    val sesi = "$formatJam:$formatMenit"

                    val userId =
                        (document["userId"] as? List<*>)?.map { it.toString() } ?: emptyList()

                    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
                    dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
                    val formattedDate = dateFormat.format(tanggal)
                    val currentDate = dateFormat.format(Date())
                    Log.d("formatted tanggal : ", formattedDate)

                    //jam sekarang
                    val currentTime = Calendar.getInstance()
                    val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
                    val currentMinute = currentTime.get(Calendar.MINUTE)

//                    arGym.add(
//                        Gym(
//                            Gymid,
//                            formattedDate,
//                            sesi,
//                            kuotaMax,
//                            kuotaSisa,
//                            ArrayList(userId)
//                        )
//                    )
                    // kalau jam hari ini sudah lewat berarti tidak masuk ongoing
                    if (formattedDate == currentDate) {
                        Log.d("ooo", "masuk1")
                        Log.d("ooo", "jam : $jam")
                        Log.d("ooo", "currentHour : $currentHour")
                        if (jam > currentHour) {
                            Log.d("ooo", "masuk2")
                            Log.d("ooo", "menit : $menit")
                            Log.d("ooo", "currentMinute : $currentMinute")
                            arGym.add(
                                Gym(
                                    Gymid,
                                    formattedDate,
                                    sesi,timestamp,
                                    kuotaMax,
                                    kuotaSisa,
                                    ArrayList(userId)
                                )
                            )
                        }
                        if (jam == currentHour) {
                            if (menit > currentMinute) {
                                Log.d("ooo", "masuk3")
                                arGym.add(
                                    Gym(
                                        Gymid,
                                        formattedDate,
                                        sesi,timestamp,
                                        kuotaMax,
                                        kuotaSisa,
                                        ArrayList(userId)
                                    )
                                )
                            }
                        }
                    } else {
                        arGym.add(
                            Gym(
                                Gymid,
                                formattedDate,
                                sesi,timestamp,
                                kuotaMax,
                                kuotaSisa,
                                ArrayList(userId)
                            )
                        )
                    }
                }
            }
            arGym.sortBy { it.sesi }
            _rvGym.adapter?.notifyDataSetChanged()
        }.addOnFailureListener { e ->
            Log.e("haes", "Error fetching data from Firebase", e)
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

    fun cekDate(timestamp: Date?, btnDate: Date): Boolean {
        val dateFormat = SimpleDateFormat("dd MMM", Locale("id", "ID"))
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")

        val formattedDate = dateFormat.format(timestamp)
        val date = dateFormat.format(btnDate)

        Log.d("cektanggal", "format" + formattedDate)
        Log.d("cektanggal", "button " + date)

        return formattedDate == date
    }

    companion object {
        val size1 = 18
        val size2 = 15

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment fGym.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fGym().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}