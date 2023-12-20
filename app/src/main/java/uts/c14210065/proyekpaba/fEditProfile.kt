package uts.c14210065.proyekpaba

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [fEditProfile.newInstance] factory method to
 * create an instance of this fragment.
 */
class fEditProfile : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    val db = Firebase.firestore
    lateinit var idLogin:String

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
        return inflater.inflate(R.layout.fragment_f_edit_profile, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        idLogin = arguments?.getString("userId")!!

        var _edNama = view.findViewById<EditText>(R.id.edNama)
        var _edBerat = view.findViewById<EditText>(R.id.edBerat)
        var _edTinggi = view.findViewById<EditText>(R.id.edTinggi)
        var _edPassword = view.findViewById<EditText>(R.id.edPassword)
        var _edEmail = view.findViewById<EditText>(R.id.edEmail)

        var _btnSave = view.findViewById<Button>(R.id.btnSave)

        _btnSave.setOnClickListener(){
            var updatedNama = _edNama.text.toString()
            var updatedBerat = _edBerat.text.toString()
            var updatedTinggi = _edTinggi.text.toString()
            var updatedEmail = _edEmail.text.toString()
            var updatedPassword = _edPassword.text.toString()


            if(updatedNama.isNotBlank()) {
                UpdateData("nama", updatedNama)
                _edNama.text.clear()
            }
            if(updatedBerat.isNotBlank()){
                UpdateData("berat", updatedBerat)
                _edBerat.text.clear()
            }
            if(updatedTinggi.isNotBlank()){
                UpdateData("tinggi", updatedTinggi)
                _edTinggi.text.clear()
            }
            if(updatedEmail.isNotBlank()) {
                UpdateData("email", updatedEmail)
                _edEmail.text.clear()
            }
            if(updatedPassword.isNotBlank()) {
                UpdateData("password", updatedPassword)
                _edPassword.text.clear()
            }

        }
    }

    private fun UpdateData(field:String, data:String) {
        if (field == "tinggi" || field == "berat") data.toInt()
        db.collection("users")
            .document(idLogin)
            .update(field, data)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "DocumentSnapshot successfully updated!")
                // Tambahan: Tampilkan pesan atau lakukan tindakan lain setelah berhasil update
//                Toast.makeText(this@fEditProfile, "berhasil update", Toast.LENGTH_SHORT).show)
//                Toast.makeText(this@fEditProfile, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show()
                Log.d("updated db", "db berhasil di update")

            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error updating document", e)
                // Tambahan: Tampilkan pesan atau lakukan tindakan lain jika update gagal
//                Toast.makeText(this@fEditProfile, "Gagal memperbarui data", Toast.LENGTH_SHORT).show()
            }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment fEditProfile.
         */
        const val login = "GETDATA"
        const val userId = "GETID"
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fEditProfile().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}