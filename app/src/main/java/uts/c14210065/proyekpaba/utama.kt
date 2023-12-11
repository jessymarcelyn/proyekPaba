package uts.c14210065.proyekpaba

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.fragment.app.Fragment

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
        val _ivTrainer = findViewById<ImageView>(R.id.ivTrainer)

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
            goToPage(fHome())
        }

        _ivGym.setOnClickListener{
            goToPage(fGym())
        }

        _ivJoin.setOnClickListener{
            goToPage(fJoin())
        }

        _ivClass.setOnClickListener{
            goToPage(fClass())
        }

        _ivTrainer.setOnClickListener{
            goToPage(fTrainer())
        }
    }
    private fun goToPage(fragment: Fragment) {
        //            val mBundle = Bundle()
        //            mBundle.putParcelableArrayList("dataMatkul", isiMatkul)
        //            mfSatu.arguments = mBundle  // Set the bundle as arguments for hlm2

        val mFragmentManager = supportFragmentManager
        val mfSatu = fragment

        mFragmentManager.findFragmentByTag(fragment::class.java.simpleName)
        mFragmentManager.beginTransaction().replace(R.id.frameLayout, mfSatu, fragment::class.java.simpleName).commit()
    }
}