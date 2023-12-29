package uts.c14210065.proyekpaba

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class activityTrainer : AppCompatActivity() {
    lateinit var nama: String
    lateinit var tvNama : TextView
    lateinit var tvNomor : TextView
    lateinit var tvEmail : TextView
    lateinit var tvSkilll : TextView

    lateinit var btnLogout: Button
    lateinit var btnInput: Button
    lateinit var btnJadwal: Button

    var dataLogin : Boolean = false
    var idLogin: String = ""
    companion object{
        const val login = "GETDATA"
        const val userId = "GETID"
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trainer)

        tvNama = findViewById(R.id.tvNama2)
        tvEmail = findViewById(R.id.tvEmail3)
        tvNomor = findViewById(R.id.tvNomor)
        tvSkilll = findViewById(R.id.tvSkilll)

        btnLogout = findViewById(R.id.btnLogoutT)
        btnInput = findViewById(R.id.btnInputJadwalT)
        btnJadwal = findViewById(R.id.btnJadwalTrainer)


        dataLogin = intent.getBooleanExtra(utama.login, false)
        idLogin = intent.getStringExtra(utama.userId).toString()
        Log.d("erer", "idLogin : $idLogin")

        val btnIconEdit2 = findViewById<ImageView>(R.id.iconEdit2)

        ReadData(idLogin)

        btnInput.setOnClickListener {
            val intent = Intent(this, inputJadwalTrainer::class.java)
            intent.putExtra(activityTrainer.login, true)
            intent.putExtra(activityTrainer.userId, idLogin)
            startActivity(intent)
        }

        btnLogout.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(utama.login, false)
            intent.putExtra(utama.userId, "0")
            startActivity(intent)
        }

        btnJadwal.setOnClickListener{
            val intent = Intent(this, activityJadwalTrainer::class.java)
            intent.putExtra(activityTrainer.login, true)
            intent.putExtra(activityTrainer.userId, idLogin)
            startActivity(intent)
        }

        btnIconEdit2.setOnClickListener{
            val intent = Intent(this, detailTrainer::class.java)
            intent.putExtra(activityTrainer.login, true)
            intent.putExtra(activityTrainer.userId, idLogin)
            startActivity(intent)
        }
    }

    fun ReadData(idLogin : String){
        val db = Firebase.firestore
        db.collection("Trainer")
            .document(idLogin)
            .get().addOnSuccessListener { result ->
                tvNama.text = result.get("nama").toString()
                tvNomor.text = result.get("nomor").toString()
                tvEmail.text = result.get("email").toString()
                val skills = (result["skills"] as? List<*>)?.map { it.toString() } ?: emptyList()
                val skillFormat = skills.joinToString(", ") { it.capitalize() }.dropLast(2)
                tvSkilll.text = skillFormat
            }
    }
}