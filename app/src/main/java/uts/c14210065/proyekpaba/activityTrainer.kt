package uts.c14210065.proyekpaba

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
    var dataLogin : Boolean = false
    var idLogin: String = ""
    companion object{
        const val login = "GETDATA"
        const val userId = "GETID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trainer)

        tvNama = findViewById<TextView>(R.id.tvNama2)
        tvEmail = findViewById<TextView>(R.id.tvEmail3)
        tvNomor = findViewById<TextView>(R.id.tvNomor)
        tvSkilll = findViewById<TextView>(R.id.tvSkilll)

        dataLogin = intent.getBooleanExtra(utama.login, false)
        idLogin = intent.getStringExtra(utama.userId).toString()
        Log.d("erer", "idLogin : $idLogin")

        val btnIconEdit2 = findViewById<ImageView>(R.id.iconEdit2)

        ReadData(idLogin)

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
                val skills =
                    (result.get("skills") as? List<*>)?.map { it.toString() } ?: emptyList()
                tvSkilll.text = skills.toString()
            }
    }
}