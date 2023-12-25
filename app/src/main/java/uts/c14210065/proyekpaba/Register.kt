package uts.c14210065.proyekpaba

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import org.mindrot.jbcrypt.BCrypt

class Register : AppCompatActivity() {

    val db = Firebase.firestore

    lateinit var _etNomor: EditText
    lateinit var _etPassword: EditText
    lateinit var _etNama : EditText
    lateinit var _etEmail : EditText

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrasi)

        _etNama = findViewById(R.id.etNama)
        _etNomor = findViewById(R.id.etNomor)
        _etPassword = findViewById(R.id.etPassword)
        _etEmail = findViewById(R.id.etEmail)
        val genderRadioGroup: RadioGroup = findViewById(R.id.genderRadioGroup)
        var selectedGender : String =""
        genderRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            // Check which radio button is selected
            val selectedRadioButton: RadioButton? = if (checkedId != View.NO_ID) {
                findViewById(checkedId)
            } else {
                null
            }
            // Get the text of the selected radio button
            selectedGender = selectedRadioButton?.text.toString()

        }




        val btnRegist = findViewById<Button>(R.id.btnRegist)

        btnRegist.setOnClickListener{
            val hashedPassword = hashPassword(_etPassword.text.toString())
            if (_etNama.text.toString().isNullOrBlank() || _etNomor.text.toString().isNullOrBlank() || _etPassword.text.toString().isNullOrBlank()||_etEmail.text.toString().isNullOrBlank()) {
                // Salah satu atau lebih field kosong
                Toast.makeText(this@Register, "Harap isi semua kolom", Toast.LENGTH_SHORT).show()
            }else{
                Log.d("masuk", "iya")
                checkNumber(_etNomor.text.toString()){registered ->
                    Log.d("registerd", registered.toString())
                    if(registered){
                        Toast.makeText(this@Register, "Nomor telah terdaftar", Toast.LENGTH_SHORT).show()
                    }else {
                        CreateData(
                            _etNama.text.toString(),
                            _etNomor.text.toString(),
                            hashedPassword,
                            selectedGender,
                            _etEmail.text.toString()
                        )
                        _etNama.text.clear()
                        _etNomor.text.clear()
                        _etPassword.text.clear()
                        _etEmail.text.clear()
                        genderRadioGroup.clearCheck()
                    }
                }

            }

        }

    }

    fun checkNumber(number:String, callback: (Boolean) -> Unit) {

        db.collection("users")
            .whereEqualTo("nomor", number)
            .get()
            .addOnSuccessListener {documents ->
                val isTerdaftar = !documents.isEmpty
                callback(isTerdaftar)
            }
            .addOnFailureListener { exception ->
                callback(false)
                Log.w("TAG", "Error getting documents: ", exception)
            }
    }
    private fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    fun CreateData(nama: String, nomor: String, password: String, gender: String, email: String){
        val userCollection = db.collection("users")

        // Membuat data yang akan disimpan di Firestore
        val userData = hashMapOf(
            "nama" to nama,
            "nomor" to nomor,
            "password" to password,
            "gender" to gender,
            "email" to email,
            "member" to false,
            "berat" to 0,
            "tinggi" to 0
        )

        // Menambahkan data ke Firestore
        userCollection.document(nomor.toString()).set(userData)
            .addOnSuccessListener {
                val intent = Intent(this@Register, MainActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                // Handle ketika terjadi kesalahan
                // Misalnya, tampilkan pesan kesalahan atau log kesalahan
                Log.d("gagal", "iya")
            }
    }
}