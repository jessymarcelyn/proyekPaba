package uts.c14210065.proyekpaba

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class activityHistoryPembayaran : AppCompatActivity() {
    lateinit var _rvHistoryP : RecyclerView
    lateinit var idLogin : String
    private var arHistoryP= arrayListOf<History>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_pembayaran)

        idLogin = intent.getStringExtra(utama.userId).toString()
        _rvHistoryP = findViewById(R.id.rvHistoryP)

        _rvHistoryP.layoutManager = LinearLayoutManager(this)
        val adapterP = adapterHistory(arHistoryP)
        _rvHistoryP.adapter = adapterP
    }
}