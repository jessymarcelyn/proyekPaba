package uts.c14210065.proyekpaba

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class detailTrainer : AppCompatActivity() {

    lateinit var arr: ArrayList<String>
    var idLogin: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_trainer)

        val edNama = findViewById<EditText>(R.id.edNama)
        val edEmail = findViewById<EditText>(R.id.edEmail)
        val edPassword = findViewById<EditText>(R.id.edPassword)
        val cbAtheltic = findViewById<CheckBox>(R.id.cbAtheltic)
        val cbShape = findViewById<CheckBox>(R.id.cbShape)
        val cbSpecial = findViewById<CheckBox>(R.id.cbSpecial)
        val cbStrong = findViewById<CheckBox>(R.id.cbStrong)
        val cbWellness = findViewById<CheckBox>(R.id.cbWellness)

        idLogin = intent.getStringExtra(utama.userId).toString()
        arr = ArrayList()
        arr.add("")

        cbAtheltic.setOnCheckedChangeListener { _, isChecked ->
            arr.add(cbAtheltic.text.toString())
        }

        cbShape.setOnCheckedChangeListener { _, isChecked ->
            arr.add(cbShape.text.toString())
        }

        cbSpecial.setOnCheckedChangeListener { _, isChecked ->
            arr.add(cbSpecial.text.toString())
        }
        cbStrong.setOnCheckedChangeListener { _, isChecked ->
            arr.add(cbStrong.text.toString())
        }
        cbWellness.setOnCheckedChangeListener { _, isChecked ->
            arr.add(cbWellness.text.toString())
        }

        val saveButton: Button = findViewById(R.id.btnSave)
        saveButton.setOnClickListener {

            val newData = mapOf(
                "skills" to arr
            )

            Log.d("detTrainer", "new data: ${newData}")
            val db = Firebase.firestore
            db.collection("Trainer") .document(idLogin).update(newData)
                .addOnSuccessListener {
                    Log.d("detTrainer", "transaksi berhasil")
                }
                .addOnFailureListener { er ->
                    Log.d("detTrainer", "Error: $er")
                }
        }
    }
}