package uts.c14210065.proyekpaba

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class adapterGym (
    private val listGym: ArrayList<Gym>
): RecyclerView.Adapter<adapterGym.ListViewHolder>(){

    private lateinit var  onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun  onItemClicked(data : User)
        fun delData(pos:Int)
    }
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var _tvStartGym : TextView = itemView.findViewById(R.id.tvStartGym)
        var _tvEndGym : TextView = itemView.findViewById(R.id.tvEndGym)
        var _tvSlot : TextView = itemView.findViewById(R.id.tvSlot)
        var _tvBookGym : Button = itemView.findViewById(R.id.tvBookGym)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.itemgym, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listGym.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var gym = listGym[position]

        holder._tvStartGym.setText(gym.jamStart[0].toString())
        holder._tvEndGym.setText(gym.jamStart[0].toString())
        holder._tvSlot.setText(gym.kuotaSisa[0].toString())


//        holder._btnHapus.setOnClickListener{
//            onItemClickCallback.delData(position)
//        }

    }
}