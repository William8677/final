package com.williamfq.xhat.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.williamfq.xhat.databinding.ItemCommunityBinding
import com.williamfq.domain.model.Community

class CommunityAdapter(
    private val onSubscribeClick: (Community) -> Unit,
    private val onSettingsClick: (Community) -> Unit
) : ListAdapter<Community, CommunityAdapter.CommunityViewHolder>(CommunityDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityViewHolder {
        val binding = ItemCommunityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommunityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommunityViewHolder, position: Int) {
        val community = getItem(position)
        holder.bind(community)
    }

    inner class CommunityViewHolder(private val binding: ItemCommunityBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(community: Community) {
            binding.apply {
                tvCommunityName.text = community.name
                tvCommunityDescription.text = community.description
                tvMemberCount.text = "${community.memberCount} miembros"
                btnSettings.setOnClickListener {
                    onSettingsClick(community)
                }

                btnSubscribe.apply {
                    text = if (community.isSubscribed) "Suscrito" else "Suscribirse"
                    isEnabled = !community.isSubscribed
                    backgroundTintList = ColorStateList.valueOf(
                        if (community.isSubscribed) Color.GRAY else Color.parseColor("#6200EE")
                    )
                    setOnClickListener {
                        if (!community.isSubscribed) {
                            onSubscribeClick(community)
                        }
                    }
                }
            }
        }
    }

    class CommunityDiffCallback : DiffUtil.ItemCallback<Community>() {
        override fun areItemsTheSame(oldItem: Community, newItem: Community): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Community, newItem: Community): Boolean {
            return oldItem == newItem
        }
    }
}
