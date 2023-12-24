package uts.c14210065.proyekpaba

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class adapterHistoryPembayaran  (
    private val listTransaksi: ArrayList<Transaksi>
): RecyclerView.Adapter<adapterHistoryPembayaran.ListViewHolder>(){
    private lateinit var  onItemClickCallback: OnItemClickCallback


    interface OnItemClickCallback {
        fun  onItemClicked(data : Transaksi)
        fun delData(pos:Int)
    }
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }


    inner class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var _tvTanggalT: TextView = itemView.findViewById(R.id.tvTanggalT)
        var _tvPilihanPaket: TextView = itemView.findViewById(R.id.tvPilihanPaket)
        var _tvTotal: TextView = itemView.findViewById(R.id.tvTotal)
        var _tvMetode: TextView = itemView.findViewById(R.id.tvMetode)
        var _tvNoPembayaran: TextView = itemView.findViewById(R.id.tvNoPembayaran)
        val _bgHistoryP: ConstraintLayout = itemView.findViewById(R.id.bgHistoryP)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_history_pembayaran, parent,false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listTransaksi.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var transaksi = listTransaksi[position]

        holder._tvNoPembayaran.text = transaksi.idTransaksi
        holder._tvTanggalT.text = transaksi.tanggalBeli
        if(transaksi.idPaket != ""){
            holder._tvPilihanPaket.text = transaksi.pilihan + " (paket ${transaksi.idPaket})"
        }else{
            holder._tvPilihanPaket.text = transaksi.pilihan + " (paket ${transaksi.jenisMember})"
        }

        //beri Rp dan seperator koma
        val formattedHarga = NumberFormat.getNumberInstance(Locale("id", "ID")).format(transaksi.harga)
        holder._tvTotal.text = "Rp $formattedHarga"

        holder._tvMetode.text = transaksi.jenisPembayaran

        holder._bgHistoryP.setOnClickListener{
            Log.d("eeee", "masukk $position")
            onItemClickCallback.onItemClicked(listTransaksi[position])
        }
    }
}