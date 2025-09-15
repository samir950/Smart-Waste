package com.qbrain.smartwaste.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.qbrain.smartwaste.R
import com.qbrain.smartwaste.databinding.ItemNearbyBinBinding
import com.qbrain.smartwaste.models.NearbyBin
import com.qbrain.smartwaste.utils.Constants

class NearbyBinsAdapter(
    private val bins: List<NearbyBin>,
    private val onBinClick: (NearbyBin) -> Unit
) : RecyclerView.Adapter<NearbyBinsAdapter.BinViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BinViewHolder {
        val binding = ItemNearbyBinBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BinViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BinViewHolder, position: Int) {
        holder.bind(bins[position])
    }

    override fun getItemCount(): Int = bins.size

    inner class BinViewHolder(private val binding: ItemNearbyBinBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(bin: NearbyBin) {
            binding.apply {
                tvBinId.text = "Bin ${bin.binId}"
                tvDistance.text = "${bin.distance}m away"
                tvFillPercentage.text = "${bin.fillPercentage}%"
                tvBinType.text = bin.type.capitalize()
                
                // Set status color and text
                when (bin.status) {
                    Constants.BIN_STATUS_FULL -> {
                        tvStatus.text = "Full"
                        tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.error_red))
                        progressFill.setIndicatorColor(ContextCompat.getColor(root.context, R.color.error_red))
                    }
                    Constants.BIN_STATUS_NEEDS_COLLECTION -> {
                        tvStatus.text = "Needs Collection"
                        tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.warning_orange))
                        progressFill.setIndicatorColor(ContextCompat.getColor(root.context, R.color.warning_orange))
                    }
                    Constants.BIN_STATUS_NORMAL -> {
                        tvStatus.text = "Normal"
                        tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.success_green))
                        progressFill.setIndicatorColor(ContextCompat.getColor(root.context, R.color.success_green))
                    }
                    else -> {
                        tvStatus.text = "Empty"
                        tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.primary_blue))
                        progressFill.setIndicatorColor(ContextCompat.getColor(root.context, R.color.primary_blue))
                    }
                }
                
                progressFill.progress = bin.fillPercentage
                
                // Set bin type icon
                when (bin.type.lowercase()) {
                    "organic" -> ivBinType.setImageResource(R.drawable.ic_organic)
                    "recyclable" -> ivBinType.setImageResource(R.drawable.ic_recycle)
                    else -> ivBinType.setImageResource(R.drawable.ic_delete)
                }
                
                root.setOnClickListener { onBinClick(bin) }
            }
        }
    }
}