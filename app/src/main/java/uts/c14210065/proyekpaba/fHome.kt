package uts.c14210065.proyekpaba

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import uts.c14210065.proyekpaba.adapter.adapterHomeTrainer
import uts.c14210065.proyekpaba.adapter.adapterTrainer
import uts.c14210065.proyekpaba.model.TrainerModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [fHome.newInstance] factory method to
 * create an instance of this fragment.
 */
class fHome : Fragment() {
    lateinit var rvTrainer: RecyclerView
    lateinit var arTrainer: ArrayList<TrainerModel>

    lateinit var rvClass: RecyclerView
    lateinit var arClass: ArrayList<GymClass>

    lateinit var tvWelcome: TextView

//    lateinit var rvGym: RecyclerView
//    lateinit var arGym: ArrayList<Gym>

    val db = Firebase.firestore

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_f_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Trainer
        rvTrainer = view.findViewById(R.id.rvHomeTrainer)
        arTrainer = ArrayList()

        TrainerData("")

        rvTrainer.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val adapterTrainer = adapterHomeTrainer(arTrainer)
        rvTrainer.adapter = adapterTrainer

        // Class
        rvClass = view.findViewById(R.id.rvHomeClass)
        arClass = ArrayList()

        ClassData()
        rvClass.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val adapterClass = adapterHomeClass(arClass)
        rvClass.adapter = adapterClass

    }
    private fun TrainerData(skill: String) {
        db.collection("Trainer")
            .whereArrayContains("skills", skill)
            .get()
            .addOnSuccessListener { result ->
                arTrainer.clear()
                for (document in result) {
                    var documentId = document.id
                    var namaTrainer = document.getString("nama")
                    val clientId = (document["clientId"] as? List<*>)?.map { it.toString() } ?: emptyList()
                    val skills = (document["skills"] as? List<*>)?.map { it.toString() } ?: emptyList()

                    val fotoName = document.getString("foto")

                    arTrainer.add(TrainerModel(documentId, fotoName, namaTrainer, ArrayList(skills), clientId.size))
                    Log.d("fetchTrainer", "Document data: ${document.data}")
                    Log.d("fetchTrainer", "Document data: ${clientId.size}")
                }

                rvTrainer.adapter?.notifyDataSetChanged()
            }.addOnFailureListener { e ->
                Log.e("fetchHomeTrainer", "Error:", e)
            }
    }

    private fun ClassData() {
        db.collection("Class").get().addOnSuccessListener { result ->
            arClass.clear()
            for (document in result) {
                val nama = document.getString("nama").toString()
                val pelatih = document.getString("pelatih").toString()
                val level = document.getString("level").toString()
//                val selectedDate = (document["tanggal"] as? Timestamp)?.toDate()?.time ?: 0
                val timestamp = document.getTimestamp("tanggal")

                arClass.add(GymClass("", nama, 0,0, pelatih.capitalize(), timestamp, level, ArrayList()))
            }
            arClass.sortBy { it.timestamp}
            rvClass.adapter?.notifyDataSetChanged()
        }.addOnFailureListener { e ->
            Log.e("haes", "Error fetching data from Firebase", e)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment fHome.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fHome().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}