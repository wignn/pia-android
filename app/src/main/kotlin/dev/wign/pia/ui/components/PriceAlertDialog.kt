package dev.wign.pia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import dev.wign.pia.ui.theme.PiaAccent
import dev.wign.pia.ui.theme.PiaBorder
import dev.wign.pia.ui.theme.PiaCard
import dev.wign.pia.ui.theme.PiaDown
import dev.wign.pia.ui.theme.PiaText
import dev.wign.pia.ui.theme.PiaTextMuted
import dev.wign.pia.ui.theme.PiaUp

data class PriceAlert(
    val id: String = java.util.UUID.randomUUID().toString(),
    val symbol: String,
    val targetPrice: Double,
    val condition: String, // "ABOVE", "BELOW"
    var isTriggered: Boolean = false
)

@Composable
fun PriceAlertDialog(
    symbol: String,
    currentPrice: Double,
    onSetAlert: (Double, String) -> Unit,
    onDismiss: () -> Unit
) {
    var priceText by remember { mutableStateOf(if (currentPrice > 0) String.format("%.2f", currentPrice) else "") }
    var selectedCondition by remember { mutableStateOf("ABOVE") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = PiaCard,
            modifier = Modifier
                .fillMaxWidth()
                .border(0.5.dp, PiaBorder, RoundedCornerShape(12.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Alert",
                            tint = PiaAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "Set Price Alert",
                            color = PiaText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = PiaTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "$symbol • Current: ${if (currentPrice > 0) String.format("%,.2f", currentPrice) else "--"}",
                    color = PiaTextMuted,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Condition Buttons: Above / Below
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (selectedCondition == "ABOVE") PiaUp.copy(alpha = 0.2f) else PiaBorder.copy(alpha = 0.4f))
                            .border(0.5.dp, if (selectedCondition == "ABOVE") PiaUp else PiaBorder, RoundedCornerShape(6.dp))
                            .clickable { selectedCondition = "ABOVE" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Crosses Above (≥)",
                            color = if (selectedCondition == "ABOVE") PiaUp else PiaTextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (selectedCondition == "BELOW") PiaDown.copy(alpha = 0.2f) else PiaBorder.copy(alpha = 0.4f))
                            .border(0.5.dp, if (selectedCondition == "BELOW") PiaDown else PiaBorder, RoundedCornerShape(6.dp))
                            .clickable { selectedCondition = "BELOW" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Crosses Below (≤)",
                            color = if (selectedCondition == "BELOW") PiaDown else PiaTextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Price Input Field
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Target Price", color = PiaTextMuted, fontSize = 12.sp) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PiaAccent,
                        unfocusedBorderColor = PiaBorder,
                        focusedTextColor = PiaText,
                        unfocusedTextColor = PiaText
                    )
                )

                // Quick Offset Pills (+1%, +2%, -1%, -2%)
                if (currentPrice > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            Pair("+1%", currentPrice * 1.01),
                            Pair("+2%", currentPrice * 1.02),
                            Pair("-1%", currentPrice * 0.99),
                            Pair("-2%", currentPrice * 0.98)
                        ).forEach { (label, calculated) ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(PiaBorder.copy(alpha = 0.5f))
                                    .clickable {
                                        priceText = String.format("%.2f", calculated)
                                        if (label.startsWith("+")) selectedCondition = "ABOVE"
                                        else selectedCondition = "BELOW"
                                    }
                                    .padding(vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = PiaTextMuted,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action Button
                Button(
                    onClick = {
                        val target = priceText.toDoubleOrNull()
                        if (target != null && target > 0) {
                            onSetAlert(target, selectedCondition)
                            onDismiss()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PiaAccent),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Save Alert", color = PiaText, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
