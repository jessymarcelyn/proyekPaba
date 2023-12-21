package uts.c14210065.proyekpaba

import android.annotation.SuppressLint
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
        _etPassword = findViewById(R.id.etPasswordRegist)
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

            // Handle the selected gender (e.g., display in a Toast)
//            Toast.makeText(this, "Selected Gender: $selectedGender", Toast.LENGTH_SHORT).show()
        }




        val btnRegist = findViewById<Button>(R.id.btnRegist)

        btnRegist.setOnClickListener{
            if (_etNama.text.toString().isNullOrBlank() || _etNomor.text.toString().isNullOrBlank() || _etPassword.text.toString().isNullOrBlank()||_etEmail.text.toString().isNullOrBlank()) {
                // Salah satu atau lebih field kosong
                Toast.makeText(this@Register, "Harap isi semua kolom", Toast.LENGTH_SHORT).show()
            }else{
                Log.d("pass match" , _etPassword.text.toString())
                val hashedPassword = hashPassword(_etPassword.text.toString())
                Log.d("masuk", "iya")
                checkNumber(_etNomor.text.toString().toInt()){registered ->
                    Log.d("registerd", registered.toString())
                    if(registered){
                        Toast.makeText(this@Register, "Nomor telah terdaftar", Toast.LENGTH_SHORT).show()
                    }else {
                        CreateData(
                            _etNama.text.toString(),
                            _etNomor.text.toString().toInt(),
                            hashedPassword,
//                            _etPassword.text.toString(),
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

    fun checkNumber(number:Int, callback: (Boolean) -> Unit) {

        db.collection("users")
            .whereEqualTo("nomor", number)
            .get()
            .addOnSuccessListener { documents ->
                val isTerdaftar = !documents.isEmpty
                Log.d("trdaftar", isTerdaftar.toString())
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

    fun CreateData(nama: String, nomor: Int, password: String, gender: String, email: String){
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
            .addOnSuccessListener { documentReference ->
                // Handle ketika data berhasil ditambahkan
                // documentReference.id berisi ID unik dari data yang baru ditambahkan


            }
            .addOnFailureListener { e ->
                // Handle ketika terjadi kesalahan
                // Misalnya, tampilkan pesan kesalahan atau log kesalahan
                Log.d("gagal", "iya")
            }
    }
}