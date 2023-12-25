package uts.c14210065.proyekpaba

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore.Audio.Radio
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.log

class Pembayaran : AppCompatActivity() {

    lateinit var totalHarga: TextView

    lateinit var bca: RadioButton
    lateinit var mandiri: RadioButton
    lateinit var cd: RadioButton
    lateinit var ovo: RadioButton
    lateinit var gopay: RadioButton
//    lateinit var btnBack: ImageView
    lateinit var btnBayar: Button
    lateinit var tvRincian: TextView
    lateinit var tvJenisMember: TextView

    var pembayaranMember: Int = 0
    var durasiMember: Int = 0
    var formatRupiah: String = ""
    lateinit var loginId: String
    lateinit var jenisMember: String
    var paket: Int = 0
    lateinit var trainerId: String

    var pembayaran: String = ""

    val db = Firebase.firestore

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pembayaran)

//        btnBack = findViewById(R.id.btnPembayaranBack)
        tvRincian = findViewById(R.id.tvRincian)
        tvJenisMember = findViewById(R.id.tvJenisRincian)

        loginId = intent.getStringExtra("userId") ?: "0"
        jenisMember = intent.getStringExtra("jenisMember") ?: ""
        durasiMember = intent.getIntExtra("durasiMember", 0)
        paket = intent.getIntExtra("paket", 0)
        trainerId = intent.getStringExtra("trainerId")?: ""

        Log.d("pembayaran", "paket: $paket")


