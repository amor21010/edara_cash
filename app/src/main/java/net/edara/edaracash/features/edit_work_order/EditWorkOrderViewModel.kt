package net.edara.edaracash.features.edit_work_order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.edara.domain.models.getAllService.GetAllServiceResonse.Data.*

class EditWorkOrderViewModel : ViewModel() {
    private val _resultOrder = MutableLiveData<Service>()
    val resultOrder: LiveData<Service>
        get() = _resultOrder

    fun setOrder(workOrder: Service) {
        _resultOrder.value = workOrder
    }






}