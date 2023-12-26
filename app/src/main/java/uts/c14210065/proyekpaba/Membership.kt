package uts.c14210065.proyekpaba

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.text.NumberFormat
import java.util.Locale

class Membership : AppCompatActivity() {

    lateinit var btnBronze: CardView
    lateinit var btnSilver: CardView
    lateinit var btnGold: CardView
    lateinit var btnDiamond: CardView
    lateinit var btnBack: ImageView

    lateinit var jenisBronze: TextView
    lateinit var jenisSilver: TextView
    lateinit var jenisGold: TextView
    lateinit var jenisDiamond: TextView

    lateinit var durasiBronze: TextView
    lateinit var durasiSilver: TextView
    lateinit var durasiGold: TextView
    lateinit var durasiDiamond: TextView

    lateinit var hargaBronze: TextView
    lateinit var hargaSilver: TextView
    lateinit var hargaGold: TextView
    lateinit var hargaDiamond: TextView


    lateinit var loginId: String
    var formatRupiah: String = ""
    val db = Firebase.firestore

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_membership)

        loginId = intent.getStringExtra("userId")?: "0"

        Log.d("membership", "Login ID: $loginId")

        jenisBronze = findViewById(R.id.jenisBronze)
        durasiBronze = findViewById(R.id.durasiBronze)
        hargaBronze = findViewById(R.id.hargaBronze)

        // isi bronze
        db.collection("PilihanMember").document("bronze").get().addOnSuccessListener { result ->
            var durasi = result.getLong("durasi")?.toInt()
            var harga = result.getLong("harga")?.toInt()
            var jenis = result.getString("jenis").toString()

            jenisBronze.text = jenis.capitalize()
            durasiBronze.text = "$durasi bulan"
            formatRupiah =
                NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(harga)

            hargaBronze.text = formatRupiah
        }

        jenisSilver = findViewById(R.id.jenisSilver)
        durasiSilver = findViewById(R.id.durasiSilver)
        hargaSilver = findViewById(R.id.hargaSilver)

        // isi silver
        db.collection("PilihanMember").document("silver").get().addOnSuccessListener { result ->
            var durasi = result.getLong("durasi")?.toInt()
            var harga = result.getLong("harga")?.toInt()
            var jenis = result.getString("jenis").toString()

            jenisSilver.text = jenis.capitalize()
            durasiSilver.text = "$durasi bulan"
            formatRupiah =
                NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(harga)

            hargaSilver.text = formatRupiah
        }

        jenisGold = findViewById(R.id.jenisGold)
        durasiGold = findViewById(R.id.durasiGold)
        hargaGold = findViewById(R.id.hargaGold)

        // isi gold
        db.collection("PilihanMember").document("gold").get().addOnSuccessListener { result ->
            var durasi = result.getLong("durasi")?.toInt()
            var harga = result.getLong("harga")?.toInt()
            var jenis = result.getString("jenis").toString()

            jenisGold.text = jenis.capitalize()
            durasiGold.text = "$durasi bulan"
            formatRupiah =
                NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(harga)

            hargaGold.text = formatRupiah
        }

        jenisDiamond = findViewById(R.id.jenisDiamond)
        durasiDiamond = findViewById(R.id.durasiDiamond)
        hargaDiamond= findViewById(R.id.hargaDiamond)

        // isi diamond
        db.collection("PilihanMember").document("diamond").get().addOnSuccessListener { result ->
            var durasi = result.getLong("durasi")?.toInt()
            var harga = result.getLong("harga")?.toInt()
            var jenis = result.getString("jenis").toString()

            jenisDiamond.text = jenis.capitalize()
            durasiDiamond.text = "$durasi bulan"
            formatRupiah =
                NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(harga)

            hargaDiamond.text = formatRupiah
        }

        btnBronze = findViewById(R.id.bronze)
        btnSilver = findViewById(R.id.silver)
        btnGold = findViewById(R.id.gold)
        btnDiamond = findViewById(R.id.diamond)
        btnBack = findViewById(R.id.btnMemberBack)

        btnBack.setOnClickListener {
            val intent = Intent(this, utama::class.java)
            intent.putExtra(utama.login, true)
            intent.putExtra(utama.userId, loginId)
            startActivity(intent)
            finish()
        }

        btnBronze.setOnClickListener {
            goToPembayaran(1950000, "bronze", 6)
        }
        btnSilver.setOnClickListener {
            goToPembayaran(3300000, "silver", 12)
        }
        btnGold.setOnClickListener {
            goToPembayaran(4662000, "gold", 18)
        }
        btnDiamond.setOnClickListener {
            goToPembayaran(5976000, "diamond", 24)
        }
    }

    fun showAlert(
        context: Context,
        title: String,
        message: String,
        callback: () -> Unit
    ) {
        val builder = android.app.AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, which ->
            callback.invoke()
            dialog.dismiss()
        }
        val dialog: android.app.AlertDialog = builder.create()
        dialog.show()
    }

    fun goToPembayaran(value: Int, jenis: String, durasi: Int) {
        if (loginId != "0") {
            val intent = Intent(this, Pembayaran::class.java)
            intent.putExtra("pembayaranMember", value)
            intent.putExtra("jenisMember", jenis)
            intent.putExtra("durasiMember", durasi)
            intent.putExtra("userId", loginId)
            startActivity(intent)
        }
        else {
            showAlert(
                this,
                "Pembelian gagal",
                "Silakan Register/Login dahulu",
                {
                    val mainIntent = Intent(this, MainActivity::class.java)
                    startActivity(mainIntent)
                    finish()
                }
            )
        }
    }
}