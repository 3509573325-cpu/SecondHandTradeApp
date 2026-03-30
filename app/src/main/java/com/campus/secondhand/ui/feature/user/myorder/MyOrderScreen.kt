package com.campus.secondhand.ui.feature.user.myorder

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.campus.secondhand.core.utils.ToastUtils
import com.campus.secondhand.data.model.ui.OrderUiModel

@Composable
fun MyOrderScreen() {
    val viewModel: MyOrderViewModel = hiltViewModel()
    val buyerOrderList by viewModel.buyerOrderList.collectAsStateWithLifecycle()
    val sellerOrderList by viewModel.sellerOrderList.collectAsStateWithLifecycle()
    val operateResult by viewModel.operateResult.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // 监听操作结果
    LaunchedEffect(operateResult) {
        when (operateResult) {
            1 -> ToastUtils.show(context, "操作成功！")
            2 -> ToastUtils.show(context, "操作失败！")
        }
        viewModel.resetOperateResult()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // 买家订单区域
        Text(text = "我的购买订单", modifier = Modifier.padding(bottom = 8.dp))
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 16.dp)
        ) {
            items(buyerOrderList) { order ->
                OrderItem(
                    order = order,
                    isSeller = false,
                    onConfirmShip = {},
                    onConfirmReceive = { viewModel.confirmReceive(order.orderId) }
                )
            }
        }

        // 卖家订单区域
        Text(text = "我的售出订单", modifier = Modifier.padding(bottom = 8.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(sellerOrderList) { order ->
                OrderItem(
                    order = order,
                    isSeller = true,
                    onConfirmShip = { viewModel.confirmShip(order.orderId) },
                    onConfirmReceive = {}
                )
            }
        }
    }
}

@Composable
fun OrderItem(
    order: OrderUiModel,
    isSeller: Boolean,
    onConfirmShip: () -> Unit,
    onConfirmReceive: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "订单号：${order.orderNo}")
            Text(text = "商品：${order.goodsName}")
            Text(text = "价格：¥${order.price}")
            Text(text = "状态：${order.orderStatus}")
            Text(text = "创建时间：${order.createTime}")

            Spacer(modifier = Modifier.height(8.dp))

            // 按钮区域
            Row(horizontalArrangement = Arrangement.End) {
                if (isSeller && order.rawStatus == 0) {
                    // 卖家：待发货 → 确认发货
                    Button(onClick = onConfirmShip) {
                        Text(text = "确认发货")
                    }
                } else if (!isSeller && order.rawStatus == 1) {
                    // 买家：已发货 → 确认收货
                    Button(onClick = onConfirmReceive) {
                        Text(text = "确认收货")
                    }
                }
            }
        }
    }
}