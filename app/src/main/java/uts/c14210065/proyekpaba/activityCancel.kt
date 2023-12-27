package uts.c14210065.proyekpaba

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class activityCancel : AppCompatActivity() {
    private var arCancelG = arrayListOf<Gym>()
    private var arCancelT = arrayListOf<SesiT>()
    private var arCancelC = arrayListOf<GymClass>()
    private var lastClickedButton: Button? = null
    lateinit var dayDate: Date
    lateinit var currentDate: Date
    var idLogin: String = ""

    //    private lateinit var buttons: Array<Button>
    private lateinit var buttonsCategory: Array<Button>
    private lateinit var idTrainer: String
    lateinit var _rvOngoing: RecyclerView
    lateinit var userTrainerId: String
    var state: Int = 0
    private var selectedCategoryButton: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cancel)

        idLogin = intent.getStringExtra(utama.userId).toString()
        Log.d("trainer", idLogin)

        _rvOngoing = findViewById<RecyclerView>(R.id.rvOngoing)
        val _btnOGym = findViewById<Button>(R.id.btnOGym)
        val _btnOClass = findViewById<Button>(R.id.btnOClass)
        val _btnOTrainer = findViewById<Button>(R.id.btnOTrainer)
//        _tvHari = findViewById<TextView>(R.id.tvHari)
//        _tvHari.visibility = View.GONE
//
//        val _btnd1 = findViewById<Button>(R.id.btnd1)
//        val _btnd2 = findViewById<Button>(R.id.btnd2)
//        val _btnd3 = findViewById<Button>(R.id.btnd3)
//        val _btnd4 = findViewById<Button>(R.id.btnd4)
//        val _btnd5 = findViewById<Button>(R.id.btnd5)
//        val _btnd6 = findViewById<Button>(R.id.btnd6)
//        val _btnd7 = findViewById<Button>(R.id.btnd7)

        // Get the current date
        currentDate = Calendar.getInstance().time
//        dayDate = currentDate

        buttonsCategory = arrayOf(_btnOGym, _btnOClass, _btnOTrainer)
//        buttons = arrayOf(_btnd1, _btnd2, _btnd3, _btnd4, _btnd5, _btnd6, _btnd7)

        _btnOGym.setOnClickListener {
            state = 1
            changeButtonColor(_btnOGym)

            Log.d("mmm", "state1 : $state")
            setupRecyclerView()
            TampilkanDataGym()
        }

        _btnOClass.setOnClickListener {
            state = 2
            changeButtonColor(_btnOClass)

            Log.d("mmm", "state2 : $state")
            setupRecyclerView()
            TampilkanDataClass()
        }

        _btnOTrainer.setOnClickListener {
            state = 3
            changeButtonColor(_btnOTrainer)
            Log.d("mmm", "state3 : $state")
            setupRecyclerView()
            TampilkanDataTrainer()
        }
//        for (i in buttons.indices) {
//            val day = Calendar.getInstance()
//            day.time = currentDate
//            day.add(Calendar.DATE, i)
//            buttons[i].visibility = View.GONE
//
//            val formattedDate = SimpleDateFormat("EEE\ndd MMM", Locale("id", "ID")).format(day.time)
//
//            dayTextSet(buttons[i], formattedDate, fGym.size1, fGym.size2)
//            buttons[i].setOnClickListener {
//                // Reset the background color of the last clicked button to white
//                lastClickedButton?.setBackgroundColor(Color.WHITE)
//
//                // Set the background color of the current clicked button to purple
//                buttons[i].setBackgroundColor(Color.parseColor("#C9F24D"))
//
//                // Update the last clicked button
//                lastClickedButton = buttons[i]
//
//                // tanggal di button
//                val calendar = Calendar.getInstance()
//                calendar.time = currentDate
//                calendar.add(Calendar.DATE, i)
//                dayDate = calendar.time
//                if (state == 1) {
//                    TampilkanDataGym(dayDate)
//                } else if (state == 2) {
//                    TampilkanDataClass1(dayDate)
//                } else if (state == 3) {
//                    TampilkanDataTrainer(dayDate)
//                }
//            }
//        }

    }

    private fun setupRecyclerView() {
        if (state == 1) {
            Log.d("mmm", "masuk")
            _rvOngoing.layoutManager = LinearLayoutManager(this)
            val adapterP = adapterCancelG(arCancelG, idLogin)
            _rvOngoing.adapter = adapterP
        } else if (state == 2) {
            Log.d("mmm", "masuk")
            _rvOngoing.layoutManager = LinearLayoutManager(this)
            val adapterP = adapterCancelC(arCancelC, idLogin)
            _rvOngoing.adapter = adapterP
        } else if (state == 3) {
            Log.d("mmm", "masuk")
            _rvOngoing.layoutManager = LinearLayoutManager(this)
            val adapterP = adapterCancelT(arCancelT, idLogin)
            _rvOngoing.adapter = adapterP

        }
    }

    private fun changeButtonColor(clickedButton: Button) {
        selectedCategoryButton?.setBackgroundColor(Color.WHITE)
        clickedButton.setBackgroundColor(Color.parseColor("#C9F24D"))
        selectedCategoryButton = clickedButton

//        changeButtonVisible()
    }


