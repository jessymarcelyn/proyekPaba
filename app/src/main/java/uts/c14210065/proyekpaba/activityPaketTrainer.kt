package uts.c14210065.proyekpaba

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import uts.c14210065.proyekpaba.model.TrainerModel
import kotlin.math.log

class activityPaketTrainer : AppCompatActivity() {

    lateinit var btnBack: ImageView
    lateinit var ivDet: ImageView
    lateinit var tvNama: TextView
    lateinit var tvClient: TextView
    lateinit var tvSkill: TextView

    lateinit var btn1: Button
    lateinit var btn2: Button
    lateinit var btn3: Button
    lateinit var btn4: Button
    lateinit var btn5: Button
    lateinit var btn6: Button
    lateinit var btnCancel: Button

    lateinit var tvPilihPaket: TextView
    lateinit var pilihanPaket: LinearLayout

    lateinit var trainerId: String
    lateinit var loginId: String

    val db = Firebase.firestore

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paket_trainer)

        loginId = intent.getStringExtra("userId")?: "0"
        trainerId = intent.getStringExtra("documentId")?: ""
        Log.d("paketTrainer", "loginId: $loginId")
        Log.d("paketTrainer", "trainerId: $trainerId")

        ivDet = findViewById(R.id.ivDetTrainer)
        tvNama = findViewById(R.id.tvDetNamaTrainer)
        tvClient = findViewById(R.id.tvDetClientTrainer)
        tvSkill = findViewById(R.id.tvDetSkillTrainer)
        btnBack = findViewById(R.id.btnPaketTrainerBack)
        btnCancel = findViewById(R.id.btnCancel)

        btn1 = findViewById(R.id.btnPaket1)
        btn2 = findViewById(R.id.btnPaket2)
        btn3 = findViewById(R.id.btnPaket3)
        btn4 = findViewById(R.id.btnPaket4)
        btn5 = findViewById(R.id.btnPaket5)
        btn6 = findViewById(R.id.btnPaket6)

        pilihanPaket = findViewById(R.id.pilihanPaket)
        tvPilihPaket = findViewById(R.id.tvPilihPaket)

        btnBack.setOnClickListener {
            val intent = Intent(this, utama::class.java)
            intent.putExtra(utama.login, true)
            intent.putExtra(utama.userId, loginId)
            startActivity(intent)
            finish()
        }

        TampilkanData()

        btn1.setOnClickListener {
            PilihPaket(1)
        }
        btn2.setOnClickListener {
            PilihPaket(2)
        }
        btn3.setOnClickListener {
            PilihPaket(3)
        }
        btn4.setOnClickListener {
            PilihPaket(4)
        }
        btn5.setOnClickListener {
            PilihPaket(5)
        }
        btn6.setOnClickListener {
            PilihPaket(6)
        }
    }

    fun showAlert(
        context: Context,
        title: String,
        message: String,
        callback: () -> Unit
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, which ->
            callback.invoke()
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun TampilkanData() {
        db.collection("Trainer").get().addOnSuccessListener { result ->

            for (document in result) {
                if (document.id == trainerId) {
                    var nama = document.getString("nama")
                    val clients = ((document["clientId"] as? List<*>)?.map { it.toString() } ?: emptyList()).size
                    val skills = (document["skills"] as? List<*>)?.map { it.toString() } ?: emptyList()
                    val foto = document.getString("foto")

                    val skillFormat = skills.joinToString(", ") { it.capitalize() }.dropLast(2)

                    tvNama.text = nama
                    tvClient.text = "Active clients: " + clients.toString()
                    if (skillFormat == "") {
                        tvSkill.text = ""
                    }
                    else {
                        tvSkill.text = "Skills: $skillFormat"
                    }



//                    val fotoSource = "trainer_${nama?.toLowerCase()}"
                    val imageResId = resources.getIdentifier(foto, "drawable", packageName)

                    ivDet.setImageResource(imageResId)
                }
            }
        }

        db.collection("UserTrainer").get().addOnSuccessListener { result ->
            var docId: String? = null
            for (document in result) {

                var idUser = document.getString("idUser")
                var idTrainer = document.getString("idTrainer")

                if (idUser == loginId && idTrainer == trainerId) {
                    docId = document.id
                    break
                }
            }
            if (docId != null) {
                btnCancel.visibility = View.VISIBLE
                btnCancel.setOnClickListener {
                    CancelTrainer(docId)
                }
            } else {
                btnCancel.visibility = View.GONE
            }
        }
    }

    fun PilihPaket(paket: Int) {
        if (loginId != "0") {
            val intent = Intent(this, Pembayaran::class.java)
            intent.putExtra("userId", loginId)
            intent.putExtra("paket", paket)
            intent.putExtra("trainerId", trainerId)
            startActivity(intent)
        } else {
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

    private fun CancelTrainer(documentId: String) {
        db.collection("UserTrainer").document(documentId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Cancel Trainer berhasil", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Log.d("paket", "Error: $e")
            }

        db.collection("Trainer").document(trainerId)
            .update("clientId", FieldValue.arrayRemove(loginId))
            .addOnSuccessListener {
                Log.d("paket", "Client ID removed")
                finish()
            }
            .addOnFailureListener { e ->
                Log.d("paket", "Error: $e")
            }

        db.collection("Transaksi").get().addOnSuccessListener { result ->
            var docId: String? = null
            for (document in result) {
                var idUser = document.getString("idUser")
                var idTrainer = document.getString("idTrainer")

                if (idUser == loginId && idTrainer == trainerId) {
                    docId = document.id
                    break
                }
            }
            Log.d("transaksi", "docid: $docId")
            if (docId != null) {
                db.collection("Transaksi").document(docId)
                    .update("status", 0)
                    .addOnSuccessListener {
                        Log.d("paket", "status updated")
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Log.d("paket", "Error: $e")
                    }
            } else {
                Log.d("paket", "gagal")
            }
        }
    }
}