package net.edara.edaracash.presentation.search_result;

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import net.edara.edaracash.databinding.ResultItemBinding
import net.edara.edaracash.models.WorkOrder


class ResultAdapter(

    private val itemClick: (WorkOrder) -> Unit
) :
    ListAdapter<WorkOrder, ResultAdapter.ResultViewHolder>(SearchResultDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        return ResultViewHolder.from(this, parent)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val item = getItem(position)
        holder.bindView(item) { notifyItemRangeChanged(0,itemCount) }
    }


    class ResultViewHolder private constructor(
        private val binding: ResultItemBinding,
        val itemClick: (WorkOrder) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindView(item: WorkOrder, notifyItemChanged: () -> Unit) {
            with(item) {
                binding.result=item

                itemView.setOnClickListener {
                    itemClick(this)
                    notifyItemChanged()

                }
            }
        }

        companion object {
            fun from(
                adapter: ResultAdapter,
                parent: ViewGroup
            ): ResultViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view =
                    ResultItemBinding
                        .inflate(layoutInflater, parent, false)
                return ResultViewHolder(view, adapter.itemClick)
            }
        }
    }


}

class SearchResultDiffUtil : DiffUtil.ItemCallback<WorkOrder>() {
    override fun areItemsTheSame(oldItem: WorkOrder, newItem: WorkOrder): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: WorkOrder, newItem: WorkOrder): Boolean {
        return oldItem == newItem
    }
}