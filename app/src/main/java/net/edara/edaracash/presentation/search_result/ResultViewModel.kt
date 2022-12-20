package net.edara.edaracash.presentation.search_result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.edara.edaracash.models.WorkOrder
import kotlin.random.Random

class ResultViewModel : ViewModel() {
    private val _resultOrder = MutableLiveData<WorkOrder>(
    )
    private var oldWorkOrder: WorkOrder? = null

    private var random = Random.nextInt(1, 4)

    init {
        generateResult()
    }

    private fun generateResult() {
        _resultOrder.value = when (random) {
            1 ->
                WorkOrder(
                    id = "1",
                    title = "Car Wash",
                    clientName = "dummy client name",
                    unitNo = 123,
                    analysisCode = 524,
                    spareParts = 0.0,
                    laborCost = 0.0,
                )
            2 ->

                WorkOrder(
                    id = "2",
                    title = "house keeping",
                    clientName = "dummy client name",
                    unitNo = 123,
                    analysisCode = 522,
                    spareParts = 0.0,
                    laborCost = 0.0,
                )
            else ->
                WorkOrder(
                    id = "3",
                    title = "electricity fixing",
                    clientName = "dummy client name",
                    unitNo = 123,
                    analysisCode = 521,
                    spareParts = 0.0,
                    laborCost = 0.0,
                )
        }
        saveChanges()
    }

    fun cancelChanges() {
        _resultOrder.value = oldWorkOrder!!
    }

    fun saveChanges() {
        oldWorkOrder = _resultOrder.value!!
    }

    val resultOrder: LiveData<WorkOrder>
        get() = _resultOrder


    fun changeLaborCost(cost: Double) {
        val result = _resultOrder.value!!
        if (result.laborCost == cost) return

        _resultOrder.value =
            result
                .copy(
                    total =
                    result.total
                            - result.laborCost
                            + cost
                )
                .copy(laborCost = cost)

    }

    fun changeSparParts(cost: Double) {
        val result = _resultOrder.value!!
        if (result.spareParts == cost) return

        _resultOrder.value =
            result.copy(
                total =
                result.total
                        - result.spareParts
                        + cost
            ).copy(spareParts = cost)

    }

    fun changeExtraCharge(cost: Double) {
        val result = _resultOrder.value!!
        if (result.extraCharge == cost) return

        _resultOrder.value =
            result.copy(
                total =
                result.total
                        - result.extraCharge
                        + cost
            ).copy(extraCharge = cost)

    }

    fun changeTax(cost: Double) {

        val result = _resultOrder.value!!
        if (result.tax == cost) return

        _resultOrder.value =
            result.copy(
                total =
                result.total
                        - result.tax
                        + cost
            ).copy(tax = cost)

    }

    fun changeDiscount(cost: Double) {
        val result = _resultOrder.value!!
        if (result.discount == cost) return
        _resultOrder.value =
            result.copy(
                total =
                result.total
                        + result.discount
                        - cost
            ).copy(discount = cost)

    }
}