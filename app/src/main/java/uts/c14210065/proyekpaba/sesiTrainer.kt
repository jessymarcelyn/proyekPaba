package uts.c14210065.proyekpaba

import android.annotation.SuppressLint
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
import android.widget.ImageView
import android.widget.TextView
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

class sesiTrainer : AppCompatActivity() {
    companion object {
        const val login = "GETDATA"
        const val userId = "GETID"
    }

    private var arSesiT = arrayListOf<SesiT>()
    private var arCard = arrayListOf<UserTrainer>()
    private var lastClickedButton: Button? = null
    lateinit var dayDate: Date
    var idLogin: String = ""
    private lateinit var _tvNamaT: TextView
    private lateinit var _tvTanggalMulai: TextView
    private lateinit var _tvDurasi: TextView
    private lateinit var _tvTanggalExpired: TextView
    private lateinit var _tvSesi: TextView
    private lateinit var buttons: Array<Button>
    var namaTrainer: String = ""
    lateinit var idTrainer: String
    lateinit var _rvSesiT: RecyclerView
    lateinit var _rvUserTrainer: RecyclerView
    lateinit var userTrainerId: String
    private var currentVisiblePosition = 0


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sesi_trainer)

        idLogin = intent.getStringExtra(utama.userId).toString()
        Log.d("trainer", idLogin)

