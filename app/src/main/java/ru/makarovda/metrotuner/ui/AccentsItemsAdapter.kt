package ru.makarovda.metrotuner.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
}