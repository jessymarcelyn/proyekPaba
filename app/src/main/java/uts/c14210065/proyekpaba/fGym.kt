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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _rvGym = view.findViewById(R.id.rvGym)
// Usage
        val _btnMon = view.findViewById<Button>(R.id.btnMon)
        val _btnTue = view.findViewById<Button>(R.id.btnTue)
        val _btnWed = view.findViewById<Button>(R.id.btnWed)
        val _btnThu = view.findViewById<Button>(R.id.btnThu)
        val _btnFri = view.findViewById<Button>(R.id.btnFri)
        val _btnSat = view.findViewById<Button>(R.id.btnSat)
        val _btnSun = view.findViewById<Button>(R.id.btnSun)
//        dayTextSet(_btnMon, "Mon\n17 Des", size1, size2)
//        dayTextSet(_btnTue, "Tue\n18 Des", size1, size2)
//        dayTextSet(_btnWed, "Wed\n19 Des", size1, size2)
//        dayTextSet(_btnThu, "Thu\n20 Des", size1, size2)
//        dayTextSet(_btnFri, "Fri\n21 Des", size1, size2)
//        dayTextSet(_btnSat, "Sat\n22 Des", size1, size2)
//        dayTextSet(_btnSun, "Sun\n23 Des", size1, size2)

        // Get the current date
        val currentDate = Calendar.getInstance().time

        // Generate text for the next 7 days
        for (i in 0 until 7) {
            val day = Calendar.getInstance()
            day.time = currentDate
            day.add(Calendar.DATE, i)

            val formattedDate = SimpleDateFormat("EEE\ndd MMM", Locale.getDefault()).format(day.time)

            // Use the generated text in your buttons
            when (i) {
                0 -> dayTextSet(_btnMon, formattedDate, size1, size2)
                1 -> dayTextSet(_btnTue, formattedDate, size1, size2)
                2 -> dayTextSet(_btnWed, formattedDate, size1, size2)
                3 -> dayTextSet(_btnThu, formattedDate, size1, size2)
                4 -> dayTextSet(_btnFri, formattedDate, size1, size2)
                5 -> dayTextSet(_btnSat, formattedDate, size1, size2)
                6 -> dayTextSet(_btnSun, formattedDate, size1, size2)
            }
        }

        _btnMon.setOnClickListener{
            // Reset the background color of the last clicked button to white
            lastClickedButton?.setBackgroundColor(Color.WHITE)

            // Set the background color of the current clicked button to purple
            _btnMon.setBackgroundColor(Color.parseColor("#6750A4"))

            // Update the last clicked button
            lastClickedButton = _btnMon
        }

        _btnTue.setOnClickListener{
            // Reset the background color of the last clicked button to white
            lastClickedButton?.setBackgroundColor(Color.WHITE)

            // Set the background color of the current clicked button to purple
            _btnTue.setBackgroundColor(Color.parseColor("#6750A4"))

            // Update the last clicked button
            lastClickedButton = _btnTue
        }

        _btnWed.setOnClickListener{
            // Reset the background color of the last clicked button to white
            lastClickedButton?.setBackgroundColor(Color.WHITE)

            // Set the background color of the current clicked button to purple
            _btnWed.setBackgroundColor(Color.parseColor("#6750A4"))

            // Update the last clicked button
            lastClickedButton = _btnWed
        }

        _btnThu.setOnClickListener{
            // Reset the background color of the last clicked button to white
            lastClickedButton?.setBackgroundColor(Color.WHITE)

            // Set the background color of the current clicked button to purple
            _btnThu.setBackgroundColor(Color.parseColor("#6750A4"))

            // Update the last clicked button
            lastClickedButton = _btnThu
        }

        _btnFri.setOnClickListener{
            // Reset the background color of the last clicked button to white
            lastClickedButton?.setBackgroundColor(Color.WHITE)

            // Set the background color of the current clicked button to purple
            _btnFri.setBackgroundColor(Color.parseColor("#6750A4"))

            // Update the last clicked button
            lastClickedButton = _btnFri
        }

        _btnSat.setOnClickListener{
            // Reset the background color of the last clicked button to white
            lastClickedButton?.setBackgroundColor(Color.WHITE)

            // Set the background color of the current clicked button to purple
            _btnSat.setBackgroundColor(Color.parseColor("#6750A4"))

            // Update the last clicked button
            lastClickedButton = _btnSat
        }

        _btnSun.setOnClickListener{
            // Reset the background color of the last clicked button to white
            lastClickedButton?.setBackgroundColor(Color.WHITE)

            // Set the background color of the current clicked button to purple
            _btnSun.setBackgroundColor(Color.parseColor("#6750A4"))

            // Update the last clicked button
            lastClickedButton = _btnSun
        }

        TampilkanData()
        _rvGym.layoutManager = LinearLayoutManager(context)
        val adapterP = adapterGym(arGym)
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
    lateinit var lvAdapterSimple: SimpleAdapter
    val db = Firebase.firestore

    private fun TampilkanData() {
        db.collection("GymSesi").get().addOnSuccessListener { result ->
            arGym.clear()
            for (document in result) {

                val tanggal = (document["tanggal"] as? Timestamp)?.toDate()?.time ?: 0


                if(isToday(tanggal)) {
                    val kuotaMax = document.getLong("kuotaMax")?.toInt() ?: 0
                    val kuotaSisa = document.getLong("kuotaSisa")?.toInt() ?: 0
                    val sesi = document.getString("sesi") ?: ""

                    val userId =
                        (document["userId"] as? List<*>)?.map { it.toString() } ?: emptyList()


                val dateFormat = SimpleDateFormat("dd MMM", Locale.ENGLISH)
                dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
                val formattedDate = dateFormat.format(Date(tanggal))

                    arGym.add(Gym(formattedDate, sesi, kuotaMax, kuotaSisa, ArrayList(userId)))
                    Log.d("haes", "Document data: ${document.data}")
                    Log.d("haes", formattedDate)
                }
            }
            _rvGym.adapter?.notifyDataSetChanged()
        }.addOnFailureListener { e ->
            Log.e("haes", "Error fetching data from Firebase", e)
        }
    }


    fun isToday(timestamp: Long): Boolean {
        val dateFormat = SimpleDateFormat("dd MMM", Locale("id", "ID"))
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")

        val formattedDate = dateFormat.format(Date(timestamp))
        val currentDate = dateFormat.format(Date())

        return formattedDate == currentDate
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