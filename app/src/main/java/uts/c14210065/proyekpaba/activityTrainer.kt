package uts.c14210065.proyekpaba

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class activityTrainer : AppCompatActivity() {
    lateinit var nama: String
    lateinit var tvNama : TextView
    lateinit var tvNomor : TextView
    lateinit var tvEmail : TextView
    lateinit var tvSkilll : TextView
    var idLogin: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trainer)

        tvNama = findViewById<TextView>(R.id.tvNama2)
        tvEmail = findViewById<TextView>(R.id.tvEmail3)
        tvNomor = findViewById<TextView>(R.id.tvNomor)
        tvSkilll = findViewById<TextView>(R.id.tvSkilll)

        idLogin = intent.getStringExtra(utama.userId).toString()

        ReadData(idLogin)


    }

    fun ReadData(idLogin : String){
        val db = Firebase.firestore
        db.collection("trainer")
            .document(idLogin)
            .get().addOnSuccessListener { result ->
                tvNama.text = result.get("nama").toString()
                tvNomor.text = result.get("nomor").toString()
                tvEmail.text = result.get("email").toString()
                tvSkilll.text = result.get("skills").toString()
            }
    }
}