package uts.c14210065.proyekpaba

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
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

class activityOngoing : AppCompatActivity() {
    companion object {
        const val login = "GETDATA"
        const val userId = "GETID"
    }

    private var arOngoingG = arrayListOf<Gym>()
    private var arOngoingT = arrayListOf<SesiT>()
    private var arOngoingC = arrayListOf<GymClass>()
    private var lastClickedButton: Button? = null
    lateinit var dayDate: Date
    var idLogin: String = ""
    private lateinit var buttons: Array<Button>
    private lateinit var buttonsCategory: Array<Button>
    private lateinit var idTrainer: String
    lateinit var _rvOngoing: RecyclerView
    lateinit var userTrainerId: String
    lateinit var _tvHari: TextView
    var state: Int = 0
    private var selectedCategoryButton: Button? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ongoing)

        idLogin = intent.getStringExtra(utama.userId).toString()
        Log.d("trainer", idLogin)

        _rvOngoing = findViewById<RecyclerView>(R.id.rvOngoing)
        val _btnOGym = findViewById<Button>(R.id.btnOGym)
        val _btnOClass = findViewById<Button>(R.id.btnOClass)
        val _btnOTrainer = findViewById<Button>(R.id.btnOTrainer)
        _tvHari = findViewById<TextView>(R.id.tvHari)
        _tvHari.visibility = View.GONE

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

        buttonsCategory = arrayOf(_btnOGym, _btnOClass, _btnOTrainer)
        buttons = arrayOf(_btnd1, _btnd2, _btnd3, _btnd4, _btnd5, _btnd6, _btnd7)

        _btnOGym.setOnClickListener {
            state = 1
            changeButtonColor(_btnOGym)
            Log.d("mmm", "state1 : $state")
            setupRecyclerView()
        }

        _btnOClass.setOnClickListener {
            state = 2
            changeButtonColor(_btnOClass)
            Log.d("mmm", "state2 : $state")
            setupRecyclerView()
        }

        _btnOTrainer.setOnClickListener {
            state = 3
            changeButtonColor(_btnOTrainer)
            Log.d("mmm", "state3 : $state")
            setupRecyclerView()
        }

        for (i in buttons.indices) {
            val day = Calendar.getInstance()
            day.time = currentDate
            day.add(Calendar.DATE, i)
            buttons[i].visibility = View.GONE

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
                if (state == 1) {
                    TampilkanDataGym(dayDate)
                } else if (state == 2) {
                    TampilkanDataClass(dayDate)
                } else if (state == 3) {
                    TampilkanDataTrainer(dayDate)
                }
            }
        }
    }

    private fun setupRecyclerView() {
        if (state == 1) {
            Log.d("mmm", "masuk")
            _rvOngoing.layoutManager = LinearLayoutManager(this)
            val adapterP = adapterOngoingG(arOngoingG, idLogin)
            _rvOngoing.adapter = adapterP

            adapterP.setOnItemClickCallback(object : adapterOngoingG.OnItemClickCallback {
                override fun onItemClicked(data: Gym) {

                }

                override fun delData(pos: Int) {
                    AlertDialog.Builder(this@activityOngoing)
                        .setTitle("Pembatalan Sesi")
                        .setMessage("Apakah anda yakin membatalkan sesi gym pada " + arOngoingG[pos].tanggal + " pukul ${arOngoingG[pos].sesi} ?")
                        .setPositiveButton("Ya", DialogInterface.OnClickListener { dialog, which ->
                            val documentId = arOngoingG[pos].idGym
                            val userIdToDelete = idLogin

                            Log.d("www", "docuemntid $documentId")
                            db.collection("GymSesi")
                                .document(documentId)
                                .update("userId", FieldValue.arrayRemove(userIdToDelete))
                                .addOnSuccessListener {
                                    showAlert(
                                        this@activityOngoing,
                                        "Pembatalan berhasil",
                                        "Sesi gym berhasil dibatalkan."
                                    )

                                    // BLM UPDATE JUMLAH KUOTA SESI
                                    val documentId = arOngoingG[pos].idGym
                                    val newKuotaSisa = arOngoingG[pos].kuotaSisa + 1

                                    val updateData = mapOf(
                                        "kuotaSisa" to newKuotaSisa,
                                    )

                                    db.collection("GymSesi").document(documentId)
                                        .update(updateData)
                                        .addOnSuccessListener {
                                            Log.d(
                                                "BookingGym",
                                                "berhasil update"
                                            )
                                            TampilkanDataGym(dayDate)
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


                                }
                                .addOnFailureListener {
                                    showAlert(
                                        this@activityOngoing,
                                        "Pembatalan Gagal",
                                        "Sesi gym gagal dibatalkan."
                                    )
                                }
                        })
                        .setNegativeButton(
                            "Tidak", DialogInterface.OnClickListener { dialog, which ->
//                                Toast.makeText(this@activityOngoing, "DATA BATAL DIHAPUS", Toast.LENGTH_SHORT)
//                                    .show()
                            })
                        .show()
                }
            })
        } else if (state == 2) {
            Log.d("mmm", "masuk")
            _rvOngoing.layoutManager = LinearLayoutManager(this)
            val adapterP = adapterOngoingC(arOngoingC, idLogin)
            _rvOngoing.adapter = adapterP

            adapterP.setOnItemClickCallback(object : adapterOngoingC.OnItemClickCallback {
                override fun onItemClicked(data: Gym) {

                }

                override fun delData(pos: Int) {
                    val timestamp = arOngoingC[pos].timestamp
                    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
                    dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
                    val formattedDate = timestamp?.toDate()?.let { dateFormat.format(it) } ?: ""
                    val calendar = Calendar.getInstance()
                    calendar.time = timestamp?.toDate()

                    val hour = calendar.get(Calendar.HOUR_OF_DAY)
                    val minute = calendar.get(Calendar.MINUTE)
                    val formattedTime = String.format("%02d:%02d", hour, minute)

                    AlertDialog.Builder(this@activityOngoing)
                        .setTitle("Pembatalan Sesi Class")
                        .setMessage("Apakah anda yakin membatalkan sesi class pada $formattedDate pukul $formattedTime?")
                        .setPositiveButton("Ya", DialogInterface.OnClickListener { dialog, which ->
                            val documentId = arOngoingC[pos].idClass
                            val userIdToDelete = idLogin

                            Log.d("www", "docuemntid $documentId")
                            db.collection("Class")
                                .document(documentId)
                                .update("userId", FieldValue.arrayRemove(userIdToDelete))
                                .addOnSuccessListener {
                                    showAlert(
                                        this@activityOngoing,
                                        "Pembatalan berhasil",
                                        "Sesi class berhasil dibatalkan."
                                    )


                                    val documentId = arOngoingC[pos].idClass
                                    val newKuotaSisa = arOngoingC[pos].capacity + 1

                                    val updateData = mapOf(
                                        "kapasitas" to newKuotaSisa,
                                    )

                                    db.collection("Class").document(documentId)
                                        .update(updateData)
                                        .addOnSuccessListener {
                                            Log.d(
                                                "Cancelclass",
                                                "berhasil update"
                                            )
                                            TampilkanDataClass(dayDate)
                                        }
                                        .addOnFailureListener { e ->
                                            Log.d(
                                                "Cancelclass",
                                                "gagal update"
                                            )
                                        }

                                        .addOnFailureListener { e ->
                                            Log.e("TAG", "Error adding document", e)
                                        }


                                }
                                .addOnFailureListener {
                                    showAlert(
                                        this@activityOngoing,
                                        "Pembatalan Gagal",
                                        "Sesi gym gagal dibatalkan."
                                    )
                                }
                        })
                        .setNegativeButton(
                            "Tidak", DialogInterface.OnClickListener { dialog, which ->
//                                Toast.makeText(this@activityOngoing, "DATA BATAL DIHAPUS", Toast.LENGTH_SHORT)
//                                    .show()
                            })
                        .show()
                }
            })
        } else if (state == 3) {
            Log.d("mmm", "masuk")
            _rvOngoing.layoutManager = LinearLayoutManager(this)
            val adapterP = adapterOngoingT(arOngoingT, idLogin)
            _rvOngoing.adapter = adapterP

            adapterP.setOnItemClickCallback(object : adapterOngoingT.OnItemClickCallback {
                override fun onItemClicked(data: Gym) {
                }

                override fun delData(pos: Int) {
                    AlertDialog.Builder(this@activityOngoing)
                        .setTitle("Pembatalan Sesi")
                        .setMessage("Apakah anda yakin membatalkan sesi personal trainer pada " + arOngoingT[pos].tanggal + " pukul ${arOngoingT[pos].sesi} ?")
                        .setPositiveButton("Ya", DialogInterface.OnClickListener { dialog, which ->
                            val documentId = arOngoingT[pos].idJadwal

                            db.collection("JadwalTrainer")
                                .document(documentId)
                                .update("userTrainerId", "")
                                .addOnSuccessListener {
                                    showAlert(
                                        this@activityOngoing,
                                        "Pembatalan berhasil",
                                        "Sesi personal trainer berhasil dibatalkan."
                                    )

                                    val documentId = arOngoingT[pos].userTrainerId
//                                    val newKuotaSisa = arOngoingT[pos].kuotaSisa + 1

                                    db.collection("UserTrainer").get()
                                        .addOnSuccessListener { result ->
                                            for (document in result) {
                                                if (document.id == arOngoingT[pos].userTrainerId) {
                                                    val sisaSesi =
                                                        document.getLong("sisaSesi")?.toInt() ?: 0

                                                    val newsisaSesi = sisaSesi + 1

                                                    val updateData = mapOf(
                                                        "sisaSesi" to newsisaSesi,
                                                    )
                                                    db.collection("UserTrainer")
                                                        .document(documentId)
                                                        .update(updateData)
                                                        .addOnSuccessListener {
                                                            Log.d(
                                                                "OngoingT",
                                                                "berhasil update"
                                                            )
                                                            TampilkanDataTrainer(dayDate)
                                                        }
                                                        .addOnFailureListener { e ->
                                                            Log.d(
                                                                "OngoingT",
                                                                "gagal update"
                                                            )
                                                        }
                                                }
                                            }
                                        }

                                }
                                .addOnFailureListener {
                                    showAlert(
                                        this@activityOngoing,
                                        "Pembatalan Gagal",
                                        "Sesi personal trainer gagal dibatalkan."
                                    )
                                }
                        })
                        .setNegativeButton(
                            "Tidak", DialogInterface.OnClickListener { dialog, which ->
//                                Toast.makeText(this@activityOngoing, "DATA BATAL DIHAPUS", Toast.LENGTH_SHORT)
//                                    .show()
                            })
                        .show()
                }
            })
        }
    }

    private fun changeButtonColor(clickedButton: Button) {
        selectedCategoryButton?.setBackgroundColor(Color.WHITE)
        clickedButton.setBackgroundColor(Color.parseColor("#C9F24D"))
        selectedCategoryButton = clickedButton

        changeButtonVisible()
    }


    private fun changeButtonVisible() {
        for (button in buttons) {
            button.visibility = View.GONE
        }
        // Apabila button gym, trainer dan class ditekan maka baru muncul
        when (state) {
            1, 2, 3 -> {
                _tvHari.visibility = View.VISIBLE
                for (i in buttons.indices) {
                    buttons[i].visibility = View.VISIBLE
                }

            }
        }
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

    //menampilkan data gym
    private fun TampilkanDataGym(btnDate: Date) {
        db.collection("GymSesi").get().addOnSuccessListener { result ->
            arOngoingG.clear()
            for (document in result) {
                var Gymid = document.id
                val tanggal = (document["tanggal"] as? Timestamp)?.toDate()
                val kuotaSisa = document.getLong("kuotaSisa")?.toInt() ?: 0

                if (cekDate(tanggal, btnDate)) {
                    val userId =
                        (document["userId"] as? List<*>)?.map { it.toString() } ?: emptyList()

                    if (idLogin in userId) {
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

                        //jam sekarang
                        val currentTime = Calendar.getInstance()
                        val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
                        val currentMinute = currentTime.get(Calendar.MINUTE)

                        //tanggal diubah dengan format
                        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
                        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
                        val formattedDate = dateFormat.format(tanggal)
                        val currentDate = dateFormat.format(Date())

                        //jam ditambah 2 jam
                        calendar.add(Calendar.HOUR_OF_DAY, 2)
                        val jamm = calendar.get(Calendar.HOUR_OF_DAY)

                        // kalau jam hari ini sudah lewat berarti tidak masuk ongoing
                        if (formattedDate == currentDate) {
                            Log.d("ppp", "masuk1")
                            if (jamm > currentHour) {
                                Log.d("ppp", "masuk2")
                                arOngoingG.add(
                                    Gym(
                                        Gymid,
                                        formattedDate,
                                        sesi,
                                        kuotaMax,
                                        kuotaSisa,
                                        ArrayList(userId)
                                    )
                                )

                            }
                            if (jamm == currentHour) {
                                if (menit > currentMinute) {
                                    Log.d("ppp", "masuk3")
                                    arOngoingG.add(
                                        Gym(
                                            Gymid,
                                            formattedDate,
                                            sesi,
                                            kuotaMax,
                                            kuotaSisa,
                                            ArrayList(userId)
                                        )
                                    )
                                    Log.d("haes", "Document data: ${document.data}")
                                    Log.d("haes", formattedDate)
                                }
                            }
                        } else {
                            arOngoingG.add(
                                Gym(
                                    Gymid,
                                    formattedDate,
                                    sesi,
                                    kuotaMax,
                                    kuotaSisa,
                                    ArrayList(userId)
                                )
                            )
                        }
                    }
                }
            }
            Log.d("haes", "arOngoingG: ${arOngoingG}")
            arOngoingG.sortBy { it.sesi }
            _rvOngoing.adapter?.notifyDataSetChanged()
        }.addOnFailureListener { e ->
            Log.e("haes", "Error fetching data from Firebase", e)
        }
    }

    //menampilkan data trainer
    private fun TampilkanDataTrainer(btnDate: Date) {
        arOngoingT.clear()
        db.collection("UserTrainer").get().addOnSuccessListener { userTrainerResult ->
            for (userTrainerDocument in userTrainerResult) {
                var userTrainerId = userTrainerDocument.id
                Log.d("rrr", "UserTrainerId : $userTrainerId")
                var idUser = userTrainerDocument.getString("idUser") ?: ""
                var durasi = userTrainerDocument.getLong("durasiPaket")?.toInt() ?: 0

                val tanggalMulai =
                    (userTrainerDocument["tanggalMulai"] as? Timestamp)?.toDate()?.time ?: 0

                val tanggalBerakhir = addMonthsToTimestamp(tanggalMulai, durasi)
                val currentDate = Calendar.getInstance().time

                idTrainer = userTrainerDocument.getString("idTrainer") ?: ""

                if (idUser == idLogin && tanggalBerakhir >= currentDate) {
                    Log.d("sesiTrainerr", "masuk")
                    userTrainerId = userTrainerDocument.id
                    fetchJadwalTrainer(userTrainerId, btnDate, idTrainer)
                } else {
                    Log.d("sesiTrainerr", "tidak masuk: idUser=$idUser, idLogin=$idLogin, tanggalBerakhir=$tanggalBerakhir, currentDate=$currentDate")
                }
            }
        }
    }

    private fun fetchJadwalTrainer(userTrainerId: String, btnDate: Date, idTrainer:String) {
        db.collection("JadwalTrainer").whereEqualTo("userTrainerId", userTrainerId)
            .get().addOnSuccessListener { jadwalTrainerResult ->
                for (jadwalTrainerDocument in jadwalTrainerResult) {
                    var jadwalTrainerId = jadwalTrainerDocument.id

                    val trainerId = jadwalTrainerDocument.getString("trainerId") ?: ""
                    val userTrainerIdd = jadwalTrainerDocument.getString("userTrainerId") ?: ""

                    if (trainerId == idTrainer && userTrainerId == userTrainerIdd) {
                        Log.d("bbb", "masuk")

                        val tanggal = (jadwalTrainerDocument["tanggal"] as? Timestamp)?.toDate()

                        if (cekDate(tanggal, btnDate)) {
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
                            val currentDate = dateFormat.format(Date())

                            //jam sekarang
                            val currentTime = Calendar.getInstance()
                            val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
                            val currentMinute = currentTime.get(Calendar.MINUTE)

                            if (formattedDate == currentDate) {
                                if (jam > currentHour) {
                                    Log.d("rrr", "jadwalTrainerId : $jadwalTrainerId")
                                    arOngoingT.add(
                                        SesiT(
                                            jadwalTrainerId,
                                            formattedDate,
                                            trainerId,
                                            sesi,
                                            userTrainerIdd, userTrainerId
                                        )
                                    )
                                } else if (jam == currentHour) {
                                    if (menit > currentMinute) {
                                        Log.d("rrr", "jadwalTrainerId : $jadwalTrainerId")
                                        arOngoingT.add(
                                            SesiT(
                                                jadwalTrainerId,
                                                formattedDate,
                                                trainerId,
                                                sesi,
                                                userTrainerIdd, userTrainerId
                                            )
                                        )
                                    }
                                }
                            } else {
                                Log.d("rrr", "jadwalTrainerId : $jadwalTrainerId")
                                arOngoingT.add(
                                    SesiT(
                                        jadwalTrainerId,
                                        formattedDate,
                                        trainerId,
                                        sesi,
                                        userTrainerIdd, userTrainerId
                                    )
                                )
                            }
                        } else {
                            Log.d("bbb", "tidak masuk cekDate: tanggal=$tanggal, btnDate=$btnDate")
                        }
                    }
                }
                arOngoingT.sortBy { it.sesi }
                _rvOngoing.adapter?.notifyDataSetChanged()
            }.addOnFailureListener { e ->
                Log.e("haes", "Error fetching data from Firebase", e)
            }
    }



    //menampilkan data trainer
//    private fun TampilkanDataTrainer(btnDate: Date) {
//        arOngoingT.clear()
//        db.collection("UserTrainer").get().addOnSuccessListener { result ->
//            for (document in result) {
//                var tes = document.id
//                Log.d("rrr", "UserTrainerId : $tes")
//                var idUser = document.getString("idUser") ?: ""
//                var durasi = document.getLong("durasiPaket")?.toInt() ?: 0
//
//                val tanggalMulai = (document["tanggalMulai"] as? Timestamp)?.toDate()?.time ?: 0
//
//                val tanggalBerakhir = addMonthsToTimestamp(tanggalMulai, durasi)
//                val currentDate = Calendar.getInstance().time
//
//                idTrainer = document.getString("idTrainer") ?: ""
//
//                if (idUser == idLogin && tanggalBerakhir >= currentDate) {
//                    Log.d("sesiTrainerr", "masuk")
//                    userTrainerId = document.id
//                    db.collection("JadwalTrainer").get().addOnSuccessListener { result ->
//                        arOngoingT.clear()
//                        for (document in result) {
//                            var jadwalTrainerId = document.id
//                            Log.d("rrr", "jadwalTrainerId : $jadwalTrainerId")
//                            val trainerId = document.getString("trainerId") ?: ""
//                            val userTrainerIdd = document.getString("userTrainerId") ?: ""
//
//                            Log.d("bbb", "userTrainerId : $userTrainerId")
//                            Log.d("bbb", "userTrainerIdd : $userTrainerIdd")
//                            if (trainerId == idTrainer && userTrainerId == userTrainerIdd) {
//                                Log.d("bbb", "masuk")
//
//                                val tanggal = (document["tanggal"] as? Timestamp)?.toDate()
//
//                                if (cekDate(tanggal, btnDate)) {
//                                    val calendar = Calendar.getInstance()
//                                    calendar.time = tanggal
//
//                                    // ambil jam dan menit dari timestamp tanggal di database
//                                    val jam = calendar.get(Calendar.HOUR_OF_DAY)
//                                    val menit = calendar.get(Calendar.MINUTE)
//
//                                    //agar jadi 08.00 atau 17.00
//                                    val formatJam = String.format("%02d", jam)
//                                    val formatMenit = String.format("%02d", menit)
//
//                                    val sesi = "$formatJam:$formatMenit"
//
//                                    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
//                                    dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
//                                    val formattedDate = dateFormat.format(tanggal)
//                                    val currentDate = dateFormat.format(Date())
//
//                                    //jam sekarang
//                                    val currentTime = Calendar.getInstance()
//                                    val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
//                                    val currentMinute = currentTime.get(Calendar.MINUTE)
//
//                                    if (formattedDate == currentDate) {
//                                        if (jam > currentHour) {
//                                            arOngoingT.add(
//                                                SesiT(
//                                                    jadwalTrainerId,
//                                                    formattedDate,
//                                                    trainerId,
//                                                    sesi,
//                                                    userTrainerIdd, userTrainerId
//                                                )
//                                            )
//                                        } else if (jam == currentHour) {
//                                            if (menit > currentMinute) {
//                                                arOngoingT.add(
//                                                    SesiT(
//                                                        jadwalTrainerId,
//                                                        formattedDate,
//                                                        trainerId,
//                                                        sesi,
//                                                        userTrainerIdd, userTrainerId
//                                                    )
//                                                )
//                                            }
//                                        }
//                                    } else {
//                                        Log.d("bbb", "masuk3")
//                                        arOngoingT.add(
//                                            SesiT(
//                                                jadwalTrainerId,
//                                                formattedDate,
//                                                trainerId,
//                                                sesi,
//                                                userTrainerIdd, userTrainerId
//                                            )
//                                        )
//                                    }
//                                }
//                            }
//                        }
//                        arOngoingT.sortBy { it.sesi }
//                        _rvOngoing.adapter?.notifyDataSetChanged()
//                    }.addOnFailureListener { e ->
//                        Log.e("haes", "Error fetching data from Firebase", e)
//                    }
////                    break
//                }
//            }
//        }
//    }

    //menampilkan data class
    private fun TampilkanDataClass(date: Date) {
        db.collection("Class").get().addOnSuccessListener { result ->
            arOngoingC.clear()
            for (document in result) {

                val userId =
                    (document["userId"] as? List<*>)?.map { it.toString() } ?: emptyList()
                if (idLogin in userId) {
                    val selectedDate = (document["tanggal"] as? Timestamp)?.toDate()?.time ?: 0
                    Log.d("sDate", selectedDate.toString())
                    if (cekDateLong(selectedDate, date)) {
                        val id = document.id
                        val nama = document.getString("nama") ?: ""
                        val kapasitas = document.getLong("kapasitas")?.toInt() ?: 0

                        val pelatih = document.getString("pelatih") ?: ""
                        val durasi = document.getLong("kapasitas")?.toInt() ?: 0
                        val level = document.getString("level") ?: ""
                        val arrUser = document.get("userId") as? List<String> ?: emptyList()

                        val timestamp = document.getTimestamp("tanggal")

                        val timestampJes = (document["tanggal"] as? Timestamp)?.toDate()

                        val calendar = Calendar.getInstance()
                        calendar.time = timestampJes

                        // ambil jam dan menit dari timestamp tanggal di database
                        val jam = calendar.get(Calendar.HOUR_OF_DAY)
                        val menit = calendar.get(Calendar.MINUTE)

                        //jam sekarang
                        val currentTime = Calendar.getInstance()
                        val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
                        val currentMinute = currentTime.get(Calendar.MINUTE)

                        //tanggal diubah dengan format
                        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
                        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
                        val formattedDate = dateFormat.format(timestampJes)
                        val currentDate = dateFormat.format(Date())

                        // kalau jam hari ini sudah lewat berarti tidak masuk ongoing
                        if (formattedDate == currentDate) {
                            Log.d("ppp", "masuk1")
                            if (jam > currentHour) {
                                Log.d("ppp", "masuk2")
                                arOngoingC.add(
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
                            }
                            if (jam == currentHour) {
                                if (menit > currentMinute) {
                                    Log.d("ppp", "masuk3")
                                    arOngoingC.add(
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
                                    Log.d("haes", "Document data: ${document.data}")
                                    Log.d("haes", formattedDate)
                                }
                            }
                        } else {
                            arOngoingC.add(
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
                        }
                    }

                    Log.d("haes", "Document data: ${document.data}")
//                    Log.d("haes", formattedDate)
                }
            }
            arOngoingC.sortBy { it.timestamp }
            _rvOngoing.adapter?.notifyDataSetChanged()
        }.addOnFailureListener { e ->
            Log.e("haes", "Error fetching data from Firebase", e)
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