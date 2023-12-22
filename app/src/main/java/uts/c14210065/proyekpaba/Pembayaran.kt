package uts.c14210065.proyekpaba

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore.Audio.Radio
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.text.NumberFormat
import java.util.Locale

class Pembayaran : AppCompatActivity() {

    lateinit var totalHarga: TextView

    lateinit var bca: RadioButton
    lateinit var mandiri: RadioButton
    lateinit var cd: RadioButton
    lateinit var ovo: RadioButton
    lateinit var gopay: RadioButton
    lateinit var btnBack: ImageView
    lateinit var btnBayar: Button

    var pembayaranMember: Int = 0
    lateinit var formatRupiah: String
    lateinit var loginId: String
    lateinit var jenisMember: String

    val db = Firebase.firestore

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pembayaran)

        btnBack = findViewById(R.id.btnPembayaranBack)

        btnBack.setOnClickListener {
            val intent = Intent(this, utama::class.java)
            startActivity(intent)
        }

        pembayaranMember = intent.getIntExtra("pembayaranMember", 0)
        loginId = intent.getStringExtra("userId") ?: "0"
        jenisMember = intent.getStringExtra("jenisMember") ?: ""
        formatRupiah =
            NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(pembayaranMember)

        if (loginId != "0") {
            totalHarga = findViewById(R.id.tvTotalPembayaran)
            bca = findViewById(R.id.bca)
            mandiri = findViewById(R.id.mandiri)
            cd = findViewById(R.id.creditdebit)
            ovo = findViewById(R.id.ovo)
            gopay = findViewById(R.id.gopay)
            btnBayar = findViewById(R.id.btnBayar)

            totalHarga.text = formatRupiah

            Log.d("pembayaran", formatRupiah)
            Log.d("pembayaran", loginId)

            btnBayar.setOnClickListener {
                PembelianMember()
            }

        }
        else {
            Toast.makeText(this, "Register/Login terlebih dahulu", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
    }

    fun PembelianMember() {
        val userTrainerCollection = db.collection("Member")
        val newDocument = userTrainerCollection.document()

        val newData = hashMapOf(
            "userId" to loginId,
            "jenisMember" to jenisMember
        )

        newDocument.set(newData)
            .addOnSuccessListener {
                Toast.makeText(this, "Anda Telah menjadi member", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, utama::class.java)
                intent.putExtra("navigateToFragment", "fTrainer")
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { er ->
                Log.d("pembayaran", "Data not inserted. Error: $er")
            }
    }
}