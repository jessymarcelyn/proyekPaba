package uts.c14210065.proyekpaba

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
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

    lateinit var _edNama : EditText
    lateinit var _edBerat : EditText
    lateinit var _edTinggi : EditText
    lateinit var _edPassword : EditText
    lateinit var _edEmail : EditText
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        idLogin = arguments?.getString("userId")!!

        _edNama = view.findViewById<EditText>(R.id.edNama)
        _edBerat = view.findViewById<EditText>(R.id.edBerat)
        _edTinggi = view.findViewById<EditText>(R.id.edTinggi)
        _edPassword = view.findViewById<EditText>(R.id.edPassword)
        _edEmail = view.findViewById<EditText>(R.id.edEmail)

        ReadData(idLogin)

        var _btnSave = view.findViewById<Button>(R.id.btnSave)

        _btnSave.setOnClickListener(){
            var updatedNama = _edNama.text.toString()
            var updatedBerat = _edBerat.text.toString()
            var updatedTinggi = _edTinggi.text.toString()
            var updatedEmail = _edEmail.text.toString()
            var updatedPassword = _edPassword.text.toString()


            if(updatedNama.isNotBlank()) {
                UpdateData("nama", updatedNama, idLogin)
                _edNama.text.clear()
            }
            if(updatedBerat.isNotBlank()){
                UpdateData("berat", updatedBerat, idLogin)
                _edBerat.text.clear()
            }
            if(updatedTinggi.isNotBlank()){
                UpdateData("tinggi", updatedTinggi, idLogin)
                _edTinggi.text.clear()
            }
            if(updatedEmail.isNotBlank()) {
                UpdateData("email", updatedEmail, idLogin)
                _edEmail.text.clear()
            }
            if(updatedPassword.isNotBlank()) {
                UpdateData("password", updatedPassword, idLogin)
                _edPassword.text.clear()
            }

        }
    }

    private fun UpdateData(field:String, data:String, userId: String) {
        if (field == "tinggi" || field == "berat") data.toInt()
        db.collection("users")
            .document(userId)
            .update(field, data)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "DocumentSnapshot successfully updated!")
                // Tambahan: Tampilkan pesan atau lakukan tindakan lain setelah berhasil update
                showAlert(requireContext(), "Update Profile Berhasil", "Perubahan data telah berhasil dilakukan")
                Log.d("updated db", "db berhasil di update")
//                ReadData(userId)
//                (activity as utama).goToPage(fProfile())

            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error updating document", e)
                // Tambahan: Tampilkan pesan atau lakukan tindakan lain jika update gagal
//                Toast.makeText(this@fEditProfile, "Gagal memperbarui data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun ReadData(userId : String) {
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener {document ->
                Log.d("read db", "db berhasil di update")
                _edNama.hint = document.get("nama").toString()
                _edBerat.hint = document.get("berat").toString() + "kg"
                _edTinggi.hint = document.get("tinggi").toString() + "cm"
                _edEmail.hint = document.get("email").toString()
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error updating document", e)
                // Tambahan: Tampilkan pesan atau lakukan tindakan lain jika update gagal
//                Toast.makeText(this@fEditProfile, "Gagal memperbarui data", Toast.LENGTH_SHORT).show()
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