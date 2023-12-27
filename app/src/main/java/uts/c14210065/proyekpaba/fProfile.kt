package uts.c14210065.proyekpaba

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import java.util.Calendar
import java.util.Date


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [fProfile.newInstance] factory method to
 * create an instance of this fragment.
 */
class fProfile : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    val db = Firebase.firestore
    lateinit var user : User

    lateinit var nama: String
    lateinit var nomor: String
    lateinit var tvNama : TextView
    lateinit var tvNomor : TextView
    lateinit var tvEmail : TextView
    lateinit var tvBerat : TextView
    lateinit var tvTinggi : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val idLogin = arguments?.getString("userId")!!
        Log.d("trainer", "profile" + idLogin)
        ReadData(idLogin)
        val _btnLogout = view.findViewById<Button>(R.id.btnLogout)
        val _btnTrainer = view.findViewById<Button>(R.id.btnTrainer)
        val _btnHistory = view.findViewById<Button>(R.id.btnHistory3)
        val _btnPayment = view.findViewById<Button>(R.id.btnPayment)
        val _btnMember = view.findViewById<Button>(R.id.btnMember)
        val _btnOnGoing = view.findViewById<Button>(R.id.btnOnGoing)
        val _btnCancelH =  view.findViewById<Button>(R.id.btnCancelHistory)
        val  _iconedit = view.findViewById<ImageView>(R.id.iconEdit)

        tvNama = view.findViewById<TextView>(R.id.tvNama2)
        tvEmail = view.findViewById<TextView>(R.id.tvEmail3)
        tvNomor = view.findViewById<TextView>(R.id.tvNomor)
        tvBerat = view.findViewById(R.id.tvBerat)
        tvTinggi = view.findViewById(R.id.tvTinggi)

        _btnLogout.setOnClickListener{
            val intent = Intent(activity, MainActivity::class.java)
            intent.putExtra(utama.login, false)
            intent.putExtra(utama.userId, "0")
            startActivity(intent)
        }

        _btnTrainer.setOnClickListener{
            db.collection("UserTrainer").get().addOnSuccessListener { result ->
                var ketemu = false
                for (document in result) {
                    var idUser = document.getString("idUser") ?: ""
                    var durasi = document.getLong("durasiPaket")?.toInt() ?: 0
                    val currentDate = Calendar.getInstance().time
                    val tanggalMulai = (document["tanggalMulai"] as? Timestamp)?.toDate()?.time ?: 0
                    val tanggalBerakhir = addMonthsToTimestamp(tanggalMulai, durasi)
                    if (idUser == idLogin && tanggalBerakhir >= currentDate) {
                        val intent = Intent(activity, sesiTrainer::class.java)
                        intent.putExtra(utama.login, true)
                        intent.putExtra(utama.userId, idLogin)
                        startActivity(intent)
                        ketemu = true
                        break
                    }
                }
                if(!ketemu){
                    showAlert(
                        requireContext(),
                        "Akses Gagal",
                        "Anda belum memiliki paket dengan trainer kami."
                    )
                }
            }

        }

        _btnCancelH.setOnClickListener{
            val intent = Intent(activity, activityCancel::class.java)
            intent.putExtra(utama.login, true)
            intent.putExtra(utama.userId, idLogin)
            startActivity(intent)
        }

        _btnOnGoing.setOnClickListener{
            val intent = Intent(activity, activityOngoing::class.java)
            intent.putExtra(utama.login, true)
            intent.putExtra(utama.userId, idLogin)
            startActivity(intent)
        }

        _btnPayment.setOnClickListener{
            val intent = Intent(activity, activityHistoryPembayaran::class.java)
            intent.putExtra(utama.login, true)
            intent.putExtra(utama.userId, idLogin)
            startActivity(intent)
        }

        _btnMember.setOnClickListener{
            val intent = Intent(activity, activityDetailMembership::class.java)
            intent.putExtra(utama.login, true)
            intent.putExtra(utama.userId, idLogin)
            startActivity(intent)
        }
        _btnHistory.setOnClickListener{
            val intent = Intent(activity, activityHistory::class.java)
            intent.putExtra(utama.login, true)
            intent.putExtra(utama.userId, idLogin)
            startActivity(intent)
        }

        _iconedit.setOnClickListener{
            (activity as utama).goToPage(fEditProfile())
        }
    }

    // menghitung tanggal berakhir
    fun addMonthsToTimestamp(originalTimestamp: Long, monthsToAdd: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = originalTimestamp
        calendar.add(Calendar.MONTH, monthsToAdd)
        return calendar.time
    }

    fun ReadData(idLogin : String){
        db.collection("users")
            .document(idLogin)
            .get().addOnSuccessListener { result ->
                tvNama.text = result.get("nama").toString()
                tvNomor.text = result.get("nomor").toString()
                tvEmail.text = result.get("email").toString()
                tvBerat.text = result.get("berat").toString() + " kg"
                tvTinggi.text = result.get("tinggi").toString() + " cm"
            }
    }
    fun showAlert(context: Context, title: String, message: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, which ->
            // Handle the "OK" button click if needed
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_f_profile, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment fProfile.
         */
        // TODO: Rename and change types and number of parameters

        const val login = "GETDATA"
        const val userId = "GETID"
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fProfile().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}