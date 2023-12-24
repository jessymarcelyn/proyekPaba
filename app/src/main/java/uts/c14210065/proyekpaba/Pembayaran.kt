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
import kotlin.math.log

class Pembayaran : AppCompatActivity() {

    lateinit var totalHarga: TextView

    lateinit var bca: RadioButton
    lateinit var mandiri: RadioButton
    lateinit var cd: RadioButton
    lateinit var ovo: RadioButton
    lateinit var gopay: RadioButton
    lateinit var btnBack: ImageView
    lateinit var btnBayar: Button
    lateinit var tvRincian: TextView
    lateinit var tvJenisMember: TextView

    var pembayaranMember: Int = 0
    var formatRupiah: String = ""
    lateinit var loginId: String
    lateinit var jenisMember: String
    var paket: Int = 0
    lateinit var trainerId: String

    lateinit var pembayaran: String

    val db = Firebase.firestore

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pembayaran)

        btnBack = findViewById(R.id.btnPembayaranBack)
        tvRincian = findViewById(R.id.tvRincian)
        tvJenisMember = findViewById(R.id.tvJenisRincian)

        loginId = intent.getStringExtra("userId") ?: "0"
        jenisMember = intent.getStringExtra("jenisMember") ?: ""
        paket = intent.getIntExtra("paket", 0)
        trainerId = intent.getStringExtra("trainerId")?: ""

        Log.d("pembayaran", "paket: $paket")


        btnBack.setOnClickListener {
            val intent = Intent(this, Membership::class.java)
            intent.putExtra("navigateToFragment", "fJoin")
            intent.putExtra("userId", loginId)
            startActivity(intent)
            finish()
        }

        if (loginId != "0") {
            totalHarga = findViewById(R.id.tvTotalPembayaran)
            bca = findViewById(R.id.bca)
            mandiri = findViewById(R.id.mandiri)
            cd = findViewById(R.id.creditdebit)
            ovo = findViewById(R.id.ovo)
            gopay = findViewById(R.id.gopay)
            btnBayar = findViewById(R.id.btnBayar)

            bca.setOnClickListener {
                pembayaran = "bca"
            }
            mandiri.setOnClickListener {
                pembayaran = "mandiri"
            }
            cd.setOnClickListener {
                pembayaran = "credit/debit"
            }
            ovo.setOnClickListener {
                pembayaran = "ovo"
            }
            gopay.setOnClickListener {
                pembayaran = "gopay"
            }

            if (jenisMember == "bronze" || jenisMember == "silver" || jenisMember == "gold" || jenisMember == "diamond") {
                pembayaranMember = intent.getIntExtra("pembayaranMember", 0)
                formatRupiah =
                    NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(pembayaranMember)

                totalHarga.text = formatRupiah

                tvRincian.text = "Jenis Member: "
                tvJenisMember.text = jenisMember.capitalize()

                Log.d("pembayaran", "loginId: $loginId")

                btnBayar.setOnClickListener {
                    db.collection("users").document(loginId).get()
                        .addOnSuccessListener { result ->
                            if (result.exists()) {
                                val isMember = result.getBoolean("member")
                                if (isMember == true) {
                                    Toast.makeText(
                                        this,
                                        "Anda telah terdaftar sebagai member",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    PembelianMember()
                                }
                            }
                        }
                }
            }

            if (paket == 1 || paket == 2 || paket == 3 || paket == 4 || paket == 5 || paket == 6) {
                tvRincian.text = "Pilihan Paket: "
                

                db.collection("PilihanPaket").document(paket.toString()).get().addOnSuccessListener { result ->
                    if (result.exists()) {
                        val harga = result.getLong("harga")?.toInt()
                        val totalSesi = result.getLong("totalSesi")?.toInt()
                        tvJenisMember.text = "$totalSesi Sesi"
                        formatRupiah =
                            NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(harga)
                        totalHarga.text = formatRupiah
                    }
                }

                btnBayar.setOnClickListener {
                    db.collection("users").document(loginId).get()
                        .addOnSuccessListener { documentSnapshot ->
                            if (documentSnapshot.exists()) {
                                val isMember = documentSnapshot.getBoolean("member")
                                if (isMember == true) {
                                    PembelianPaket()
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Daftar menjadi member dahulu",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                }

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
            "jenisMember" to jenisMember,
            "jenisPembayaran" to pembayaran
        )

        db.collection("users").document(loginId).update("member", true)
            .addOnSuccessListener {
                Log.d("pembayaran", "User data updated!")
            }
            .addOnFailureListener { er ->
                Log.e("pembayaran", "Error updating user data", er)
            }

        newDocument.set(newData)
            .addOnSuccessListener {
                Toast.makeText(this, "Anda Telah menjadi member", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, utama::class.java)
                intent.putExtra("navigateToFragment", "fJoin")
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { er ->
                Log.d("pembayaran", "Data not inserted. Error: $er")
            }
    }

    fun PembelianPaket() {
        db.collection("PilihanPaket").document(paket.toString()).get()
            .addOnSuccessListener { doc ->
                var harga = doc.getLong("harga")?.toInt()
                var totalSesi = doc.getLong("totalSesi")?.toInt()
                var durasi = doc.getLong("durasi")?.toInt()

                val userTrainerCollection = db.collection("UserTrainer")
                val newDocument = userTrainerCollection.document()

                val newData = hashMapOf(
                    "idUser" to loginId,
                    "idTrainer" to trainerId,
                    "totalSesi" to totalSesi,
                    "durasi" to durasi,
                    "harga" to harga
                )

                UpdateClient(loginId)

                newDocument.set(newData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Data inserted successfully!", Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this, utama::class.java)
                        intent.putExtra("navigateToFragment", "fTrainer")
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { er ->
                        Log.d("paketTrainer", "Data not inserted. Error: $er")
                    }
            }
    }

    fun UpdateClient(userId: String) {
        val documentReference = db.collection("Trainer").document(trainerId)
        documentReference.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val currentArray = documentSnapshot.get("clientId") as? List<String> ?: emptyList()

                val newArray = currentArray.toMutableList()
                newArray.add(userId)

                documentReference.update("clientId", newArray)
                    .addOnSuccessListener {
                        Log.d("paketTrainer", "Array updated successfully")
                    }
                    .addOnFailureListener { e ->
                        Log.e("paketTrainer", "Error updating array", e)
                    }
            } else {
                Log.d("paketTrainer", "Document does not exist")
            }
        }
    }
}