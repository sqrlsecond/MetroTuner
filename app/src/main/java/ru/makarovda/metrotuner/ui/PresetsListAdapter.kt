package ru.makarovda.metrotuner.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.makarovda.metrotuner.R
import ru.makarovda.metrotuner.data.MetronomeSettingsEntity


class PresetsListAdapter(private val clickListener: (MetronomeSettingsEntity, Actions) -> Unit):
    ListAdapter<MetronomeSettingsEntity, PresetsListAdapter.ViewHolder>(DiffCallback) {


    class ViewHolder(itemView: View, clickAtPosition: (Int, Actions)-> Unit) : RecyclerView.ViewHolder(itemView){

        private val nameText:TextView = itemView.findViewById(R.id.metronome_preset_name_text)
        private val beatsText:TextView = itemView.findViewById(R.id.metronome_preset_beats_text)

        init {
            nameText.setOnClickListener {
                clickAtPosition(adapterPosition, Actions.CHOOSE)
            }
            itemView.findViewById<ImageView>(R.id.delete_item_icon).setOnClickListener {
                clickAtPosition(adapterPosition, Actions.DELETE)
            }
        }
        fun bind(settingsEntity: MetronomeSettingsEntity){
            nameText.text = settingsEntity.name
            beatsText.text = itemView.context.getString(
                R.string.metronome_preset,
                                                        settingsEntity.bpm,
                                                        settingsEntity.beats,
                                                        settingsEntity.accent)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
                .inflate(   R.layout.metronome_preset_item,
                            parent,
                            false)
        ) { position: Int,
            action: Actions ->
            clickListener(currentList[position], action)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
       private val DiffCallback = object : DiffUtil.ItemCallback<MetronomeSettingsEntity>() {
           override fun areItemsTheSame(
               oldItem: MetronomeSettingsEntity,
               newItem: MetronomeSettingsEntity
           ): Boolean {
               return newItem.name == oldItem.name
           }

           override fun areContentsTheSame(
               oldItem: MetronomeSettingsEntity,
               newItem: MetronomeSettingsEntity
           ): Boolean {
               return newItem.name == oldItem.name
           }

       }

    }

    enum class Actions{
        CHOOSE,
        DELETE,
    }
}