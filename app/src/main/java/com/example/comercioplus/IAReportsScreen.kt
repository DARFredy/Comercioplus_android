package com.example.comercioplus

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun IAReportsScreen(navController: NavController, viewModel: ReportsViewModel = viewModel()) {
    val reportData by viewModel.reportData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val primaryOrange = Color(0xFFE65A00)
    val slateBg = Color(0xFF0F172A)
    val cardColor = Color(0xFF1E293B)

    LaunchedEffect(Unit) {
        viewModel.fetchAiReport()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(slateBg)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, null, tint = Color.White)
            }
            Text(
                "IA Comercial y Reportes",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // AI Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(primaryOrange.copy(alpha = 0.1f))
                .border(1.dp, primaryOrange.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column {
                Text("IA COMERCIAL", color = primaryOrange, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("Centro inteligente de decisiones", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                Text("Analítica real conectada al backend para ventas, IVA e inventario.", color = Color.Gray, fontSize = 12.sp)
            }
        }

        if (isLoading) {
            Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = primaryOrange)
            }
        } else {
            // Stats Grid
            Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                KpiReportCard("Ventas Brutas", "$${reportData?.sales_total ?: 0.0}", Icons.Default.TrendingUp, Modifier.weight(1f), primaryOrange, cardColor)
                KpiReportCard("IVA Acumulado", "$${reportData?.tax_total ?: 0.0}", Icons.Default.AccountBalanceWallet, Modifier.weight(1f), Color(0xFF10B981), cardColor)
            }

            // AI Analysis Section
            Card(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, null, tint = primaryOrange, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Análisis de OpenAI", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        reportData?.ai_analysis ?: "No hay análisis disponible en este momento.",
                        color = Color(0xFF94A3B8),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }

            // Recommendations
            if (reportData?.recommendations?.isNotEmpty() == true) {
                Text(
                    "Recomendaciones IA",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                reportData?.recommendations?.forEach { rec ->
                    RecommendationItem(rec, cardColor)
                }
            }

            Spacer(Modifier.height(16.dp))

            // IVA Adjustment Button
            Button(
                onClick = { viewModel.adjustIVA(19.0) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryOrange),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.AutoFixHigh, null)
                Spacer(Modifier.width(8.dp))
                Text("IA: Ajustar Precios al IVA Actual (19%)", fontWeight = FontWeight.Bold)
            }
            
            // Payment Methods Section
            PaymentMethodsSection(cardColor, primaryOrange)
        }
    }
}

@Composable
fun KpiReportCard(label: String, value: String, icon: ImageVector, modifier: Modifier, iconColor: Color, cardColor: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(Modifier.padding(16.dp)) {
            Icon(icon, null, tint = iconColor, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(8.dp))
            Text(value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
            Text(label, color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
fun RecommendationItem(text: String, cardColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(cardColor)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Lightbulb, null, tint = Color.Yellow, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(12.dp))
        Text(text, color = Color(0xFFE2E8F0), fontSize = 13.sp)
    }
}

@Composable
fun PaymentMethodsSection(cardColor: Color, primaryColor: Color) {
    Column(Modifier.padding(16.dp)) {
        Text("Métodos de Pago Habilitados", color = Color.White, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        val methods = listOf(
            "Efectivo" to Icons.Default.Money,
            "Transferencia Bancaria" to Icons.Default.AccountBalance,
            "Nequi / Daviplata" to Icons.Default.QrCode,
            "Crédito ComercioPlus (Flado)" to Icons.Default.AccessTime
        )
        
        methods.forEach { (name, icon) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(cardColor)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, null, tint = primaryColor)
                Spacer(Modifier.width(16.dp))
                Text(name, color = Color.White, fontSize = 14.sp)
                Spacer(Modifier.weight(1f))
                Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
            }
        }
    }
}
