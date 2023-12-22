package uts.c14210065.proyekpaba

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.SimpleAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import org.mindrot.jbcrypt.BCrypt

class Login : AppCompatActivity() {

    companion object {
        const val login = "GETDATA"
        const val userId = "GETID"
    }

    val db = Firebase.firestore

    lateinit var _etNomor: EditText
    lateinit var _etPassword: EditText
    lateinit var _tvGagal: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val dataLogin = intent.getBooleanExtra(utama.login, false)

        _etNomor = findViewById<EditText>(R.id.etNomor)
        _etPassword = findViewById<EditText>(R.id.etPassword)

        _tvGagal = findViewById<TextView>(R.id.tvGagal)

        val _btnLogin2 = findViewById<Button>(R.id.btnLogin2)

//        _btnLogin2.isEnabled = !(_etNomor?.text.isNullOrEmpty() || _etPassword?.text.isNullOrEmpty())

        _etNomor?.addTextChangedListener {
            _btnLogin2.isEnabled = !(_etNomor?.text.isNullOrEmpty() || _etPassword?.text.isNullOrEmpty())
        }

        _etPassword?.addTextChangedListener {
            _btnLogin2.isEnabled = !(_etNomor?.text.isNullOrEmpty() || _etPassword?.text.isNullOrEmpty())
        }

        _btnLogin2.setOnClickListener {
            ReadData()
        }
    }
    fun hashPassword(password: String): String {
        val salt = BCrypt.gensalt()
        return BCrypt.hashpw(password, salt)
    }

    fun checkPassword(candidate: String, hashed: String): Boolean {
        return BCrypt.checkpw(candidate, hashed)
    }
    fun ReadData() {
        val enteredNoTelp = _etNomor.text.toString()
        val enteredPassword = _etPassword.text.toString()

        db.collection("users").get().addOnSuccessListener { result ->

            for (document in result) {
                var id = document.id
                val noTelp = document.data.get("nomor").toString()
                val password = document.data.get("password").toString()

//              check hashed password
                val passwordMatches = checkPassword(enteredPassword, password)
                Log.d("pass", passwordMatches.toString())

                if (enteredNoTelp == noTelp && passwordMatches) {
//                if (enteredNoTelp == noTelp && enteredPassword==password) {

                    val intentWithData = Intent(this@Login, utama::class.java).apply {
                        putExtra(utama.login, true)
                        putExtra(utama.userId, id.toString())
                        Log.d("Dataid", id.toString())
                    }
                    startActivity(intentWithData)
                    return@addOnSuccessListener
                }
            }

            _tvGagal.visibility = View.VISIBLE
        }
    }


}