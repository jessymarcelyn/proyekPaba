package uts.c14210065.proyekpaba

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
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

    private lateinit var _rvGym : RecyclerView
    private var arGym = arrayListOf<Gym>()
    private var lastClickedButton: Button? = null
    lateinit var dayDate : Date

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var idLogin = arguments?.getString("userId")
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

                TampilkanData(dayDate)
            }
        }

        _rvGym.layoutManager = LinearLayoutManager(context)
        val adapterP = adapterGym(arGym, idLogin, requireContext())
        _rvGym.adapter = adapterP
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

    val db = Firebase.firestore

    private fun TampilkanData(btnDate: Date) {
        db.collection("GymSesi").get().addOnSuccessListener { result ->
            arGym.clear()
            for (document in result) {
                var Gymid = document.id
                val tanggal = (document["tanggal"] as? Timestamp)?.toDate()
                val kuotaSisa = document.getLong("kuotaSisa")?.toInt() ?: 0

                if(cekDate(tanggal, btnDate) && kuotaSisa > 0) {
                    val kuotaMax = document.getLong("kuotaMax")?.toInt() ?: 0
//                    val sesi = document.getString("sesi") ?: ""

                    val calendar = Calendar.getInstance()
                    calendar.time = tanggal

                    // ambil jam dan menit dari timestamp tanggal di database
                    val jam = calendar.get(Calendar.HOUR_OF_DAY)
                    val menit = calendar.get(Calendar.MINUTE)

                    //agar jadi 08.00 atau 17.00
                    val formatJam = String.format("%02d", jam)
                    val formatMenit = String.format("%02d", menit)

                    val sesi = "$formatJam:$formatMenit"

                    val userId = (document["userId"] as? List<*>)?.map { it.toString() } ?: emptyList()
                    val member = document.getBoolean("member") ?: false

                    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
                    dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
                    val formattedDate = dateFormat.format(tanggal)
                    Log.d("formatted tanggal : ", formattedDate)

                    arGym.add(Gym(Gymid,formattedDate, sesi, kuotaMax, kuotaSisa, ArrayList(userId), member))
                    Log.d("haes", "Document data: ${document.data}")
                    Log.d("haes", formattedDate)
                }
            }
            arGym.sortBy { it.sesi }
            _rvGym.adapter?.notifyDataSetChanged()
        }.addOnFailureListener { e ->
            Log.e("haes", "Error fetching data from Firebase", e)
        }
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