//        _tvNamaT = findViewById<TextView>(R.id.tvNamaT)
//        _tvTanggalMulai = findViewById<TextView>(R.id.tvTanggalMulai)
//        _tvDurasi = findViewById<TextView>(R.id.tvDurasi)
//        _tvTanggalExpired = findViewById<TextView>(R.id.tvTanggalExpired)
//        _tvSesi = findViewById<TextView>(R.id.tvSesi)

        TampilkanData1()
        _rvSesiT = findViewById<RecyclerView>(R.id.rvSesiT)
        _rvUserTrainer = findViewById<RecyclerView>(R.id.rvUserTrainer)

        _rvUserTrainer.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val adapterW = adapterUserTrainer(arCard, idLogin)
        _rvUserTrainer.adapter = adapterW

        _rvSesiT.layoutManager = LinearLayoutManager(this)
        val adapterP = adapterSesiT(arSesiT, idLogin)
        _rvSesiT.adapter = adapterP

        adapterP.setOnItemClickCallback(object : adapterSesiT.OnItemClickCallback {
            //            Log.d("toast", "dependee")
            override fun onItemClicked(data: SesiT) {
//                Toast.makeText(this@fClass, data.name, Toast.LENGTH_LONG). show()
            }

            override fun delData(pos: Int) {
                TODO("Not yet implemented")
            }

            override fun bookSesi(data: SesiT) {
                Log.d("iii", "sesiTrainer.userTrainerId : ${data.userTrainerId}")
                db.collection("UserTrainer").document(data.userTrainerId).get()
                    .addOnSuccessListener { documentSnapshot ->
                        var sisaSesi = documentSnapshot.getLong("sisaSesi")?.toInt() ?: 0
                        Log.d("iii", "sisaSesi : $sisaSesi")
                        if (sisaSesi > 0) {
                            // Update GymSesi untuk kuota dan userId
                            val updateData = mapOf(
                                "userTrainerId" to data.userTrainerId
                            )
                            db.collection("JadwalTrainer").document(data.idJadwal)
                                .update(updateData)
                                .addOnSuccessListener {
                                    Log.d(
                                        "BookingSesi",
                                        "berhasil update usertrainerId di jadwaltrainer"
                                    )
                                    val documentRef =
                                        db.collection("UserTrainer")
                                            .document(data.userTrainerId)
                                    documentRef.get()
                                        .addOnSuccessListener { documentSnapshot ->
                                            Log.d("BookingSesi", "berhasil update kuota sesi")

                                            var sisaSesi =
                                                documentSnapshot.getLong("sisaSesi")?.toInt() ?: 0
                                            val updatedSisaSesi = sisaSesi - 1

                                            val updateData = mapOf(
                                                "sisaSesi" to updatedSisaSesi
                                            )
                                            db.collection("UserTrainer")
                                                .document(data.userTrainerId)
                                                .update(updateData)
                                                .addOnSuccessListener {
                                                    Log.d("BookingGym", "berhasil update")
                                                    showAlert(
                                                        this@sesiTrainer,
                                                        "Booking Berhasil",
                                                        "Booking sesi dengan personal trainer anda pada tanggal ${data.tanggal} " +
                                                                " jam ${data.sesi} telah berhasil, Salam sehat! "
                                                    )
                                                    TampilkanData2()
                                                    TampilkanData1()
                                                }
                                                .addOnFailureListener { e ->
                                                    Log.d("BookingGym", "gagal update")
                                                }
                                        }
                                }
                                .addOnFailureListener { e ->
                                    Log.d(
                                        "BookingGym",
                                        "gagal update"
                                    )
                                }
                        } else {
                            showAlert(
                                this@sesiTrainer,
                                "Booking Gagal",
                                "Sesi anda sudah habis, silahkan diperpanjang. "
                            )
                        }

                    }
            }

        })

        val ivNext = findViewById<ImageView>(R.id.iv_next)
        val ivPrevious = findViewById<ImageView>(R.id.iv_previous)
        ivPrevious.visibility = View.GONE

        ivNext.setOnClickListener {
            // Kalau nggak last item
            if (currentVisiblePosition < arCard.size - 1) {
                currentVisiblePosition++

                // Update recycleview dengan posisi terbaru
                _rvUserTrainer.scrollToPosition(currentVisiblePosition)
                adapterW.notifyDataSetChanged()

                // Enable karena tidak sedang di posisi pertama
                ivPrevious.visibility = View.VISIBLE
                ivPrevious.isEnabled = true

                TampilkanData2()
            }

            //Kalau last item
            if (currentVisiblePosition == arCard.size - 1) {
                ivNext.isEnabled = false
                ivNext.visibility = View.GONE
            }
        }

        ivPrevious.setOnClickListener {
            // Kalau nggak first item
            if (currentVisiblePosition > 0) {

                currentVisiblePosition--

                // Update recycleview dengan posisi terbaru
                _rvUserTrainer.scrollToPosition(currentVisiblePosition)
                adapterW.notifyDataSetChanged()

                // Enable karena tidak sedang di posisi terakhir
                ivNext.visibility = View.VISIBLE
                ivNext.isEnabled = true

                TampilkanData2()
            }

            //Kalau first item
            if (currentVisiblePosition == 0) {
                ivPrevious.isEnabled = false
                ivPrevious.visibility = View.GONE
            }
        }


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

        buttons = arrayOf(_btnd1, _btnd2, _btnd3, _btnd4, _btnd5, _btnd6, _btnd7)

        for (i in buttons.indices) {
            val day = Calendar.getInstance()
            day.time = currentDate
            day.add(Calendar.DATE, i)

            val formattedDate = SimpleDateFormat("EEE\ndd MMM", Locale("id", "ID")).format(day.time)

            dayTextSet(buttons[i], formattedDate, fGym.size1, fGym.size2)
            buttons[i].visibility = View.GONE
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
//                TampilkanData2(dayDate)
                TampilkanData2()
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

    // untuk menampilkan informasi yang atas
    private fun fetchNamaTrainer(idTrainer: String) {

    }

    private fun TampilkanData1() {
        db.collection("UserTrainer").get().addOnSuccessListener { result ->
            arCard.clear()
            if (result.isEmpty) {
                // kalau sudah expired hapus id client di Trainer
                Log.d("HHH", "MASUK")
                val db = Firebase.firestore
                val documentReference = db.collection("Trainer").document(idTrainer)

                val elementToRemove = idLogin

                documentReference.update("clientId", FieldValue.arrayRemove(elementToRemove))
                    .addOnSuccessListener {
                        Log.d(
                            "FirestoreArrayRemove",
                            "Element successfully removed from the array!"
                        )

                        arCard.add(
                            UserTrainer(
                                "",
                                0, "-", "-", 0, "-", "-", 0, 0
                            )
                        )
                        _rvUserTrainer.adapter?.notifyDataSetChanged()
                        return@addOnSuccessListener
                    }
            }
            for (document in result) {
                var idUserTrainer = document.id
                var idUser = document.getString("idUser") ?: ""
                var durasi = document.getLong("durasiPaket")?.toInt() ?: 0

                val tanggalMulai = (document["tanggalMulai"] as? Timestamp)?.toDate()?.time ?: 0

                val tanggalBerakhir = addMonthsToTimestamp(tanggalMulai, durasi)
                val currentDate = Calendar.getInstance().time

                idTrainer = document.getString("idTrainer") ?: ""
                Log.d("ccc", "idTrainer : $idTrainer")
                var sisaSesi = document.getLong("sisaSesi")?.toInt() ?: 0

                if (idUser == idLogin && tanggalBerakhir >= currentDate) {
                    Log.d("jjj", "idUserTrainer : $idUserTrainer")
                    userTrainerId = document.id
                    var harga = document.getLong("harga")?.toInt() ?: 0
                    var totalSesi = document.getLong("totalSesi")?.toInt() ?: 0

                    val tanggalMulaiConvert = convertTimestampToDate(tanggalMulai)
                    val tanggalBerakhirConvert = formatDateToString(tanggalBerakhir)


                    Log.d("ccc", "namatrainer2 : $namaTrainer")
                    arCard.add(
                        UserTrainer(
                            idUserTrainer,
                            durasi,
                            idTrainer,
                            userId,
                            sisaSesi,
                            tanggalMulaiConvert,
                            tanggalBerakhirConvert,
                            totalSesi,
                            harga
                        )
                    )
                    Log.d("ccc", arCard.toString())
                    arCard.sortBy { it.tanggalMulai }
                    _rvUserTrainer.adapter?.notifyDataSetChanged()

                    //apabila jumlah sisa sesi sudah habis maka tidak bisa melihat button
                    if (sisaSesi > 0) {
                        for (i in buttons.indices) {
                            buttons[i].visibility = View.VISIBLE
                        }
//                        TampilkanData2(dayDate)
                        buttons[0].setBackgroundColor(Color.parseColor("#C9F24D"))
                        lastClickedButton = buttons[0]
                    }
//                    break

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

    //Menampilkan data yang bawah untuk jadwal trainer.
    private fun TampilkanData2() {
        db.collection("JadwalTrainer").get().addOnSuccessListener { result ->
            arSesiT.clear()
            val currentUserTrainer = arCard[currentVisiblePosition]

            for (document in result) {
                var jadwalTrainerId = document.id
                val trainerId = document.getString("trainerId") ?: ""
                if (trainerId == currentUserTrainer.trainerId) {
                    Log.d("tralala", "masuk")
                    val userTrainerIdd = document.getString("userTrainerId") ?: ""
                    val tanggal = (document["tanggal"] as? Timestamp)?.toDate()

                    if (cekDate(tanggal, dayDate)) {
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
                                arSesiT.add(
                                    SesiT(
                                        jadwalTrainerId,
                                        formattedDate,
                                        trainerId,
                                        sesi,
                                        userTrainerIdd, currentUserTrainer.idUserTrainer
                                    )
                                )
                            } else if (jam == currentHour) {
                                if (menit > currentMinute) {
                                    arSesiT.add(
                                        SesiT(
                                            jadwalTrainerId,
                                            formattedDate,
                                            trainerId,
                                            sesi,
                                            userTrainerIdd, currentUserTrainer.idUserTrainer
                                        )
                                    )
                                }
                            }
                        } else {
                            arSesiT.add(
                                SesiT(
                                    jadwalTrainerId,
                                    formattedDate,
                                    trainerId,
                                    sesi,
                                    userTrainerIdd, currentUserTrainer.idUserTrainer
                                )
                            )
                        }
                    }
                }
            }
            arSesiT.sortBy { it.sesi }
            _rvSesiT.adapter?.notifyDataSetChanged()
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

    fun cekDate(timestamp: Date?, btnDate: Date): Boolean {
        val dateFormat = SimpleDateFormat("dd MMM", Locale("id", "ID"))
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")

        val formattedDate = dateFormat.format(timestamp)
        val date = dateFormat.format(btnDate)

        Log.d("cektanggal", "format" + formattedDate)
        Log.d("cektanggal", "button " + date)

        return formattedDate == date
    }
}