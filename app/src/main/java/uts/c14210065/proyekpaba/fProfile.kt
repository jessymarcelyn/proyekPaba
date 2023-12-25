package uts.c14210065.proyekpaba

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
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


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
        val  _iconedit = view.findViewById<ImageView>(R.id.iconEdit)

        tvNama = view.findViewById<TextView>(R.id.tvNama2)
        val tvEmail = view.findViewById<TextView>(R.id.tvEmail3)
        tvNomor = view.findViewById<TextView>(R.id.tvNomor)


//        tvNama.text = nama
//        tvNomor.text = nomor


        _btnLogout.setOnClickListener{
            val intent = Intent(activity, MainActivity::class.java)
            intent.putExtra(utama.login, false)
            intent.putExtra(utama.userId, "0")
            startActivity(intent)
        }

        _btnTrainer.setOnClickListener{

            val intent = Intent(activity, sesiTrainer::class.java)
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

    fun ReadData(idLogin : String){
        db.collection("users")
            .document(idLogin)
            .get().addOnSuccessListener { result ->
                tvNama.text = result.get("nama").toString()
                tvNomor.text = result.get("nomor").toString()

            }
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