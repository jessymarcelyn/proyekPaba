package uts.c14210065.proyekpaba

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.sql.Time
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
 * Use the [fClass.newInstance] factory method to
 * create an instance of this fragment.
 */
class fClass : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    val db = Firebase.firestore

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
        return inflater.inflate(R.layout.fragment_f_class, container, false)
    }

    private lateinit var _rvClass : RecyclerView
    private var arClass = arrayListOf<GymClass>()
    private var lastClickedButton: Button? = null
    lateinit var dayDate : Date
    lateinit var idLogin : String


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        idLogin = arguments?.getString("userId").toString()
        Log.d("userId", "Class" + idLogin)
        _rvClass = view.findViewById(R.id.rvClass)

        val _btnd1 = view.findViewById<Button>(R.id.btnMon)
        val _btnd2 = view.findViewById<Button>(R.id.btnTue)
        val _btnd3 = view.findViewById<Button>(R.id.btnWed)
        val _btnd4 = view.findViewById<Button>(R.id.btnThu)
        val _btnd5 = view.findViewById<Button>(R.id.btnFri)
        val _btnd6 = view.findViewById<Button>(R.id.btnSat)
        val _btnd7 = view.findViewById<Button>(R.id.btnSun)

        // Get the current date
        var currentDate = Calendar.getInstance().time
        dayDate = currentDate

        val buttons = arrayOf(_btnd1, _btnd2, _btnd3, _btnd4, _btnd5, _btnd6, _btnd7)
        ReadData(dayDate)
        buttons[0].setBackgroundColor(Color.parseColor("#C9F24D"))
        lastClickedButton = buttons[0]

        for (i in buttons.indices) {
            val day = Calendar.getInstance()
            day.time = currentDate
            day.add(Calendar.DATE, i)

            val formattedDate = SimpleDateFormat("EEE\ndd MMM", Locale("id", "ID")).format(day.time)

            dayTextSet(buttons[i], formattedDate, fGym.size1, fGym.size2)

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

                ReadData(dayDate)
            }
        }
//        ReadData()
        _rvClass.layoutManager = LinearLayoutManager(context)
        val adapterP = adapterGymClass(arClass, idLogin)
        _rvClass.adapter = adapterP

        adapterP.setOnItemClickCallback(object : adapterGymClass.OnItemClickCallback{
            //            Log.d("toast", "dependee")
            override fun onItemClicked(data: GymClass) {
//                Toast.makeText(this@fClass, data.name, Toast.LENGTH_LONG). show()
                Log.d("toast", "kepencet")
                Toast.makeText(requireContext(),"HALO", Toast.LENGTH_LONG).show()

            }

            override fun delData(pos: Int) {
                TODO("Not yet implemented")
            }

            override fun bookClass(data: GymClass) {
                CreateData(data)
            }
        })

    }

    fun dayTextSet(button: Button, text: String, size1: Int, size2: Int) {
        val spannableString = SpannableString(text)

        spannableString.setSpan(AbsoluteSizeSpan(size1, true), 0, text.indexOf('\n'), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(AbsoluteSizeSpan(size2, true), text.indexOf('\n') + 1, spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        button.text = spannableString

        button.setOnClickListener {
            button.isEnabled = false

        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun ReadData(date: Date) {
        db.collection("Class").get().addOnSuccessListener { result ->
            arClass.clear()
            for (document in result) {
                val selectedDate = (document["tanggal"] as? Timestamp)?.toDate()?.time ?: 0
                Log.d("sDate",selectedDate.toString())
                if(cekDate(selectedDate, date)) {
                    val id = document.id
                    val nama = document.getString("nama") ?:""
                    val kapasitas = document.getLong("kapasitas")?.toInt() ?: 0

                    val pelatih = document.getString("pelatih" )?:""
                    val durasi = document.getLong("kapasitas")?.toInt() ?: 0
                    val level = document.getString("level") ?:""
                    val arrUser = document.get("userId") as? List<String> ?: emptyList()


                    val timestamp = document.getTimestamp("tanggal")

                    val schedule = document.getTimestamp("tanggal")?.toDate()?.time // Assuming it's in milliseconds
                    val currentTimestamp = System.currentTimeMillis()

                    // Check if the timestamp is in the future
                    if (schedule != null && schedule > currentTimestamp) {
                        arClass.add(GymClass(id, nama, kapasitas, durasi, pelatih, timestamp, level, arrUser))
                    }

                    Log.d("haes", "Document data: ${document.data}")
                }
            }
            arClass.sortBy { it.timestamp}
            _rvClass.adapter?.notifyDataSetChanged()
        }.addOnFailureListener { e ->
            Log.e("haes", "Error fetching data from Firebase", e)
        }
    }

    fun cekDate(timestamp: Long, btnDate: Date): Boolean {
        val dateFormat = SimpleDateFormat("dd MMM", Locale("id", "ID"))
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")

        val formattedDate = dateFormat.format(Date(timestamp))
        val date = dateFormat.format(btnDate)

        Log.d("cektanggal", "format" + formattedDate)
        Log.d("cektanggal", "button " + date)

        return formattedDate == date
    }
    fun CreateData(data : GymClass) {
        Log.d("idlogin", idLogin)
        if (idLogin != "0") {
            db.collection("Class")
                .document(data.idClass).get()
                .addOnSuccessListener { document ->
                    Log.d("class", "masuk")
                    val userIds = document.get("userId") as? List<String> ?: emptyList()

                    if (!userIds.contains(idLogin)) {
                        db.collection("users").document(idLogin).get()
                            .addOnSuccessListener { document ->
                                val member = document.getBoolean("member")
                                if (member == true) {
                                    Log.d("cek user", "masuk")

                                    val documentId = data.idClass
                                    val kapasitas = data.capacity - 1
                                    val bookedClass = hashMapOf(
                                        "kapasitas" to kapasitas,
                                        "userId" to FieldValue.arrayUnion(idLogin)
                                    )

                                    showAlert(
                                        requireContext(),
                                        "Booking Berhasil",
                                        "Booking Class anda telah berhasil, Salam sehat! "
                                    )

                                    db.collection("Class").document(documentId)
                                        .update(bookedClass)
                                        .addOnSuccessListener {
                                            Log.d(
                                                "Booking Class",
                                                "berhasil update"
                                                
                                            )
                                            ReadData(dayDate)
//                                            onBookingSuccessListener.onBookingSuccess()
                                        }
                                        .addOnFailureListener { e ->
                                            Log.d(
                                                "Booking Class",
                                                "gagal update"
                                            )
                                        }
                                } else {
                                    showAlert(
                                        requireContext(),
                                        "Booking Gagal",
                                        "Anda belum berlangganan membership gym"
                                    )
                                }
                            }
                    } else {
                        Log.d("Booking Class", "userId sudah terdaftar di sesi tersebut")
                        showAlert(requireContext(), "Booking Gagal", "Anda sudah terdaftar pada class tersebut")
                    }

                }
        }else {
                Log.d(
                    "Booking Class", "user belum login"
                )
                context?.let {
                    showAlert(
                        it!!,
                        "Booking Gagal",
                        "Silahkan login terlebih dahulu."
                    )
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


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment fClass.
         */
        val size1 = 18
        val size2 = 15
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fClass().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
