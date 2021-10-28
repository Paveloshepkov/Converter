package com.example.converter.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.converter.database.DatabaseValute
import com.example.converter.database.ValuteDatabase.Companion.getDatabase
import com.example.converter.domain.Valute
import com.example.converter.repository.ValuteRepository
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ConverterViewModel(application: Application) : AndroidViewModel(application) {

    private val rubDatabase: DatabaseValute = DatabaseValute(
        "643",
        "RUB",
        "1",
        "Российский рубль",
        "1"
    )

    private val rub: Valute = Valute(
        "643",
        "RUB",
        "1",
        "Российский рубль",
        "1"
    )

    var dateInString: String = ""
    var rightNumber: Float = 0f
    var leftNumber: Float = 0f

    private var _leftValute = MutableLiveData(rub)
    val leftValute: LiveData<Valute> = _leftValute

    private var _rightValute = MutableLiveData(rub)
    val rightValute: LiveData<Valute> = _rightValute

    private val valuteRepository = ValuteRepository(getDatabase(application))
    val valuteList = valuteRepository.valute

    private var _eventNetworkError = MutableLiveData(false)
    val eventNetworkError: LiveData<Boolean>
        get() = _eventNetworkError

    private var _isNetworkErrorShown = MutableLiveData(false)
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown

    init {
        refreshDataFromRepository()
    }

    fun refreshDataFromRepository() {
        viewModelScope.launch {
            try {
                valuteRepository.refreshValute()
                valuteRepository.insertValute(rubDatabase)
                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false
                getCurrentDate()

            } catch (networkError: IOException) {
                if (valuteList.value.isNullOrEmpty())
                    _eventNetworkError.value = true
            }
        }
    }

    private fun getCurrentDate() {
        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat.getDateTimeInstance()
        dateInString = formatter.format(date)
    }

    fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }

    fun getLeftValute(valute: Valute) {
        _leftValute.value = valute
    }

    fun getRightValute(valute: Valute) {
        _rightValute.value = valute
    }

    fun setLeftNumber(string: String) {
        if (string != "") {
            leftNumber = string.toFloat()
            calculateRightNumber()
        }
        if (string == "") {
            rightNumber = 0f
        }
    }

    fun setRightNumber(string: String) {
        if (string != "") {
            rightNumber = string.toFloat()
            calculateLeftNumber()
        }
        if (string == "") {
            leftNumber = 0f
        }
    }

    private fun calculateLeftNumber() {

        val leftNominal = _leftValute.value?.nominal?.toInt()
        val rightNominal = _rightValute.value?.nominal?.toInt()
        val leftValue = _leftValute.value?.value?.replace(",", ".")?.toFloat()
        val rightValue = _rightValute.value?.value?.replace(",", ".")?.toFloat()

        if (leftValue!! < rightValue!!) {
            leftNumber = rightNumber * (rightValue / leftValue)
        }

        if (leftValue > rightValue) {
            leftNumber = rightNumber * (rightValue / leftValue)
        }

        if (leftNominal!! > rightNominal!!) {
            leftNumber = rightNumber * (rightValue / (leftValue / (leftNominal / rightNominal)))
        }

        if (leftNominal < rightNominal) {
            leftNumber = rightNumber * (rightValue / leftValue / (rightNominal / leftNominal))
        }

        if (leftValue == rightValue) {
            leftNumber = rightNumber
        }
    }

    private fun calculateRightNumber() {

        val leftNominal = _leftValute.value?.nominal?.toInt()
        val rightNominal = _rightValute.value?.nominal?.toInt()
        val leftValue = _leftValute.value?.value?.replace(",", ".")?.toFloat()
        val rightValue = _rightValute.value?.value?.replace(",", ".")?.toFloat()

        if (leftValue!! < rightValue!!) {
            rightNumber = leftNumber * (leftValue / rightValue)
        }

        if (leftValue > rightValue) {
            rightNumber = leftNumber * (leftValue / rightValue)
        }

        if (rightNominal!! > leftNominal!!) {
            rightNumber = leftNumber * (leftValue / (rightValue / (rightNominal / leftNominal)))
        }

        if (rightNominal < leftNominal) {
            rightNumber = leftNumber * (leftValue / rightValue / (leftNominal / rightNominal))
        }

        if (rightValue == leftValue) {
            rightNumber = leftNumber
        }
    }
}

class ConverterViewModelFactory(val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConverterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ConverterViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
