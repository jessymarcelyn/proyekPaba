package uts.c14210065.proyekpaba

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContentProviderCompat.requireContext

class Membership : AppCompatActivity() {

    lateinit var btnBronze: CardView
    lateinit var btnSilver: CardView
    lateinit var btnGold: CardView
    lateinit var btnDiamond: CardView
    lateinit var btnBack: ImageView

    lateinit var loginId: String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_membership)

        loginId = intent.getStringExtra("userId")?: "0"

        Log.d("membership", "Login ID: $loginId")

        btnBronze = findViewById(R.id.bronze)
        btnSilver = findViewById(R.id.silver)
        btnGold = findViewById(R.id.gold)
        btnDiamond = findViewById(R.id.diamond)
        btnBack = findViewById(R.id.btnMemberBack)

        Log.d("ddd", "idLogin membership : $loginId")
        btnBack.setOnClickListener {
            val intentWithData = Intent(this@Membership, utama::class.java).apply {
                putExtra("navigateToFragment", "fJoin")
                putExtra(utama.userId, loginId)
                if(loginId != "0"){
                    Log.d("ddd", "Masuk1")
                    putExtra(utama.login, true)
                }else{
                    Log.d("ddd", "Masuk2")
                    putExtra(utama.login, false)
                }
            }
            startActivity(intentWithData)
//            val intent = Intent(this, utama::class.java)
//            intent.putExtra("navigateToFragment", "fJoin")
//            intent.putExtra("userId", loginId)
//            if(loginId != "0"){
//                intent.putExtra("dataLogin", true)
//            }else{
//                intent.putExtra("dataLogin", false)
//            }
//            startActivity(intent)
//            finish()
        }

        btnBronze.setOnClickListener {
            goToPembayaran(1950000, "bronze")
        }
        btnSilver.setOnClickListener {
            goToPembayaran(3300000, "silver")
        }
        btnGold.setOnClickListener {
            goToPembayaran(4662000, "gold")
        }
        btnDiamond.setOnClickListener {
            goToPembayaran(5976000, "diamond")
        }
    }

    fun goToPembayaran(value: Int, jenis: String) {
        val intent = Intent(this, Pembayaran::class.java)
        intent.putExtra("pembayaranMember", value)
        intent.putExtra("jenisMember", jenis)
        intent.putExtra("userId", loginId)
        startActivity(intent)
    }
}