//    private fun changeButtonVisible() {
//        for (button in buttons) {
//            button.visibility = View.GONE
//        }
//        // Apabila button gym, trainer dan class ditekan maka baru muncul
//        when (state) {
//            1, 2, 3 -> {
//                _tvHari.visibility = View.VISIBLE
//                for (i in buttons.indices) {
//                    buttons[i].visibility = View.VISIBLE
//                }
//
//            }
//        }
//    }

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

    //menampilkan data gym
    private fun TampilkanDataGym() {
        arCancelG.clear()
        db.collection("CancelGym").get().addOnSuccessListener { result ->
            val listIdGym = mutableListOf<String>()
            for (document in result) {
                val userId = document.id
                if (userId == idLogin) {
                    val idGymList =
                        (document["idGym"] as? List<*>)?.map { it.toString() } ?: emptyList()

                    listIdGym.addAll(idGymList)
                }
            }

            Log.d("cvcv", listIdGym.toString())

            for (listid in listIdGym) {
                db.collection("GymSesi").document(listid).get().addOnSuccessListener { document ->
                    if (document.exists()) {
//                        Log.d("cvcv", "listid : $listid")
//                        Log.d("cvcv", "document.id : ${document.id}")
                        val tanggal = (document["tanggal"] as? Timestamp)?.toDate()
                        val kuotaSisa = document.getLong("kuotaSisa")?.toInt() ?: 0

                        val timestamp = document.getTimestamp("tanggal")!!
//                        Log.d("cvcv", "tanggal : $tanggal")
//                        Log.d("cvcv", "btnDate : $btnDate")

                        val userId =
                            (document["userId"] as? List<*>)?.map { it.toString() }
                                ?: emptyList()

                        val kuotaMax = document.getLong("kuotaMax")?.toInt() ?: 0

                        val calendar = Calendar.getInstance()
                        calendar.time = tanggal

                        // ambil jam dan menit dari timestamp tanggal di database
                        val jam = calendar.get(Calendar.HOUR_OF_DAY)
                        val menit = calendar.get(Calendar.MINUTE)

                        //agar jadi 08.00 atau 17.00
                        val formatJam = String.format("%02d", jam)
                        val formatMenit = String.format("%02d", menit)

                        val sesi = "$formatJam:$formatMenit"

                        //tanggal diubah dengan format
                        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
                        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
                        val formattedDate = dateFormat.format(tanggal)

                        arCancelG.add(
                            Gym(
                                listid,
                                formattedDate,
                                sesi,
                                timestamp,
                                kuotaMax,
                                kuotaSisa,
                                ArrayList(userId)
                            )
                        )
                    }

                    arCancelG.sortByDescending { it.timestamp }
                    _rvOngoing.adapter?.notifyDataSetChanged()
                }
            }
        }
    }

    //menampilkan data class
    private fun TampilkanDataClass() {
        arCancelC.clear()
        db.collection("CancelClass").get().addOnSuccessListener { result ->
            val listIdClass = mutableListOf<String>()
            for (document in result) {
                val userId = document.id
                if (userId == idLogin) {
                    val idClassList =
                        (document["idClass"] as? List<*>)?.map { it.toString() } ?: emptyList()

                    listIdClass.addAll(idClassList)
                }
            }

            Log.d("cvcv", listIdClass.toString())

            for (listid in listIdClass) {
                db.collection("Class").document(listid).get().addOnSuccessListener { document ->
                    if (document.exists()) {
                        val id = document.id
                        val nama = document.getString("nama") ?: ""
                        val kapasitas = document.getLong("kapasitas")?.toInt() ?: 0

                        val pelatih = document.getString("pelatih") ?: ""
                        val durasi = document.getLong("kapasitas")?.toInt() ?: 0
                        val level = document.getString("level") ?: ""
                        val arrUser = document.get("userId") as? List<String> ?: emptyList()

                        val timestamp = document.getTimestamp("tanggal")

                        arCancelC.add(
                            GymClass(
                                id,
                                nama,
                                kapasitas,
                                durasi,
                                pelatih,
                                timestamp,
                                level,
                                arrUser
                            )
                        )

                        Log.d("nmnm", "arCancelC : $arCancelC")
                    }
                    arCancelC.sortByDescending { it.timestamp }
                    _rvOngoing.adapter?.notifyDataSetChanged()
                }
            }

        }
    }

    //menampilkan data trainer
    private fun TampilkanDataTrainer() {
        arCancelT.clear()

        db.collection("CancelTrainer").get().addOnSuccessListener { result ->
            val listIdJadwal = mutableListOf<String>()
            for (document in result) {
                val userId = document.id
                if (userId == idLogin) {
                    val idTrainerList =
                        (document["idJadwal"] as? List<*>)?.map { it.toString() } ?: emptyList()

                    listIdJadwal.addAll(idTrainerList)
                }
            }
            for (listid in listIdJadwal) {
                db.collection("JadwalTrainer").get().addOnSuccessListener { userTrainerResult ->
                    for (userTrainerDocument in userTrainerResult) {
                        var jadwalId = userTrainerDocument.id

                        if(jadwalId == listid){
                            val tanggal = (userTrainerDocument["tanggal"] as? Timestamp)?.toDate()
                            val userTrainerIdd = userTrainerDocument.getString("userTrainerId") ?: ""
                            val trainerId = userTrainerDocument.getString("trainerId") ?: ""
                            val timestamp = userTrainerDocument.getTimestamp("tanggal")

                            val calendar = Calendar.getInstance()
                            calendar.time = tanggal

                            // ambil jam dan menit dari timestamp tanggal di database
                            val jam = calendar.get(Calendar.HOUR_OF_DAY)
                            val menit = calendar.get(Calendar.MINUTE)

                            //agar jadi 08.00 atau 17.00
                            val formatJam = String.format("%02d", jam)
                            val formatMenit = String.format("%02d", menit)

                            val sesi = "$formatJam:$formatMenit"

                            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
                            dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
                            val formattedDate = dateFormat.format(tanggal)

                            arCancelT.add(
                                SesiT(
                                    jadwalId,
                                    formattedDate,
                                    trainerId,
                                    sesi,timestamp!!,
                                    "", ""
                                ))
                        }
                    }
                    arCancelT.sortByDescending { it.timestamp }
                    _rvOngoing.adapter?.notifyDataSetChanged()
                }
            }
        }
    }

    fun showAlert(context: Context, title: String, message: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, which ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    // menghitung tanggal berakhir
    fun addMonthsToTimestamp(originalTimestamp: Long, monthsToAdd: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = originalTimestamp
        calendar.add(Calendar.MONTH, monthsToAdd)
        return calendar.time
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

    fun cekDateLong(timestamp: Long, btnDate: Date): Boolean {
        val dateFormat = SimpleDateFormat("dd MMM", Locale("id", "ID"))
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")

        val formattedDate = dateFormat.format(Date(timestamp))
        val date = dateFormat.format(btnDate)

        Log.d("cektanggal", "format" + formattedDate)
        Log.d("cektanggal", "button " + date)

        return formattedDate == date
    }
}
