package uts.c14210065.proyekpaba

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
        var _TVTotalSesi = findViewById<TextView>(R.id.TVTotalSesi)
        var _tvNamaTrainerTransaksi = findViewById<TextView>(R.id.tvNamaTrainerTransaksi)

        var _TVTotalSesii = findViewById<TextView>(R.id.TVTotalSesii)
        var _tvNamaTrainerTransaksii = findViewById<TextView>(R.id.tvNamaTrainerTransaksii)
        _TVTotalSesii.visibility = View.GONE
        _tvNamaTrainerTransaksii.visibility = View.GONE
        _TVTotalSesi.visibility = View.GONE
        _tvNamaTrainerTransaksi.visibility = View.GONE

        _tvidTransaksii.setText(dataIntent!!.idTransaksi)
        _tvTglTransaksi.setText(":   " + dataIntent!!.tanggalBeli)
        _tvMetodeTransaksi.setText(":   " + dataIntent!!.jenisPembayaran)
        //beri Rp dan seperator koma
        val formattedHarga = NumberFormat.getNumberInstance(Locale("id", "ID")).format(dataIntent!!.harga)
        _tvTotalTransaksi.setText(":   " + "Rp $formattedHarga")
        if(dataIntent!!.jenisMember == "") {
            //trainer
            _tvPaket.setText(":   " + dataIntent!!.pilihan + "\n   (paket ${dataIntent!!.idPaket})")
            _TVTotalSesii.visibility = View.VISIBLE
            _tvNamaTrainerTransaksii.visibility = View.VISIBLE
            _TVTotalSesi.visibility = View.VISIBLE
            _tvNamaTrainerTransaksi.visibility = View.VISIBLE
            _TVTotalSesi.setText(":   " + dataIntent!!.totalSesi.toString())
            val db = Firebase.firestore
            db.collection("Trainer").get().addOnSuccessListener { result ->
                for (document in result) {
                    if (document.id == dataIntent.idTrainer) {
                        val namaTrainer = document.getString("nama") ?: ""
                        _tvNamaTrainerTransaksi.setText(":   " + namaTrainer)
                    }
                }
            }

        }else{
            //member
            _tvPaket.setText(":   " + dataIntent!!.pilihan + "\n   (paket ${dataIntent!!.jenisMember})")
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

        _tvTglMulaiT.setText(":   "  + dataIntent!!.tanggalMulai)
        _tvTglBerakhirT.setText(":   " + dataIntent!!.tanggalBerakhir)
    }
}