package uts.c14210065.proyekpaba

import android.annotation.SuppressLint
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

    val db = FirebaseFirestore.getInstance()

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

        // Generate text for the next 7 days
        for (i in 0 until 7) {
            val day = Calendar.getInstance()
            day.time = currentDate
            day.add(Calendar.DATE, i)

            val formattedDate = SimpleDateFormat("EEE\ndd MMM", Locale.getDefault()).format(day.time)

            when (i) {
                0 -> dayTextSet(_btnd1, formattedDate, size1, size2)
                1 -> dayTextSet(_btnd2, formattedDate, size1, size2)
                2 -> dayTextSet(_btnd3, formattedDate, size1, size2)
                3 -> dayTextSet(_btnd4, formattedDate, size1, size2)
                4 -> dayTextSet(_btnd5, formattedDate, size1, size2)
                5 -> dayTextSet(_btnd6, formattedDate, size1, size2)
                6 -> dayTextSet(_btnd7, formattedDate, size1, size2)
            }
        }

        val buttons = arrayOf(_btnd1, _btnd2, _btnd3, _btnd4, _btnd5, _btnd6, _btnd7)
        ReadData(dayDate)
        for (i in buttons.indices) {
            buttons[i].setOnClickListener {
                // Reset the background color of the last clicked button to white
                lastClickedButton?.setBackgroundColor(Color.WHITE)

                // Set the background color of the current clicked button to purple
                buttons[i].setBackgroundColor(Color.parseColor("#6750A4"))

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
        val adapterP = adapterGymClass(arClass)
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
                val selectedDate = (document["waktu"] as? Timestamp)?.toDate()?.time ?: 0
                Log.d("sDate",selectedDate.toString())
                if(cekDate(selectedDate, date)) {
                    val id = document.id
                    val nama = document.getString("nama") ?:""
                    val kapasitas = document.getLong("kapasitas")?.toInt() ?: 0

                    val pelatih = document.getString("pelatih" )?:""
                    val durasi = document.getLong("kapasitas")?.toInt() ?: 0
                    val level = document.getString("level") ?:""


                    val timeFormat = SimpleDateFormat("HH:mm ", Locale("id", "ID"))
                    timeFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
                    val waktu = timeFormat.format(Time(selectedDate))
                    Log.d("wkatu", waktu)
//                    val waktu = selectedDate.toDate()?.time?.div(1000) ?: 0
//                    val kuotaMax = document.getLong("kuotaMax")?.toInt() ?: 0
//                    val kuotaSisa = document.getLong("kuotaSisa")?.toInt() ?: 0
//                    val sesi = document.getString("sesi") ?: ""

                    val userId =
                        (document["userId"] as? List<*>)?.map { it.toString() } ?: emptyList()


                    val dateFormat = SimpleDateFormat("dd MMM", Locale.ENGLISH)
                    dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
                    val formattedDate = dateFormat.format(Date(selectedDate))

                    arClass.add(GymClass(id,nama,kapasitas,durasi,pelatih,waktu,level))
                    Log.d("haes", "Document data: ${document.data}")
//                    Log.d("haes", formattedDate)
                }
            }
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
    fun CreateData(data : GymClass){

        val bookedClass = hashMapOf(
            "tanggal" to data.waktu, "user" to "ani"
        )

        db.collection("ClassBooking")
            .add(bookedClass)
            .addOnSuccessListener { documentReference ->
                Log.d(
                    "Booking Class",
                    "berhasil tambahkan user ke database GymBooking"
                )

                val documentId = data.idClass
                val kapasitas = data.capacity - 1


                val updateData = mapOf(
                    "kapasitas" to kapasitas
                )
                Log.d(
                    "Booking Class",
                    "berhasil"
                )
//                                            Toast.makeText(context, "Booking Berhasil", "Booking Gym anda pada tanggal ${data.waktu} " +
//                                                    " jam telah berhasil, Salam sehat! ")
//                                            holder._btnBookGym.isEnabled = false
                db.collection("Class").document(documentId)
                    .update(updateData)
                    .addOnSuccessListener {
                        Log.d(
                            "Booking Class",
                            "berhasil update"
                        )
                    }
                    .addOnFailureListener { e ->
                        Log.d(
                            "Booking Classs",
                            "gagal update"
                        )
                    }
            }
            .addOnFailureListener { e ->
                Log.e("TAG", "Error adding document", e)
            }
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