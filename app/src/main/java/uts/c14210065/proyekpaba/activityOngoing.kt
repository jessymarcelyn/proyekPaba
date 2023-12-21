package uts.c14210065.proyekpaba

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class activityOngoing : AppCompatActivity() {
    companion object {
        const val login = "GETDATA"
        const val userId = "GETID"
    }

    private var arOngoingG = arrayListOf<Gym>()
    private var lastClickedButton: Button? = null
    lateinit var dayDate: Date
    var idLogin: String = ""
    private lateinit var buttons: Array<Button>
    private lateinit var buttonsCategory: Array<Button>
    lateinit var idTrainer : String
    lateinit var _rvOngoing : RecyclerView
    lateinit var userTrainerId : String
    lateinit var _tvHari : TextView
    var state : Int = 0
    private var selectedCategoryButton: Button? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ongoing)

        idLogin = intent.getStringExtra(utama.userId).toString()
        Log.d("trainer", idLogin)

        _rvOngoing = findViewById<RecyclerView>(R.id.rvOngoing)
        val _btnOGym = findViewById<Button>(R.id.btnOGym)
        val _btnOClass = findViewById<Button>(R.id.btnOClass)
        val _btnOTrainer = findViewById<Button>(R.id.btnOTrainer)
        _tvHari = findViewById<TextView>(R.id.tvHari)
        _tvHari.visibility = View.GONE

        val _btnd1 = findViewById<Button>(R.id.btnd1)
        val _btnd2 = findViewById<Button>(R.id.btnd2)
        val _btnd3 = findViewById<Button>(R.id.btnd3)
        val _btnd4 = findViewById<Button>(R.id.btnd4)
        val _btnd5 = findViewById<Button>(R.id.btnd5)
        val _btnd6 = findViewById<Button>(R.id.btnd6)
        val _btnd7 = findViewById<Button>(R.id.btnd7)

        // Get the current date
        var currentDate = Calendar.getInstance().time
        dayDate = currentDate

        buttonsCategory = arrayOf(_btnOGym, _btnOClass, _btnOTrainer)
        buttons = arrayOf(_btnd1, _btnd2, _btnd3, _btnd4, _btnd5, _btnd6, _btnd7)

        _btnOGym.setOnClickListener {
            state = 1
            changeButtonColor(_btnOGym)
            Log.d("mmm", "state1 : $state")
            setupRecyclerView()
        }

        _btnOClass.setOnClickListener {
            state = 2
            changeButtonColor(_btnOClass)
            Log.d("mmm", "state2 : $state")
            setupRecyclerView()
        }

        _btnOTrainer.setOnClickListener {
            state = 3
            changeButtonColor(_btnOTrainer)
            Log.d("mmm", "state3 : $state")
            setupRecyclerView()
        }

        for (i in buttons.indices) {
            val day = Calendar.getInstance()
            day.time = currentDate
            day.add(Calendar.DATE, i)
            buttons[i].visibility = View.GONE

            val formattedDate = SimpleDateFormat("EEE\ndd MMM", Locale("id", "ID")).format(day.time)

            dayTextSet(buttons[i], formattedDate, fGym.size1, fGym.size2)
            buttons[i].setOnClickListener {
                // Reset the background color of the last clicked button to white
                lastClickedButton?.setBackgroundColor(Color.WHITE)

                // Set the background color of the current clicked button to purple
                buttons[i].setBackgroundColor(Color.parseColor("#C9F24D"))

                // Update the last clicked button
                lastClickedButton = buttons[i]

                // tanggal di button
                val calendar = Calendar.getInstance()
                calendar.time = currentDate
                calendar.add(Calendar.DATE, i)
                dayDate = calendar.time
                if(state == 1) {
                    TampilkanDataGym(dayDate)
                }
            }
        }
    }

    private fun setupRecyclerView() {
        if (state == 1) {
            Log.d("mmm", "masuk")
            _rvOngoing.layoutManager = LinearLayoutManager(this)
            val adapterP = adapterOngoingG(arOngoingG, idLogin)
            _rvOngoing.adapter = adapterP

            adapterP.setOnItemClickCallback(object: adapterOngoingG.OnItemClickCallback{
                override fun onItemClicked(data: Gym){

                }

                override fun delData(pos: Int) {
                    AlertDialog.Builder(this@activityOngoing)
                        .setTitle("Pembatalan Sesi")
                        .setMessage("Apakah anda yakin membatalkan sesi gym pada " + arOngoingG[pos].tanggal + " pukul ${arOngoingG[pos].sesi} ?")
                        .setPositiveButton("Ya", DialogInterface.OnClickListener { dialog, which ->
                            val documentId = arOngoingG[pos].idGym
                            val userIdToDelete = idLogin

                            Log.d("www", "docuemntid $documentId")
                            db.collection("GymSesi")
                                .document(documentId)
                                .update("userId", FieldValue.arrayRemove(userIdToDelete))
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this@activityOngoing,
                                        "Gym session canceled successfully.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    // BLM UPDATE JUMLAH KUOTA SESI
                                    TampilkanDataGym(dayDate)
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        this@activityOngoing,
                                        "Failed to cancel gym session.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        })
                        .setNegativeButton(
                            "Tidak", DialogInterface.OnClickListener { dialog, which ->
                                Toast.makeText(this@activityOngoing, "DATA BATAL DIHAPUS", Toast.LENGTH_SHORT)
                                    .show()
                            })
                        .show()
                }
            })
        }
    }
    private fun changeButtonColor(clickedButton: Button) {
        selectedCategoryButton?.setBackgroundColor(Color.WHITE)
        clickedButton.setBackgroundColor(Color.parseColor("#C9F24D"))
        selectedCategoryButton = clickedButton

        changeButtonVisible()
    }


    private fun changeButtonVisible() {
        for (button in buttons) {
            button.visibility = View.GONE
        }
        // Apabila button gym, trainer dan class ditekan maka baru muncul
        when (state) {
            1,2,3 -> {
                _tvHari.visibility = View.VISIBLE
                for (i in buttons.indices) {
                    buttons[i].visibility = View.VISIBLE
                }

            }
        }
    }

    fun dayTextSet(button: Button, text: String, size1: Int, size2: Int) {
        val spannableString = SpannableString(text)

        spannableString.setSpan(
            AbsoluteSizeSpan(size1, true),
            0,
            text.indexOf('\n'),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            AbsoluteSizeSpan(size2, true),
            text.indexOf('\n') + 1,
            spannableString.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        button.text = spannableString

        button.setOnClickListener {
            button.isEnabled = false
        }
    }

    val db = Firebase.firestore

    //menampilkan data gym
    private fun TampilkanDataGym(btnDate: Date) {
        db.collection("GymSesi").get().addOnSuccessListener { result ->
            arOngoingG.clear()
            for (document in result) {
                var Gymid = document.id
                val tanggal = (document["tanggal"] as? Timestamp)?.toDate()
                val kuotaSisa = document.getLong("kuotaSisa")?.toInt() ?: 0

                if(cekDate(tanggal, btnDate)) {
                    val userId =
                        (document["userId"] as? List<*>)?.map { it.toString() } ?: emptyList()

                    if (idLogin in userId) {
                        val kuotaMax = document.getLong("kuotaMax")?.toInt() ?: 0

                        val calendar = Calendar.getInstance()
                        calendar.time = tanggal

                        // ambil jam dan menit dari timestamp tanggal di database
                        val jam = calendar.get(Calendar.HOUR_OF_DAY)
                        val menit = calendar.get(Calendar.MINUTE)

                        //agar jadi 08.00 atau 17.00
                        val formatJam = String.format("%02d", jam)
                        val formatMenit = String.format("%02d", menit)

                        val sesi = "$formatJam:$formatMenit"

                        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
                        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
                        val formattedDate = dateFormat.format(tanggal)
                        Log.d("formatted tanggal : ", formattedDate)

                        arOngoingG.add(
                            Gym(
                                Gymid,
                                formattedDate,
                                sesi,
                                kuotaMax,
                                kuotaSisa,
                                ArrayList(userId)
                            )
                        )
                        Log.d("haes", "Document data: ${document.data}")
                        Log.d("haes", formattedDate)
                    }
                }
            }
            Log.d("haes", "arOngoingG: ${arOngoingG}")
            arOngoingG.sortBy { it.sesi }
            _rvOngoing.adapter?.notifyDataSetChanged()
        }.addOnFailureListener { e ->
            Log.e("haes", "Error fetching data from Firebase", e)
        }
    }

    fun cekDate(timestamp: Date?, btnDate: Date): Boolean {
        val dateFormat = SimpleDateFormat("dd MMM", Locale("id", "ID"))
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")

        val formattedDate = dateFormat.format(timestamp)
        val date = dateFormat.format(btnDate)

        Log.d("cektanggal", "format" + formattedDate)
        Log.d("cektanggal", "button " + date)

        return formattedDate == date
    }
}