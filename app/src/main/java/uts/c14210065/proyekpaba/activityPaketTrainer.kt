package uts.c14210065.proyekpaba

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
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

        btnBack.setOnClickListener {
            val intent = Intent(this, utama::class.java)
            intent.putExtra("navigateToFragment", "fTrainer")
            startActivity(intent)
            finish()
        }
        TampilkanData()

        btn1.setOnClickListener {
            
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
            db.collection("users").get().addOnSuccessListener { result ->
                for (document in result) {
                    var member = document.getBoolean("member")
                    if (member == true) {
                        val userPaketCollection = db.collection("UserPaket")
                        val newDocument = userPaketCollection.document()

                        // Set the data for the new document
                        val newData = hashMapOf(
                            "paket" to paket,
                            "clientId" to loginId,
                            "trainerId" to trainerId
                        )

                        newDocument.set(newData)
                            .addOnSuccessListener {
                                Log.d("Firestore", "Data inserted successfully!")
                            }
                    }
                }
            }
        }
    }
}