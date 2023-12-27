package uts.c14210065.proyekpaba

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import org.mindrot.jbcrypt.BCrypt

class detailTrainer : AppCompatActivity() {

    lateinit var arr: ArrayList<String>
    var idLogin: String = ""
    val db = Firebase.firestore


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_trainer)

        val edNama = findViewById<EditText>(R.id.edNamaT)
        val edEmail = findViewById<EditText>(R.id.edEmailT)
        val edPassword = findViewById<EditText>(R.id.edPasswordT)
        val cbAtheltic = findViewById<CheckBox>(R.id.cbAtheltic)
        val cbShape = findViewById<CheckBox>(R.id.cbShape)
        val cbSpecial = findViewById<CheckBox>(R.id.cbSpecial)
        val cbStrong = findViewById<CheckBox>(R.id.cbStrong)
        val cbWellness = findViewById<CheckBox>(R.id.cbWellness)

        idLogin = intent.getStringExtra(utama.userId).toString()
        arr = ArrayList()

        cbAtheltic.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) arr.add(cbAtheltic.text.toString())
            else arr.remove(cbAtheltic.text.toString())
        }

        cbShape.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) arr.add(cbShape.text.toString())
            else arr.remove(cbShape.text.toString())
        }

        cbSpecial.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) arr.add(cbSpecial.text.toString())
            else arr.remove(cbSpecial.text.toString())
        }
        cbStrong.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) arr.add(cbStrong.text.toString())
            else arr.remove(cbStrong.text.toString())
        }
        cbWellness.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) arr.add(cbWellness.text.toString())
            else arr.remove(cbWellness.text.toString())
        }

        val saveButton: Button = findViewById(R.id.btnSave)
        saveButton.setOnClickListener {
            var updatedNama = edNama.text.toString()
            var updatedEmail = edEmail.text.toString()
            var updatedPassword = edPassword.text.toString()

            if(updatedNama.isNotBlank()) {
                UpdateData("nama", updatedNama)
                edNama.text.clear()
            }
            if(updatedEmail.isNotBlank()) {
                UpdateData("email", updatedEmail)
                edEmail.text.clear()
            }
            if(updatedPassword.isNotBlank()) {
                val hashedPassword = hashPassword(edPassword.text.toString())
                UpdateData("password", hashedPassword)
                edPassword.text.clear()
            }

            if (arr.size >= 1) {
                arr.add("")
                val newData = mapOf(
                    "skills" to arr
                )

                Log.d("detTrainer", "new data: ${newData}")
                db.collection("Trainer") .document(idLogin).update(newData)
                    .addOnSuccessListener {
                        val intent = Intent(this, activityTrainer::class.java)
                        intent.putExtra(activityTrainer.login, true)
                        intent.putExtra(activityTrainer.userId, idLogin)
                        startActivity(intent)
                        Log.d("detTrainer", "update berhasil")
                    }
                    .addOnFailureListener { er ->
                        Log.d("detTrainer", "Error: $er")
                    }
            }

        }
    }
    private fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    private fun UpdateData(field:String, data:String) {
        db.collection("Trainer").document(idLogin).update(field, data)
            .addOnSuccessListener {
                Log.d("detTrainer", "berhasil update nama, email, atau password")
            }
            .addOnFailureListener { e ->
                Log.d("detTrainer", "Error updating document", e)
            }
    }
}