package ru.makarovda.metrotuner.ui.metronome

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.makarovda.metrotuner.R

class AccentsItemsAdapter(var accentsPattern: List<Boolean>,
                          private val nonAccentColor: Int,
                          private val accentColor: Int,
                          private val handler: (Int) -> Unit  ): RecyclerView.Adapter<AccentsItemsAdapter.ViewHolder>() {



    class ViewHolder(itemView: View, private val nonAccentColor: Int, private val accentColor: Int, handler: (Int)->Unit ) : RecyclerView.ViewHolder(itemView) {
        val numberText: TextView = itemView.findViewById(R.id.acc_item_number_text)
        val colorView: View = itemView.findViewById(R.id.acc_item_color_view)

        var accented = false
            set(value) {
                if(value) {
                    colorView.setBackgroundColor(accentColor)
                } else {
                    colorView.setBackgroundColor(nonAccentColor)
                }
                field = value
            }
        init {
            colorView.setOnClickListener {
                handler(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.accent_item_layout, parent, false)
        return ViewHolder(view, nonAccentColor, accentColor, handler)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.numberText.text = (position + 1).toString()
        /*if(accentsPattern[position]){
            holder.colorView.setBackgroundColor(0xFFFF0000.toInt())
        }*/
        holder.accented = accentsPattern[position]
    }

    override fun getItemCount(): Int {
        return accentsPattern.size
    }

    fun submitNewList(newList: List<Boolean>) {
        val oldList = accentsPattern
        accentsPattern = newList
        DiffUtil.calculateDiff(
            DiffUtilCallback(oldList, newList)
        ).dispatchUpdatesTo(this)
    }

    class DiffUtilCallback(
        val oldList: List<Boolean>,
        val newList: List<Boolean>
    ): DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(p0: Int, p1: Int): Boolean {
            return ((p0 == p1) && (oldList[p0] == newList[p1]))
        }

        override fun areContentsTheSame(p0: Int, p1: Int): Boolean {
            return ((p0 == p1) && (oldList[p0] == newList[p1]))
        }

    }
}