package uts.c14210065.proyekpaba

import android.annotation.SuppressLint
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

        loginId = intent.getStringExtra("userId")?: ""
        trainerId = intent.getStringExtra("documentId")?: ""
        Log.d("paketTrainer", "loginId: $loginId")
        Log.d("paketTrainer", "trainerId: $trainerId")

        ivDet = findViewById(R.id.ivDetTrainer)
        tvNama = findViewById(R.id.tvDetNamaTrainer)
        tvClient = findViewById(R.id.tvDetClientTrainer)
        tvSkill = findViewById(R.id.tvDetSkillTrainer)
        btnBack = findViewById(R.id.btnDetTrainerBack)
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
            intent.putExtra("navigateToFragment", "fTrainer")
            intent.putExtra("userId", loginId)
            startActivity(intent)
            finish()
        }

        TampilkanData()

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
                pilihanPaket.visibility = View.GONE
                tvPilihPaket.visibility = View.GONE

                btnCancel.setOnClickListener {
                    CancelTrainer(docId)
                }

            }
            else {
                btnCancel.visibility = View.GONE
                pilihanPaket.visibility = View.VISIBLE
                tvPilihPaket.visibility = View.VISIBLE
            }
        }

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

    fun TampilkanData() {
        db.collection("Trainer").get().addOnSuccessListener { result ->

            for (document in result) {
                if (document.id == trainerId) {
                    var nama = document.getString("nama")
                    val clients = ((document["clientId"] as? List<*>)?.map { it.toString() } ?: emptyList()).size
                    val skills = (document["skills"] as? List<*>)?.map { it.toString() } ?: emptyList()

                    val skillFormat = skills.joinToString(", ") { it.capitalize() }.dropLast(2)

                    tvNama.text = nama
                    tvClient.text = "Active clients: " + clients.toString()
                    tvSkill.text = "Skills: " + skillFormat

                    val fotoSource = "trainer_${nama?.toLowerCase()}"
                    val imageResId = resources.getIdentifier(fotoSource, "drawable", packageName)

                    ivDet.setImageResource(imageResId)
                }
            }
        }
    }

    fun PilihPaket(paket: Int) {
            val intent = Intent(this, Pembayaran::class.java)
            intent.putExtra("userId", loginId)
            intent.putExtra("paket", paket)
            intent.putExtra("trainerId", trainerId)
            startActivity(intent)
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
    }
}