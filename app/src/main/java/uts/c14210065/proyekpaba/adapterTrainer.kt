package uts.c14210065.proyekpaba.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uts.c14210065.proyekpaba.R
import uts.c14210065.proyekpaba.model.TrainerModel
import kotlin.math.log

class adapterTrainer (private val listTrainer: ArrayList<TrainerModel>) : RecyclerView.Adapter<adapterTrainer.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback
    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var _namaTrainer : TextView = itemView.findViewById(R.id.tvNamaTrainer)
        var _skills : TextView = itemView.findViewById(R.id.tvSkills)
        var _client : TextView = itemView.findViewById(R.id.tvClients)
        var _btnBook : Button = itemView.findViewById(R.id.btnBookTrainer)
    }

    interface OnItemClickCallback {
        fun onItemClicked(data : TrainerModel)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.list_trainer, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listTrainer.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var trainers = listTrainer[position]
        holder._namaTrainer.setText(trainers.nama)
        val skillsString = trainers.skills?.joinToString(", ") { it.capitalize() } ?: ""
        holder._skills.text = "Skills: $skillsString"
        holder._client.text = "Active clients: " + trainers.client?.toString()
    }
}

