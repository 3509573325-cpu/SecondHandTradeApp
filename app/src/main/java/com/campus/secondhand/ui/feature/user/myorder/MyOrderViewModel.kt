package com.campus.secondhand.ui.feature.user.myorder

import androidx.lifecycle.viewModelScope
import com.campus.secondhand.core.base.BaseViewModel
import com.campus.secondhand.data.local.UserLocalDataSource
import com.campus.secondhand.data.model.ui.OrderUiModel
import com.campus.secondhand.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MyOrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val userLocalDataSource: UserLocalDataSource,
) : BaseViewModel() {

    // 订单列表（买家视角）
    private val _buyerOrderList = MutableStateFlow<List<OrderUiModel>>(emptyList())
    val buyerOrderList: StateFlow<List<OrderUiModel>> = _buyerOrderList

    // 订单列表（卖家视角）
    private val _sellerOrderList = MutableStateFlow<List<OrderUiModel>>(emptyList())
    val sellerOrderList: StateFlow<List<OrderUiModel>> = _sellerOrderList

    // 操作结果：0-初始 1-成功 2-失败
    private val _operateResult = MutableStateFlow(0)
    val operateResult: StateFlow<Int> = _operateResult

    init {
        // 监听买家订单
        viewModelScope.launch {
            val currentUser = userLocalDataSource.getCurrentLoginUser() ?: return@launch
            orderRepository.getBuyerOrderList(currentUser.userId)
                .collect { list ->
                    _buyerOrderList.value = list
                }
        }

        // 监听卖家订单
        viewModelScope.launch {
            val currentUser = userLocalDataSource.getCurrentLoginUser() ?: return@launch
            orderRepository.getSellerOrderList(currentUser.userId)
                .collect { list ->
                    _sellerOrderList.value = list
                }
        }
    }

    // 卖家确认发货
    fun confirmShip(orderId: String) {
        viewModelScope.launch {
            val result = orderRepository.confirmShip(orderId)
            _operateResult.value = if (result) 1 else 2
        }
    }

    // 买家确认收货
    fun confirmReceive(orderId: String) {
        viewModelScope.launch {
            val result = orderRepository.confirmReceive(orderId)
            _operateResult.value = if (result) 1 else 2
        }
    }

    // 重置操作结果
    fun resetOperateResult() {
        _operateResult.value = 0
    }
}