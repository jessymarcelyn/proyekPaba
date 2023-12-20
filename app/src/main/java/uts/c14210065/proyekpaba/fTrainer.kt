package uts.c14210065.proyekpaba

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import uts.c14210065.proyekpaba.adapter.adapterTrainer
import uts.c14210065.proyekpaba.model.TrainerModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [fTrainer.newInstance] factory method to
 * create an instance of this fragment.
 */
class fTrainer : Fragment() {
    private lateinit var rvTrainer: RecyclerView
    private lateinit var arTrainer: ArrayList<TrainerModel>

    private lateinit var btnAll: Button
    private lateinit var btnSpecial: Button
    private lateinit var btnStrong: Button
    private lateinit var btnAthletic: Button
    private lateinit var btnShape: Button
    private lateinit var btnWellness: Button

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
        return inflater.inflate(R.layout.fragment_f_trainer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnAll = view.findViewById(R.id.btnAll)
        btnSpecial = view.findViewById(R.id.btnSpecial)
        btnStrong = view.findViewById(R.id.btnStrong)
        btnAthletic = view.findViewById(R.id.btnAthletic)
        btnShape = view.findViewById(R.id.btnShape)
        btnWellness = view.findViewById(R.id.btnWellness)

        rvTrainer = view.findViewById(R.id.rvTrainer)
        arTrainer = ArrayList()

        btnAll.setOnClickListener {
            fetchDataFromFirestore("")
        }
        btnSpecial.setOnClickListener {
            fetchDataFromFirestore("special")
        }
        btnStrong.setOnClickListener {
            fetchDataFromFirestore("strong")
        }
        btnAthletic.setOnClickListener {
            fetchDataFromFirestore("athletic")
        }
        btnShape.setOnClickListener {
            fetchDataFromFirestore("shape")
        }
        btnWellness.setOnClickListener {
            fetchDataFromFirestore("wellness")
        }

        fetchDataFromFirestore("")

        rvTrainer.layoutManager = LinearLayoutManager(requireContext())
        val adapter = adapterTrainer(arTrainer)
        rvTrainer.adapter = adapter

    }

    private fun fetchDataFromFirestore(skill: String) {
        db.collection("Trainer")
            .whereArrayContains("skills", skill)
            .get()
            .addOnSuccessListener { result ->
            arTrainer.clear()
            for (document in result) {
                var namaTrainer = document.getString("nama")
                val clientId = (document["clientId"] as? List<*>)?.map { it.toString() } ?: emptyList()
                val skills = (document["skills"] as? List<*>)?.map { it.toString() } ?: emptyList()

                val fotoName = document.getString("foto")

                arTrainer.add(TrainerModel(fotoName, namaTrainer, ArrayList(skills), clientId.size))
                Log.d("fetchTrainer", "Document data: ${document.data}")
                Log.d("fetchTrainer", "Document data: ${clientId.size}")
            }

            rvTrainer.adapter?.notifyDataSetChanged()
        }.addOnFailureListener { e ->
            Log.e("fetchTrainer", "Error fetching data from Firebase", e)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment f_trainer.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fTrainer().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}