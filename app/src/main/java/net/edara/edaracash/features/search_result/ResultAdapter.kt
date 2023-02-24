package net.edara.edaracash.features.search_result;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import net.edara.domain.models.getAllService.GetAllServiceResonse.Data.Service
import net.edara.edaracash.databinding.ResultItemBinding
import net.edara.edaracash.features.util.getColorFromAttr


class ResultAdapter(

    private val itemClick: (Service) -> Unit, private val onItemSelect: (Service) -> Unit
) : ListAdapter<Service, ResultAdapter.ResultViewHolder>(SearchResultDiffUtil()) {

    var selectedItems: MutableList<Service> = mutableListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        return ResultViewHolder.from(this, parent)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val item = getItem(position)
        holder.bindView(item)
    }

    fun toggleSelectAll(isAllSelected: Boolean) {
        currentList.forEach {
            it.isSelectedForPayment = isAllSelected
            onItemSelect(it)
        }
        notifyItemRangeChanged(0, itemCount)
    }


    class ResultViewHolder private constructor(
        private val binding: ResultItemBinding,
        val itemClick: (Service) -> Unit,
        val onItemSelect: (Service) -> Unit,
        private val selectedItems: MutableList<Service>
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(item: Service) {
            with(item) {
                binding.result = this
                itemView.setOnClickListener {
                    if (selectedItems.isNotEmpty()) {
                        isSelectedForPayment = !isSelectedForPayment
                        handleCheckStateChanges(this)
                    }
                    itemClick(this)
                }
                itemView.setOnLongClickListener {
                    isSelectedForPayment = !isSelectedForPayment
                    handleCheckStateChanges(this)
                    onItemSelect(this)
                    return@setOnLongClickListener true
                }
                handleCheckStateChanges(this)
            }
        }

        private fun handleCheckStateChanges(item: Service) {
            if (item.isSelectedForPayment) {
                selectedItems.add(item)
                binding.selected.visibility = View.VISIBLE
                binding.edit.visibility = View.GONE
                binding.card.setCardBackgroundColor(
                    itemView.context.getColorFromAttr(com.google.android.material.R.attr.colorPrimaryContainer)
                )
            } else {
                selectedItems.remove(item)
                binding.selected.visibility = View.GONE
                binding.edit.visibility = View.VISIBLE
                binding.card.setCardBackgroundColor(
                    itemView.context.getColorFromAttr(com.google.android.material.R.attr.colorOnPrimary)

                )
            }
            binding.selected.isChecked = item.isSelectedForPayment

        }

        companion object {
            fun from(
                adapter: ResultAdapter, parent: ViewGroup
            ): ResultViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = ResultItemBinding.inflate(layoutInflater, parent, false)
                return ResultViewHolder(
                    view, adapter.itemClick, adapter.onItemSelect, adapter.selectedItems
                )
            }
        }
    }


}

class SearchResultDiffUtil : DiffUtil.ItemCallback<Service>() {
    override fun areItemsTheSame(oldItem: Service, newItem: Service): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Service, newItem: Service): Boolean {
        return oldItem == newItem
    }
}