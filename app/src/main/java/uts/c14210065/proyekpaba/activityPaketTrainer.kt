package uts.c14210065.proyekpaba

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
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

    lateinit var trainerId: String
    lateinit var loginId: String

    val db = Firebase.firestore

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

        btn1 = findViewById(R.id.btnPaket1)
        btn2 = findViewById(R.id.btnPaket2)
        btn3 = findViewById(R.id.btnPaket3)
        btn4 = findViewById(R.id.btnPaket4)
        btn5 = findViewById(R.id.btnPaket5)
        btn6 = findViewById(R.id.btnPaket6)

        btnBack.setOnClickListener {
            val intent = Intent(this, utama::class.java)
            intent.putExtra("navigateToFragment", "fTrainer")
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

    override fun onBackPressed() {
        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
        } else {
            super.onBackPressed()
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
        if (loginId != "0"){
            db.collection("users").document(loginId).get().addOnSuccessListener { result ->
                var member = result.getBoolean("member")
                if (member == true) {
                    db.collection("PilihanPaket").document(paket.toString()).get()
                        .addOnSuccessListener { doc ->
                            var harga = doc.getLong("harga")?.toInt()
                            var totalSesi = doc.getLong("totalSesi")?.toInt()

                            val userTrainerCollection = db.collection("UserTrainer")
                            val newDocument = userTrainerCollection.document()

                            val newData = hashMapOf(
                                "idUser" to loginId,
                                "idTrainer" to trainerId,
                                "totalSesi" to totalSesi,
                                "harga" to harga
                            )

                            UpdateClient(loginId)
                            
                            newDocument.set(newData)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Data inserted successfully!", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, utama::class.java)
                                    intent.putExtra("navigateToFragment", "fTrainer")
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener { er ->
                                    Log.d("paketTrainer", "Data not inserted. Error: $er")
                                }
                        }
                } else {
                    Toast.makeText(
                        this,
                        "Silakan menjadi member terlebih dahulu",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("paketTrainer", "Not member")
                }
            }
        }
        else {
            Toast.makeText(this, "Register/Login terlebih dahulu", Toast.LENGTH_SHORT).show()
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