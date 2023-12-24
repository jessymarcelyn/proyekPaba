package uts.c14210065.proyekpaba.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import uts.c14210065.proyekpaba.R
import uts.c14210065.proyekpaba.model.TrainerModel

class adapterHomeTrainer (private val listTrainer: ArrayList<TrainerModel>) : RecyclerView.Adapter<adapterHomeTrainer.ListViewHolder>() {
    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var _fotoTrainer : ImageView = itemView.findViewById(R.id.ivTrainerHome)
        var _namaTrainer : TextView = itemView.findViewById(R.id.tvNamaTrainerHome)
        var _skills : TextView = itemView.findViewById(R.id.tvSkillsHome)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.list_home_trainer, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listTrainer.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var trainers = listTrainer[position]

        val imageResId = holder.itemView.context.resources.getIdentifier(
            trainers.foto, "drawable", holder.itemView.context.packageName
        )

        Picasso.get()
            .load(imageResId)
            .into(holder._fotoTrainer)

        holder._namaTrainer.setText(trainers.nama)
        val skillsString = trainers.skills?.joinToString(", ") { it.capitalize() }?.dropLast(2) ?: ""
        holder._skills.text = "Skills: $skillsString"
    }
}

