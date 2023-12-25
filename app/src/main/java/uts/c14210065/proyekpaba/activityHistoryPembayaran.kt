package uts.c14210065.proyekpaba

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.tan

class activityHistoryPembayaran : AppCompatActivity() {
    lateinit var _rvHistoryP: RecyclerView
    lateinit var idLogin: String
    var tanggalBerakhirTrainer: Date = Date()
    var tanggalBerakhirGym: Date = Date()
    var tanggalBerakhirGymConvert: String = ""
    var tanggalBerakhirTrainerConvert: String = ""
    lateinit var tanggalMulai: String
    private var arHistoryP = arrayListOf<Transaksi>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_pembayaran)

        idLogin = intent.getStringExtra(utama.userId).toString()
        _rvHistoryP = findViewById(R.id.rvHistoryP)

        TampilkanData()

        _rvHistoryP.layoutManager = LinearLayoutManager(this)
        val adapterP = adapterHistoryPembayaran(arHistoryP)
        _rvHistoryP.adapter = adapterP

        adapterP.setOnItemClickCallback(object: adapterHistoryPembayaran.OnItemClickCallback{
            override fun onItemClicked(data: Transaksi) {
                val intent = Intent(this@activityHistoryPembayaran, detPembayaran::class.java)
                intent.putExtra("kirimData", data)
                startActivity(intent)
            }

            override fun delData(pos: Int) {
            }
        })
    }

    private fun TampilkanData() {
        val db = Firebase.firestore
        db.collection("Transaksi").orderBy("tanggalBeli", Query.Direction.ASCENDING).get().addOnSuccessListener { result ->
            arHistoryP.clear()
            for (document in result) {
                var idTransaksi = document.id
                val idUser = document.getString("idUser") ?: ""

                if(idUser == idLogin) {
                    val durasi = document.getLong("durasi")?.toInt() ?: 0
                    val harga = document.getLong("harga")?.toInt() ?: 0
                    val idPaket = document.getString("idPaket") ?: ""
                    val idTrainer = document.getString("idTrainer") ?: ""

                    val jenisPembayaran = document.getString("jenisPembayaran") ?: ""
                    val jenisMember = document.getString("jenisMember") ?: ""
                    val pilihan = document.getString("pilihan") ?: ""
                    val tanggalBeli = (document["tanggalBeli"] as? Timestamp)?.toDate()?.time ?: 0
                    val totalSesi = document.getLong("totalSesi")?.toInt() ?: 0

                    val currentDate = Date()
                    val tanggalBeliConvert = convertTimestampToString(tanggalBeli)

                    Log.d("mmmm", "tanggalBeli : ${tanggalBeli}")
                    //Member Gym
                    if(jenisMember != "") {
                        Log.d("mmmm", "currentdate : $currentDate")
                        Log.d("mmmm", "tanggalBerakhirGymConvert : $tanggalBerakhirGymConvert")
                        Log.d("mmmm", "${tanggalBerakhirGym.before(currentDate)}")
                        //apabila tanggal berakhir sebelumnya tanggal hari ini/ member sebelumnya sudah habis
                        if(tanggalBerakhirGymConvert == "" || tanggalBerakhirGym.before(currentDate) ) {
                            Log.d("mmmm", "halo1")
                            tanggalMulai = tanggalBeliConvert
                            tanggalBerakhirGym = addMonthsToTimestamp(tanggalBeli, durasi)
                        }else{
                            //masih ada member sebelumnya
                            Log.d("mmmm", "halo2")
                            tanggalMulai = tanggalBerakhirGymConvert
                            tanggalBerakhirGym = addMonthsToTimestamp(tanggalBerakhirGym.time, durasi)
                        }
                        tanggalBerakhirGymConvert = formatDateToString(tanggalBerakhirGym)
                        arHistoryP.add(
                            Transaksi(
                                idTransaksi,
                                tanggalBeli,
                                durasi,
                                harga,
                                idPaket,
                                idTrainer,
                                idUser,
                                jenisPembayaran,
                                jenisMember,
                                pilihan,
                                tanggalBeliConvert,tanggalMulai,
                                tanggalBerakhirGymConvert,
                                totalSesi
                            ))
                    }else{
//                        //Member Trainer
//                        //apabila tanggal berakhir sebelumnya tanggal hari ini/ trainer sebelumnya sudah habis
//                        if(tanggalBerakhirTrainerConvert == "" || tanggalBerakhirTrainer.before(currentDate) ) {
//                            Log.d("mmmm", "halo1")
//                            tanggalMulai = tanggalBeliConvert
//                            tanggalBerakhirTrainer = addMonthsToTimestamp(tanggalBeli, durasi)
//                        }else{
//                            //masih ada sesi trainer sebelumnya
//                            Log.d("mmmm", "halo2")
//                            Log.d("mmmm", "tanggalBerakhirTrainer.time : ${tanggalBerakhirTrainer.time}")
//                            Log.d("mmmm", "durasi: ${durasi}")
//                            tanggalMulai = tanggalBerakhirTrainerConvert
//                            tanggalBerakhirTrainer = addMonthsToTimestamp(tanggalBerakhirTrainer.time, durasi)
//                        }
//                        tanggalBerakhirTrainerConvert = formatDateToString(tanggalBerakhirTrainer)
                        Log.d("mmmm", "tanggalBerakhirTrainerConvert: ${tanggalBerakhirTrainerConvert}")
                        arHistoryP.add(
                            Transaksi(
                                idTransaksi,
                                tanggalBeli,
                                durasi,
                                harga,
                                idPaket,
                                idTrainer,
                                idUser,
                                jenisPembayaran,
                                jenisMember,
                                pilihan,
                                tanggalBeliConvert,"-", "-",
                                totalSesi
                            ))
                    }

                    Log.d("xxxx", "arHistory : " + arHistoryP.toString())
                }

            }

            arHistoryP.sortByDescending { it.tanggalBeliTimeStamp }
            _rvHistoryP.adapter?.notifyDataSetChanged()
        }.addOnFailureListener { e ->
            Log.e("haes", "Error fetching data from Firebase", e)
        }
    }

    // menghitung tanggal berakhir

    fun addMonthsToTimestamp(originalTimestamp: Long, monthsToAdd: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = originalTimestamp
        calendar.add(Calendar.MONTH, monthsToAdd)
        Log.d("mmmm", "originalTimestamp : ${originalTimestamp}")
        Log.d("mmmm", "calender time : ${calendar.time}")
        return calendar.time
    }
    //date ke string
    fun formatDateToString(date: Date): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        return dateFormat.format(date)
    }

    //timestamp ke string
    fun convertTimestampToString(timestamp: Long): String {
        val date = Date(timestamp)
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        return dateFormat.format(date)
    }
}
