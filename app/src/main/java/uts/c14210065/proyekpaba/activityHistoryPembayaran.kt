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
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class activityHistoryPembayaran : AppCompatActivity() {
    lateinit var _rvHistoryP: RecyclerView
    lateinit var idLogin: String
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
        db.collection("Transaksi").get().addOnSuccessListener { result ->
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

                    val tanggalBerakhir = addMonthsToTimestamp(tanggalBeli, durasi)

                    val tanggalBeliConvert = convertTimestampToString(tanggalBeli)
                    val tanggalBerakhirConvert = formatDateToString(tanggalBerakhir)


                    arHistoryP.add(
                        Transaksi(
                            idTransaksi,
                            durasi,
                            harga,
                            idPaket,
                            idTrainer,
                            idUser,
                            jenisPembayaran,
                            jenisMember,
                            pilihan,
                            tanggalBeliConvert,
                            tanggalBerakhirConvert,
                            totalSesi
                        )
                    )
                    Log.d("xxxx", "arHistory : " + arHistoryP.toString())
                }

            }

            arHistoryP.sortBy { it.tanggalBeli }
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