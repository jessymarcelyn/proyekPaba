package uts.c14210065.proyekpaba

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView

class utama : AppCompatActivity() {
    companion object{
        const val login = "GETDATA"
        const val dataId = "GETDATA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_utama)

        val _ivHome = findViewById<ImageView>(R.id.ivHome)
        val _ivGym = findViewById<ImageView>(R.id.ivGym)
        val _ivJoin = findViewById<ImageView>(R.id.ivJoin)
        val _ivClass = findViewById<ImageView>(R.id.ivClass)
        val _ivProfile = findViewById<ImageView>(R.id.ivProfile)

        val dataLogin = intent.getBooleanExtra(login, false)
        val idLogin = intent.getStringExtra(dataId)

        Log.d("idLogin", idLogin.toString())
        Log.d("dataLogin", dataLogin.toString())
        _ivProfile.setOnClickListener {
            if (!dataLogin) {
                val intentWithData = Intent(this@utama, MainActivity::class.java).apply {
                    putExtra(utama.login, false)
                    putExtra(utama.dataId, "0")
                }
                startActivity(intentWithData)
            }else{
                val mFragmentManager = supportFragmentManager
                val mfSatu = fProfile()

//            val mBundle = Bundle()
//            mBundle.putParcelableArrayList("dataMatkul", isiMatkul)
//            mfSatu.arguments = mBundle  // Set the bundle as arguments for hlm2

                mFragmentManager.findFragmentByTag(fProfile::class.java.simpleName)
                mFragmentManager.beginTransaction().replace(R.id.frameLayout, mfSatu, fProfile::class.java.simpleName).commit()
            }
        }

        _ivHome.setOnClickListener{
            val mFragmentManager = supportFragmentManager
            val mfSatu = fHome()

//            val mBundle = Bundle()
//            mBundle.putParcelableArrayList("dataMatkul", isiMatkul)
//            mfSatu.arguments = mBundle  // Set the bundle as arguments for hlm2


            mFragmentManager.findFragmentByTag(fHome::class.java.simpleName)
            mFragmentManager.beginTransaction().replace(R.id.frameLayout, mfSatu, fHome::class.java.simpleName).commit()
        }

        _ivGym.setOnClickListener{
            val mFragmentManager = supportFragmentManager
            val mfSatu = fGym()

//            val mBundle = Bundle()
//            mBundle.putParcelableArrayList("dataMatkul", isiMatkul)
//            mfSatu.arguments = mBundle  // Set the bundle as arguments for hlm2


            mFragmentManager.findFragmentByTag(fGym::class.java.simpleName)
            mFragmentManager.beginTransaction().replace(R.id.frameLayout, mfSatu, fGym::class.java.simpleName).commit()
        }

        _ivJoin.setOnClickListener{
            val mFragmentManager = supportFragmentManager
            val mfSatu = fJoin()

//            val mBundle = Bundle()
//            mBundle.putParcelableArrayList("dataMatkul", isiMatkul)
//            mfSatu.arguments = mBundle  // Set the bundle as arguments for hlm2


            mFragmentManager.findFragmentByTag(fJoin::class.java.simpleName)
            mFragmentManager.beginTransaction().replace(R.id.frameLayout, mfSatu, fJoin::class.java.simpleName).commit()
        }

        _ivClass.setOnClickListener{
            val mFragmentManager = supportFragmentManager
            val mfSatu = fClass()

//            val mBundle = Bundle()
//            mBundle.putParcelableArrayList("dataMatkul", isiMatkul)
//            mfSatu.arguments = mBundle  // Set the bundle as arguments for hlm2


            mFragmentManager.findFragmentByTag(fClass::class.java.simpleName)
            mFragmentManager.beginTransaction().replace(R.id.frameLayout, mfSatu, fClass::class.java.simpleName).commit()
        }


    }
}