//        btnBack.setOnClickListener {
//            val intent = Intent(this, Membership::class.java)
//            intent.putExtra("navigateToFragment", "fJoin")
//            intent.putExtra("userId", loginId)
//            startActivity(intent)
//            finish()
//        }

        if (loginId != "0") {
            totalHarga = findViewById(R.id.tvTotalPembayaran)
            bca = findViewById(R.id.bca)
            mandiri = findViewById(R.id.mandiri)
            cd = findViewById(R.id.creditdebit)
            ovo = findViewById(R.id.ovo)
            gopay = findViewById(R.id.gopay)
            btnBayar = findViewById(R.id.btnBayar)

            bca.setOnClickListener {
                pembayaran = "bca"
            }
            mandiri.setOnClickListener {
                pembayaran = "mandiri"
            }
            cd.setOnClickListener {
                pembayaran = "credit/debit"
            }
            ovo.setOnClickListener {
                pembayaran = "ovo"
            }
            gopay.setOnClickListener {
                pembayaran = "gopay"
            }

            if (jenisMember == "bronze" || jenisMember == "silver" || jenisMember == "gold" || jenisMember == "diamond") {
                pembayaranMember = intent.getIntExtra("pembayaranMember", 0)
                formatRupiah =
                    NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(pembayaranMember)

                totalHarga.text = formatRupiah

                tvRincian.text = "Jenis Member: "
                tvJenisMember.text = jenisMember.capitalize()

                Log.d("pembayaran", "loginId: $loginId")

                btnBayar.setOnClickListener {
                    if (pembayaran == "") {
                        Toast.makeText(this, "Harap memilih metode pembayaran", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        PembelianMember()
                    }

                }
            }

            if (paket == 1 || paket == 2 || paket == 3 || paket == 4 || paket == 5 || paket == 6) {
                tvRincian.text = "Pilihan Paket: "

                db.collection("PilihanPaket").document(paket.toString()).get().addOnSuccessListener { result ->
                    if (result.exists()) {
                        val harga = result.getLong("harga")?.toInt()
                        val totalSesi = result.getLong("totalSesi")?.toInt()
                        tvJenisMember.text = "$totalSesi Sesi"
                        formatRupiah =
                            NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(harga)
                        totalHarga.text = formatRupiah
                    }
                }

                btnBayar.setOnClickListener {
                    if (pembayaran == "") {
                        Toast.makeText(this, "Harap memilih metode pembayaran", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        db.collection("users").document(loginId).get()
                            .addOnSuccessListener { documentSnapshot ->
                                if (documentSnapshot.exists()) {
                                    val isMember = documentSnapshot.getBoolean("member")
                                    if (isMember == true) {
                                        PembelianPaket()
                                    } else {
                                        Toast.makeText(
                                            this,
                                            "Daftar menjadi member dahulu",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        val intent = Intent(this, Membership::class.java)
                                        intent.putExtra("userId", loginId)
                                        startActivity(intent)
                                        finish()

                                    }
                                }
                            }
                    }

                }

            }
        }
        else {
            Toast.makeText(this, "Register/Login terlebih dahulu", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
    }

    fun PembelianMember() {
        // ambil data dari db PilihanMember, berdasarkan dokumen
        db.collection("PilihanMember").document(jenisMember).get()
            .addOnSuccessListener { doc ->
                var harga = doc.getLong("harga")?.toInt()
                var jenis = doc.getString("jenis").toString()
                var durasi = doc.getLong("durasi")?.toInt()

                Log.d("pembayarn", "durasi member: $durasi")

                // pengecekan user sudah member atau belum
                db.collection("UserMember").get().addOnSuccessListener { result ->
                    var docId: String? = null
                    for (document in result) {
                        var idUser = document.getString("idUser")

                        if (idUser == loginId) {
                            docId = document.id
                            break
                        }
                    }
                    // akan update
                    if (docId != null) {
                        val updateData = hashMapOf<String, Any>(
                            "durasi" to FieldValue.increment(durasi?.toLong() ?: 0),
                            "harga" to FieldValue.increment(harga?.toLong() ?: 0)
                        )

                        db.collection("UserMember").document(docId).update(updateData)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    "Member telah diperpanjang",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener { er ->
                                Log.d("pembayaran member", "Error: $er")
                            }
                    } else {
                        // jika tidak pernah booking, maka dibuat document baru
                        val newData = hashMapOf(
                            "idUser" to loginId,
                            "jenisMember" to jenis,
                            "jenisPembayaran" to pembayaran,
                            "durasi" to durasi,
                            "harga" to harga,
                            "tanggalMulai" to FieldValue.serverTimestamp()
                        )

                        db.collection("UserMember").document().set(newData)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    "Selamat! Anda menjadi Member",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener { er ->
                                Log.d("pembayaran member", "Error: $er")
                            }

                        // update user jadi member
                        db.collection("users").document(loginId)
                            .update("member", true)
                            .addOnSuccessListener {
                                Log.d("pembayaran member", "member di user updated")
                            }
                            .addOnFailureListener { er ->
                                Log.d("pembayaran member", "Error: $er")
                            }
                    }
                }
            }
        TambahTransaksi("member")
    }


    fun PembelianPaket() {
        // ambil data dari db PilihanPaket, berdasarkan dokumen
        db.collection("PilihanPaket").document(paket.toString()).get()
            .addOnSuccessListener { doc ->
                var harga = doc.getLong("harga")?.toInt()
                var totalSesi = doc.getLong("totalSesi")?.toInt()
                var durasi = doc.getLong("durasi")?.toInt()

                // pengecekan user pernah atau tidak melakukan booking
                db.collection("UserTrainer").get().addOnSuccessListener { result ->
                    var docId: String? = null
                    for (document in result) {

                        var idUser = document.getString("idUser")
                        var idTrainer = document.getString("idTrainer")

                        if (idUser == loginId && idTrainer == trainerId) {
                            docId = document.id
                            break
                        }
                    }
                    // akan update durasi, harga, dan totalSesi jika user pernah melakukan booking trainer
                    if (docId != null) {
                        val updateData = hashMapOf<String, Any>(
                            "totalSesi" to FieldValue.increment(totalSesi?.toLong() ?: 0),
                            "sisaSesi" to FieldValue.increment(totalSesi?.toLong() ?: 0),
                            "durasi" to FieldValue.increment(durasi?.toLong() ?: 0),
                            "harga" to FieldValue.increment(harga?.toLong() ?: 0)
                        )

                        db.collection("UserTrainer").document(docId).update(updateData)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    "Booking telah terupdate",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener { er ->
                                Log.d("paketTrainer", "Error: $er")
                            }
                    } else {
                        // jika tidak pernah booking, maka dibuat document baru
                        val newData = hashMapOf(
                            "idUser" to loginId,
                            "idTrainer" to trainerId,
                            "idPaket" to paket,
                            "totalSesi" to totalSesi,
                            "sisaSesi" to totalSesi,
                            "durasi" to durasi,
                            "harga" to harga,
                            "tanggalMulai" to FieldValue.serverTimestamp()
                        )

                        UpdateClient(loginId)

                        db.collection("UserTrainer").document().set(newData)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    "Selamat! Booking berhasil",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener { er ->
                                Log.d("paketTrainer", "Error: $er")
                            }
                    }
                }
            }
        TambahTransaksi("trainer")
    }

    fun UpdateClient(userId: String) {
        db.collection("Trainer").document(trainerId).get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val currentArray = documentSnapshot.get("clientId") as? List<String> ?: emptyList()

                val newArray = currentArray.toMutableList()
                newArray.add(userId)

                db.collection("Trainer").document(trainerId).update("clientId", newArray)
                    .addOnSuccessListener {
                        Log.d("paketTrainer", "updateClient berhasil")
                    }
                    .addOnFailureListener { e ->
                        Log.e("paketTambahTransaksi(\"trainer\")Trainer", "Error:", e)
                    }
            } else {
                Log.d("pembayaran", "no document")
            }
        }
    }

    fun TambahTransaksi(pilihan: String) {
        if (pilihan == "trainer") {
            db.collection("PilihanPaket").document(paket.toString()).get()
                .addOnSuccessListener { doc ->
                    var harga = doc.getLong("harga")?.toInt()
                    var totalSesi = doc.getLong("totalSesi")?.toInt()
                    var durasi = doc.getLong("durasi")?.toInt()
                    val newData = hashMapOf(
                        "idUser" to loginId,
                        "idTrainer" to trainerId,
                        "idPaket" to paket,
                        "totalSesi" to totalSesi,
                        "durasi" to durasi,
                        "harga" to harga,
                        "jenisPembayaran" to pembayaran,
                        "pilihan" to pilihan,
                        "tanggalBeli" to FieldValue.serverTimestamp()
                    )

                    Log.d("pembayaran", "new data: ${newData}")

                    db.collection("Transaksi").document().set(newData)
                        .addOnSuccessListener {
                            Log.d("pembayaran", "transaksi berhasil")
                            val intent = Intent(this, activityPaketTrainer::class.java)
                            intent.putExtra("userId", loginId)
                            intent.putExtra("documentId", trainerId)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { er ->
                            Log.d("pembayaran", "Error: $er")
                        }
                }
        } else if (pilihan == "member") {
            db.collection("PilihanMember").document(jenisMember).get()
                .addOnSuccessListener { doc ->
                    var durasi = doc.getLong("durasi")
                    var harga = doc.getLong("harga")

                    val newData = hashMapOf(
                        "idUser" to loginId,
                        "jenisMember" to jenisMember,
                        "durasi" to durasi,
                        "harga" to harga,
                        "jenisPembayaran" to pembayaran,
                        "pilihan" to pilihan,
                        "tanggalBeli" to FieldValue.serverTimestamp()
                    )

                    db.collection("Transaksi").document().set(newData)
                        .addOnSuccessListener {
                            Log.d("pembayaran", "transaksi berhasil")
                            val intent = Intent(this, Membership::class.java)
                            intent.putExtra("userId", loginId)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { er ->
                            Log.d("pembayaran", "Error: $er")
                        }
                }
        }
    }
}