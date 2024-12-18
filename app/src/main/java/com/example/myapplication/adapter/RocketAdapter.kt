package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil


import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.RocketEntity
import com.example.myapplication.databinding.ItemRocketBinding

class RocketAdapter(
    private var rocketList: List<RocketEntity>,
    private val onRocketClick: (RocketEntity) -> Unit
) : RecyclerView.Adapter<RocketAdapter.RocketViewHolder>() {

    inner class RocketViewHolder(val binding: ItemRocketBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(rocket: RocketEntity) {
            binding.tvName.text = rocket.name
            binding.root.setOnClickListener { onRocketClick(rocket) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RocketViewHolder {
        val binding = ItemRocketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RocketViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RocketViewHolder, position: Int) {
        holder.bind(rocketList[position])
    }

    override fun getItemCount(): Int = rocketList.size

    fun updateData(newList: List<RocketEntity>) {
        val diffResult = DiffUtil.calculateDiff(RocketDiffCallback(rocketList, newList))
        rocketList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    class RocketDiffCallback(
        private val oldList: List<RocketEntity>,
        private val newList: List<RocketEntity>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].name == newList[newItemPosition].name
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}