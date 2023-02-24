package net.edara.edaracash.features.invoice;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import net.edara.domain.models.getAllService.GetAllServiceResonse
import net.edara.edaracash.databinding.ServiceItemBinding


class ServiceAdapter(

) :
    ListAdapter<GetAllServiceResonse.Data.Service, ServiceAdapter.ServiceHolder>(
        ServiceDiffUtil()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceHolder {
        return ServiceHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ServiceHolder, position: Int) {
        val item = getItem(position)
        holder.bindView(item)
    }


    class ServiceHolder private constructor(
        private val binding: ServiceItemBinding,

        ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindView(item: GetAllServiceResonse.Data.Service) {
            with(item) {
                binding.serviceName.text = serviceName
                binding.servicePrice.text = amount
            }
        }

        companion object {
            fun from(
                parent: ViewGroup
            ): ServiceHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view =
                    ServiceItemBinding
                        .inflate(layoutInflater, parent, false)
                return ServiceHolder(view)
            }
        }
    }


}

class ServiceDiffUtil : DiffUtil.ItemCallback<GetAllServiceResonse.Data.Service>() {
    override fun areItemsTheSame(
        oldItem: GetAllServiceResonse.Data.Service,
        newItem: GetAllServiceResonse.Data.Service
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: GetAllServiceResonse.Data.Service,
        newItem: GetAllServiceResonse.Data.Service
    ): Boolean {
        return oldItem == newItem
    }
}