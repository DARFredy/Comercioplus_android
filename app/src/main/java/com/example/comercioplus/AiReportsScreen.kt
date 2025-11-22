package com.example.comercioplus

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest

// --- ESTRUCTURA DE TEMAS PERSONALIZADOS PARA FREDY ---
data class BotDesign(
    val name: String,
    val botName: String,
    val avatar: String, 
    val accentColor: Color,
    val backgroundColor: List<Color>,
    val cardColor: Color,
    val userCardColor: Color,
    val icon: ImageVector,
    val welcomeMsg: String,
    val suffix: String = ""
)

// URLs en PNG de alta compatibilidad
val BOT_THEMES = listOf(
    BotDesign(
        name = "Neko Chibi",
        botName = "Sakura-chan",
        avatar = "https://api.dicebear.com/7.x/lorelei/png?seed=Sakura&size=256",
        accentColor = Color(0xFFFF85A1),
        backgroundColor = listOf(Color(0xFF4D162E), Color(0xFF1A0F1F)),
        cardColor = Color(0xFF802B4A),
        userCardColor = Color(0xFFFF5D8F),
        icon = Icons.Default.Favorite,
        welcomeMsg = "¡Konichiwa Fredy-kun! 🌸 ¡Hagamos brillar ese negocio, nya!",
        suffix = " ✨"
    ),
    BotDesign(
        name = "Cyber Cat",
        botName = "Unit-01 Neko",
        avatar = "https://api.dicebear.com/7.x/adventurer/png?seed=Cyber&size=256",
        accentColor = Color(0xFF00F0FF),
        backgroundColor = listOf(Color(0xFF0B0E14), Color(0xFF1A1D23)),
        cardColor = Color(0xFF242933),
        userCardColor = Color(0xFF00F0FF).copy(alpha = 0.4f),
        icon = Icons.Default.PrecisionManufacturing,
        welcomeMsg = "[SISTEMA OK] Escaneando anomalías, Administrador Fredy..."
    ),
    BotDesign(
        name = "Bot Tierno",
        botName = "Mini-Mew",
        avatar = "https://api.dicebear.com/7.x/bottts/png?seed=Mew&size=256",
        accentColor = Color(0xFF60A5FA),
        backgroundColor = listOf(Color(0xFFEFF6FF), Color(0xFFDBEAFE)),
        cardColor = Color(0xFFFFFFFF),
        userCardColor = Color(0xFF3B82F6),
        icon = Icons.Default.SmartToy,
        welcomeMsg = "¡Bip bup! ¡Hola Fredy! 🤖 ¿En qué te ayudo hoy?"
    ),
    BotDesign(
        name = "Gamer Pro",
        botName = "PVP Assistant",
        avatar = "https://api.dicebear.com/7.x/avataaars/png?seed=Gamer&size=256",
        accentColor = Color(0xFF8B5CF6),
        backgroundColor = listOf(Color(0xFF000000), Color(0xFF1E1B4B)),
        cardColor = Color(0xFF312E81),
        userCardColor = Color(0xFF8B5CF6),
        icon = Icons.Default.SportsEsports,
        welcomeMsg = "¡Ready Player Fredy! 🎮 Combo de ganancias brutal detectado."
    ),
    BotDesign(
        name = "Ejecutivo",
        botName = "Manager AI",
        avatar = "https://api.dicebear.com/7.x/avataaars/png?seed=Manager&size=256",
        accentColor = Color(0xFF334155),
        backgroundColor = listOf(Color(0xFFF8FAFC), Color(0xFFF1F5F9)),
        cardColor = Color(0xFFFFFFFF),
        userCardColor = Color(0xFF0F172A),
        icon = Icons.Default.Work,
        welcomeMsg = "Buen día, Administrador Fredy. KPI listos para revisión."
    ),
    BotDesign(
        name = "Dark Hacker",
        botName = "Shadow Root",
        avatar = "https://api.dicebear.com/7.x/identicon/png?seed=Shadow&size=256",
        accentColor = Color(0xFFEF4444),
        backgroundColor = listOf(Color(0xFF000000), Color(0xFF110000)),
        cardColor = Color(0xFF220000),
        userCardColor = Color(0xFF991B1B),
        icon = Icons.Default.Terminal,
        welcomeMsg = "Acceso concedido. Fredy, el sistema es invulnerable ahora."
    ),
    BotDesign(
        name = "Idol Music",
        botName = "Melody AI",
        avatar = "https://api.dicebear.com/7.x/lorelei/png?seed=Melody&size=256",
        accentColor = Color(0xFFF472B6),
        backgroundColor = listOf(Color(0xFFFFF1F2), Color(0xFFFCE7F3)),
        cardColor = Color(0xFFFFFFFF),
        userCardColor = Color(0xFFBE185D),
        icon = Icons.Default.MusicNote,
        welcomeMsg = "¡Sigue el ritmo del mercado, Fredy! 🎵 ¡Éxito total!"
    ),
    BotDesign(
        name = "Galáctico",
        botName = "Nova Explorer",
        avatar = "https://api.dicebear.com/7.x/bottts/png?seed=Nova&size=256",
        accentColor = Color(0xFFFDE047),
        backgroundColor = listOf(Color(0xFF020617), Color(0xFF1E1B4B)),
        cardColor = Color(0xFF1E293B),
        userCardColor = Color(0xFFEAB308),
        icon = Icons.Default.RocketLaunch,
        welcomeMsg = "Explorando nuevas fronteras, Fredy. 🚀 ¡Ignición!"
    ),
    BotDesign(
        name = "Zen / Eco",
        botName = "Spirit Leaf",
        avatar = "https://api.dicebear.com/7.x/avataaars/png?seed=Zen&size=256",
        accentColor = Color(0xFF22C55E),
        backgroundColor = listOf(Color(0xFFF0FDF4), Color(0xFFDCFCE7)),
        cardColor = Color(0xFFFFFFFF),
        userCardColor = Color(0xFF15803D),
        icon = Icons.Default.Spa,
        welcomeMsg = "Paz y prosperidad, Fredy. 🌿 Todo está en equilibrio."
    ),
    BotDesign(
        name = "Retro 8-Bit",
        botName = "Pixel Pal",
        avatar = "https://api.dicebear.com/7.x/pixel-art/png?seed=Pixel&size=256",
        accentColor = Color(0xFFFB923C),
        backgroundColor = listOf(Color(0xFFFFF7ED), Color(0xFFFFEDD5)),
        cardColor = Color(0xFFFFFFFF),
        userCardColor = Color(0xFFC2410C),
        icon = Icons.Default.Gamepad,
        welcomeMsg = "¡START FREDY! 🕹️ High Score de Comercioplus hoy."
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiReportsScreen(
    navController: NavController,
    aiViewModel: AiViewModel,
    dashboardViewModel: DashboardViewModel,
    productViewModel: ProductViewModel
) {
    val aiState by aiViewModel.uiState.collectAsState()
    val products by productViewModel.products.collectAsState()
    var userMessage by remember { mutableStateOf("") }
    var currentDesign by remember { mutableStateOf(BOT_THEMES[0]) } 

    val gradientBackground = Brush.verticalGradient(colors = currentDesign.backgroundColor)
    val isDarkTheme = currentDesign.backgroundColor.first().luminance() < 0.5f
    val textColor = if (isDarkTheme) Color.White else Color(0xFF1E293B)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(42.dp),
                            shape = CircleShape,
                            color = Color.White,
                            shadowElevation = 4.dp,
                            border = BorderStroke(2.dp, currentDesign.accentColor)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(currentDesign.avatar)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Bot Avatar",
                                modifier = Modifier.padding(2.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(currentDesign.botName, color = textColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("Administrador Fredy", color = currentDesign.accentColor, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Volver", tint = textColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().background(gradientBackground).padding(padding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(BOT_THEMES) { design ->
                        val isSelected = currentDesign == design
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { currentDesign = design }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSelected) design.accentColor else Color.White.copy(alpha = 0.1f))
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) Color.White else Color.Gray.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(16.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    design.icon, 
                                    contentDescription = null, 
                                    tint = if (isSelected) Color.White else textColor.copy(alpha = 0.7f),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Text(
                                design.name, 
                                fontSize = 10.sp, 
                                color = textColor, 
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    item {
                        BotMessage(currentDesign.welcomeMsg, currentDesign)
                    }
                    items(aiState.chatHistory) { message ->
                        if (message.isUser) UserMessage(message.text, currentDesign) 
                        else BotMessage(message.text + currentDesign.suffix, currentDesign, message.isBotAction)
                    }
                    if (aiState.isLoading) {
                        item { 
                            Text("Escribiendo... 💭", color = currentDesign.accentColor, fontSize = 12.sp, modifier = Modifier.padding(start = 52.dp, top = 8.dp))
                        }
                    }
                }

                Surface(
                    color = currentDesign.backgroundColor.last().copy(alpha = 0.95f),
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    shadowElevation = 20.dp
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = userMessage,
                            onValueChange = { userMessage = it },
                            placeholder = { Text("¿Órdenes, Fredy?", color = Color.Gray) },
                            modifier = Modifier.weight(1f),
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedIndicatorColor = currentDesign.accentColor
                            )
                        )
                        Spacer(Modifier.width(12.dp))
                        IconButton(
                            onClick = {
                                if (userMessage.isNotBlank()) {
                                    aiViewModel.sendMessage(
                                        userText = userMessage,
                                        products = products,
                                        botName = currentDesign.botName,
                                        personality = currentDesign.name
                                    )
                                    userMessage = ""
                                }
                            },
                            modifier = Modifier
                                .size(52.dp)
                                .background(currentDesign.accentColor, CircleShape)
                                .shadow(4.dp, CircleShape)
                        ) {
                            Icon(Icons.Default.Send, "Enviar", tint = if (isDarkTheme) Color.Black else Color.White)
                        }
                    }
                }
            }
        }
    }
}

private fun Color.luminance(): Float = (0.299f * red + 0.587f * green + 0.114f * blue)

@Composable
fun BotMessage(text: String, design: BotDesign, isAction: Boolean = false) {
    Row(
        Modifier.padding(vertical = 10.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            border = BorderStroke(2.dp, design.accentColor),
            shadowElevation = 4.dp
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(design.avatar)
                    .crossfade(true)
                    .build(),
                contentDescription = "Bot",
                modifier = Modifier.clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(Modifier.width(10.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = if (isAction) design.accentColor else design.cardColor),
            shape = RoundedCornerShape(topStart = 0.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Text(
                text, 
                Modifier.padding(14.dp), 
                color = if (isAction) Color.Black else Color.White, 
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun UserMessage(text: String, design: BotDesign) {
    Row(
        Modifier.padding(vertical = 10.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Top
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = design.userCardColor),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 0.dp, bottomStart = 20.dp, bottomEnd = 20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Text(text, Modifier.padding(14.dp), color = Color.White, fontSize = 14.sp)
        }
        Spacer(Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(design.userCardColor)
                .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Shield, null, tint = Color.White, modifier = Modifier.size(22.dp))
        }
    }
}
