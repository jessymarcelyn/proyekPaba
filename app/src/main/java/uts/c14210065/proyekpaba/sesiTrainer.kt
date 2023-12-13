package uts.c14210065.proyekpaba

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.Log
import android.widget.Button
import android.widget.TextView
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

class sesiTrainer : AppCompatActivity() {
    companion object {
        const val login = "GETDATA"
        const val userId = "GETID"
    }

    private var arSesiT = arrayListOf<SesiT>()
    private var lastClickedButton: Button? = null
    lateinit var dayDate: Date
    var idLogin: String = ""
    private lateinit var _tvNamaT: TextView
    private lateinit var _tvTanggalMulai: TextView
    private lateinit var _tvDurasi: TextView
    private lateinit var _tvTanggalExpired: TextView
    private lateinit var _tvSisa: TextView
    private lateinit var _tvTotal: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sesi_trainer)

        idLogin = intent.getStringExtra(utama.userId).toString()
        Log.d("trainer", idLogin)

        _tvNamaT = findViewById<TextView>(R.id.tvNamaT)
        _tvTanggalMulai = findViewById<TextView>(R.id.tvTanggalMulai)
        _tvDurasi = findViewById<TextView>(R.id.tvDurasi)
        _tvTanggalExpired = findViewById<TextView>(R.id.tvTanggalExpired)
        _tvSisa = findViewById<TextView>(R.id.tvSisa)
        _tvTotal = findViewById<TextView>(R.id.tvTotal)

        TampilkanData1()
        val _rvSesiT = findViewById<RecyclerView>(R.id.rvSesiT)

        val _btnd1 = findViewById<Button>(R.id.btnd1)
        val _btnd2 = findViewById<Button>(R.id.btnd2)
        val _btnd3 = findViewById<Button>(R.id.btnd3)
        val _btnd4 = findViewById<Button>(R.id.btnd4)
        val _btnd5 = findViewById<Button>(R.id.btnd5)
        val _btnd6 = findViewById<Button>(R.id.btnd6)
        val _btnd7 = findViewById<Button>(R.id.btnd7)

        // Get the current date
        var currentDate = Calendar.getInstance().time
        dayDate = currentDate

        // Generate text for the next 7 days
        for (i in 0 until 7) {
            val day = Calendar.getInstance()
            day.time = currentDate
            day.add(Calendar.DATE, i)

            val formattedDate = SimpleDateFormat("EEE\ndd MMM", Locale("id", "ID")).format(day.time)

            when (i) {
                0 -> dayTextSet(_btnd1, formattedDate, fGym.size1, fGym.size2)
                1 -> dayTextSet(_btnd2, formattedDate, fGym.size1, fGym.size2)
                2 -> dayTextSet(_btnd3, formattedDate, fGym.size1, fGym.size2)
                3 -> dayTextSet(_btnd4, formattedDate, fGym.size1, fGym.size2)
                4 -> dayTextSet(_btnd5, formattedDate, fGym.size1, fGym.size2)
                5 -> dayTextSet(_btnd6, formattedDate, fGym.size1, fGym.size2)
                6 -> dayTextSet(_btnd7, formattedDate, fGym.size1, fGym.size2)
            }
        }

        val buttons = arrayOf(_btnd1, _btnd2, _btnd3, _btnd4, _btnd5, _btnd6, _btnd7)

        for (i in buttons.indices) {
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

//                TampilkanData(dayDate)
            }
        }


