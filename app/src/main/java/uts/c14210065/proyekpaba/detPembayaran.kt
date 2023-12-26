package uts.c14210065.proyekpaba

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.util.Locale

class detPembayaran : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_det_pembayaran)

        val dataIntent = intent.getParcelableExtra<Transaksi>("kirimData")

        var _tvidTransaksii = findViewById<TextView>(R.id.tvidTransaksii)
        var _tvTglTransaksi = findViewById<TextView>(R.id.tvTglTransaksi)
        var _tvMetodeTransaksi = findViewById<TextView>(R.id.tvMetodeTransaksi)
        var _tvTotalTransaksi = findViewById<TextView>(R.id.tvTotalTransaksi)
        var _tvPaket = findViewById<TextView>(R.id.tvPaket)
        var _tvDurasiTransaksi = findViewById<TextView>(R.id.tvDurasiTransaksi)
        var _tvTglMulaiT = findViewById<TextView>(R.id.tvTglMulaiT)
        var _tvTglBerakhirT = findViewById<TextView>(R.id.tvTglBerakhirT)
        var _tvStatus = findViewById<TextView>(R.id.tvStatus)


        var _tvMulaii = findViewById<TextView>(R.id.tvMulaii)
        var _tvBerakhirr = findViewById<TextView>(R.id.tvBerakhirr)

        var _tvStatuss = findViewById<TextView>(R.id.tvStatuss)

        _tvStatuss.visibility = View.GONE
        _tvStatus.visibility = View.GONE

        _tvidTransaksii.setText(dataIntent!!.idTransaksi)
        _tvTglTransaksi.setText(":   " + dataIntent!!.tanggalBeli)
        _tvMetodeTransaksi.setText(":   " + dataIntent!!.jenisPembayaran)

        //beri Rp dan seperator koma
        val formattedHarga = NumberFormat.getNumberInstance(Locale("id", "ID")).format(dataIntent!!.harga)
        _tvTotalTransaksi.setText(":   " + "Rp $formattedHarga")
        if(dataIntent!!.jenisMember == "") {
            //trainer
            _tvMulaii.setText("Total Sesi ")
            _tvBerakhirr.setText("Nama Trainer ")
            _tvPaket.setText(":   " + dataIntent!!.pilihan + "\n   (paket ${dataIntent!!.idPaket})")
            _tvStatuss.visibility = View.VISIBLE
            _tvStatus.visibility = View.VISIBLE
            _tvStatus.setText(":   " + dataIntent!!.status.toString())
            _tvTglMulaiT.setText(":   " + dataIntent!!.totalSesi.toString())
            val db = Firebase.firestore
            db.collection("Trainer").get().addOnSuccessListener { result ->
                for (document in result) {
                    if (document.id == dataIntent.idTrainer) {
                        val namaTrainer = document.getString("nama") ?: ""
                        _tvTglBerakhirT.setText(":   " + namaTrainer)
                    }
                }
            }

        }else{
            //member
            _tvPaket.setText(":   " + dataIntent!!.pilihan + "\n   (paket ${dataIntent!!.jenisMember})")
            _tvTglMulaiT.setText(":   "  + dataIntent!!.tanggalMulai)
            _tvTglBerakhirT.setText(":   " + dataIntent!!.tanggalBerakhir)
        }
        if (dataIntent!!.durasi >= 12) {
            if (dataIntent!!.durasi % 12 == 0) {
                val tahun = dataIntent!!.durasi / 12
                _tvDurasiTransaksi.setText(":   $tahun tahun")
            } else {
                val tahun = dataIntent!!.durasi / 12
                val bulan = dataIntent!!.durasi % 12
                _tvDurasiTransaksi.setText(":   $tahun tahun $bulan bulan")
            }
        } else {
            _tvDurasiTransaksi.setText(":   ${dataIntent!!.durasi} bulan")
        }

    }
}