//        _rvGym.layoutManager = LinearLayoutManager(this)
//        val adapterP = adapterGym(arSesiT, idLogin, requireContext())
//        _rvGym.adapter = adapterP
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

    // untuk menampilkan informasi yang atas
    private fun TampilkanData1() {
        db.collection("UserTrainer").get().addOnSuccessListener { result ->
            for (document in result) {
                var idUser = document.getString("idUser") ?: ""
                var durasi = document.getLong("durasiPaket")?.toInt() ?: 0

                val tanggalMulai = (document["tanggalMulai"] as? Timestamp)?.toDate()?.time ?: 0

                val tanggalBerakhir = addMonthsToTimestamp(tanggalMulai, durasi)
                val currentDate = Calendar.getInstance().time

                if (idUser == idLogin && tanggalBerakhir > currentDate) {
                    Log.d("sesiTrainerr", "masuk")
                    var userTrainerId = document.id
                    var idTrainer = document.getString("idTrainer") ?: ""
                    var harga = document.getLong("harga")?.toInt() ?: 0
                    var sisaSesi = document.getLong("sisaSesi")?.toInt() ?: 0
                    var totalSesi = document.getLong("totalSesi")?.toInt() ?: 0

                    val tanggalMulaiConvert = convertTimestampToDate(tanggalMulai)
                    val tanggalBerakhirConvert = formatDateToString(tanggalBerakhir)

                    db.collection("Trainer").get().addOnSuccessListener { result ->
                        for (document in result) {

                            if (document.id == idTrainer) {
                                var namaTrainer = document.getString("nama") ?: ""
                                _tvNamaT.text = namaTrainer.toString()
                            }
                        }
                    }

                    _tvSisa.text = sisaSesi.toString() + " / "
                    _tvTotal.text = totalSesi.toString() + " Sesi"
                    _tvDurasi.text = durasi.toString() + " Bulan"
                    _tvTanggalExpired.text = tanggalBerakhirConvert.toString()
                    _tvTanggalMulai.text = tanggalMulaiConvert.toString()
                }
            }
        }.addOnFailureListener { e ->
            Log.e("haes", "Error fetching data from Firebase", e)
        }

    }

    //date ke string
    fun formatDateToString(date: Date): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        return dateFormat.format(date)
    }

    //timestamp ke string
    fun convertTimestampToDate(timestamp: Long): String {
        val date = Date(timestamp)
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        return dateFormat.format(date)
    }

    // menghitung tanggal berakhir
    fun addMonthsToTimestamp(originalTimestamp: Long, monthsToAdd: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = originalTimestamp
        calendar.add(Calendar.MONTH, monthsToAdd)
        return calendar.time
    }
//    private fun TampilkanData(btnDate: Date) {
//        db.collection("GymSesi").get().addOnSuccessListener { result ->
//            arSesiT.clear()
//            for (document in result) {
//                var Gymid = document.id
//                val tanggal = (document["tanggal"] as? Timestamp)?.toDate()?.time ?: 0
//                val kuotaSisa = document.getLong("kuotaSisa")?.toInt() ?: 0
//
//                if(cekDate(tanggal, btnDate) && kuotaSisa > 0) {
//                    val kuotaMax = document.getLong("kuotaMax")?.toInt() ?: 0
//                    val sesi = document.getString("sesi") ?: ""
//                    val userId = (document["userId"] as? List<*>)?.map { it.toString() } ?: emptyList()
//                    val member = document.getBoolean("member") ?: false
//
//                    val dateFormat = SimpleDateFormat("dd MMM", Locale.ENGLISH)
//                    dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
//                    val formattedDate = dateFormat.format(Date(tanggal))
//
//                    arSesiT.add(Gym(Gymid,formattedDate, sesi, kuotaMax, kuotaSisa, ArrayList(userId), member))
//                    Log.d("haes", "Document data: ${document.data}")
//                    Log.d("haes", formattedDate)
//                }
//            }
//            arSesiT.sortBy { it.sesi }
//            _rvGym.adapter?.notifyDataSetChanged()
//        }.addOnFailureListener { e ->
//            Log.e("haes", "Error fetching data from Firebase", e)
//        }
//    }

    fun cekDate(timestamp: Long, btnDate: Date): Boolean {
        val dateFormat = SimpleDateFormat("dd MMM", Locale("id", "ID"))
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")

        val formattedDate = dateFormat.format(Date(timestamp))
        val date = dateFormat.format(btnDate)

        Log.d("cektanggal", "format" + formattedDate)
        Log.d("cektanggal", "button " + date)

        return formattedDate == date
    }
}