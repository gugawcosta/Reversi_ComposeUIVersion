package reversi_ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp

/**
 * Tela de seleção de resolução e modo de tela cheia.
 *
 * @param onBack Função chamada ao clicar em "Voltar".
 * @param onSetWindowSize Função chamada ao selecionar uma resolução.
 * Recebe a largura e altura em Dp.
 * @param onToggleFullscreen Função chamada ao alternar o modo tela cheia.
 * Recebe true para ativar e false para desativar.
 */
@Composable
fun ResolutionScreen(
    onBack: () -> Unit,
    onSetWindowSize: (widthDp: Dp, heightDp: Dp) -> Unit,
    onToggleFullscreen: (Boolean) -> Unit
) {
    val transition = rememberInfiniteTransition()
    val animProgress by transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    var boxWidthPx by remember { mutableStateOf(0) }
    var isFullscreen by remember { mutableStateOf(false) }

    val buttonCornerRadius = 12.dp
    val buttonShape = RoundedCornerShape(buttonCornerRadius)

    // Cores dos botões
    val vividButtonBg = Color(0xFF2E7D32)
    val vividButtonBorder = Color(0xFF81C784)

    val backButtonBg = Color(0xFF1B5E20).copy(alpha = 0.9f)
    val backButtonBorder = Color(0xFF81C784).copy(alpha = 0.5f)

    // Cores dos botões desativados
    val disabledButtonBg = Color(0xFF1B5E20).copy(alpha = 0.4f)
    val disabledText = Color.White.copy(alpha = 0.4f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F260F)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .padding(16.dp)
                .fillMaxWidth(0.86f)
                .height(520.dp),
            elevation = 8.dp,
            backgroundColor = Color.Transparent
        ) {
            Box(
                modifier = Modifier
                    .onSizeChanged { boxWidthPx = it.width }
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF49A64C), Color(0xFF195B1E)),
                            start = Offset(x = if (boxWidthPx == 0) 0f else animProgress * boxWidthPx, y = 0f),
                            end = Offset(x = if (boxWidthPx == 0) 0.toFloat() else (animProgress * boxWidthPx + boxWidthPx), y = 0f)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(vertical = 32.dp, horizontal = 28.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Título
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val label = "Resoluções Disponíveis:"
                        val outlineColor = Color.Black.copy(alpha = 0.72f)
                        val mainStyle = TextStyle(
                            brush = Brush.horizontalGradient(listOf(Color(0xFF9BE49A), Color(0xFF49A64C), Color(0xFF2F7F33))),
                            fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.SansSerif,
                            letterSpacing = 1.5.sp, shadow = Shadow(color = Color.Black.copy(alpha = 0.35f), offset = Offset(2f, 2f), blurRadius = 6f)
                        )
                        val outlineStyle = TextStyle(color = outlineColor, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.SansSerif, letterSpacing = 1.5.sp)

                        for (x in -1..1) for (y in -1..1) if (x != 0 || y != 0)
                            Text(label, modifier = Modifier.offset(x.dp, y.dp), style = outlineStyle, textAlign = TextAlign.Center)

                        Text(label, style = mainStyle, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val buttonTextStyle = TextStyle(
                        fontWeight = FontWeight.Bold, fontSize = 18.sp, letterSpacing = 1.2.sp,
                        shadow = Shadow(color = Color.Black.copy(alpha = 0.25f), offset = Offset(1f, 1f), blurRadius = 2f)
                    )

                    // Botões de resolução
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                        // 800 x 600
                        Card(
                            shape = buttonShape,
                            border = BorderStroke(2.dp, if(!isFullscreen) vividButtonBorder else Color.Transparent),
                            backgroundColor = if(!isFullscreen) vividButtonBg else disabledButtonBg,
                            elevation = if(!isFullscreen) 6.dp else 0.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp)
                                .clip(buttonShape)
                                .clickable(enabled = !isFullscreen) { onSetWindowSize(800.dp, 600.dp) }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("800 x 600", style = buttonTextStyle.copy(color = if(!isFullscreen) Color.White else disabledText))
                            }
                        }

                        // 1024 x 768
                        Card(
                            shape = buttonShape,
                            border = BorderStroke(2.dp, if(!isFullscreen) vividButtonBorder else Color.Transparent),
                            backgroundColor = if(!isFullscreen) vividButtonBg else disabledButtonBg,
                            elevation = if(!isFullscreen) 6.dp else 0.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp)
                                .clip(buttonShape)
                                .clickable(enabled = !isFullscreen) { onSetWindowSize(1024.dp, 768.dp) }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("1024 x 768", style = buttonTextStyle.copy(color = if(!isFullscreen) Color.White else disabledText))
                            }
                        }

                        // 1280 x 720
                        Card(
                            shape = buttonShape,
                            border = BorderStroke(2.dp, if(!isFullscreen) vividButtonBorder else Color.Transparent),
                            backgroundColor = if(!isFullscreen) vividButtonBg else disabledButtonBg,
                            elevation = if(!isFullscreen) 6.dp else 0.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp)
                                .clip(buttonShape)
                                .clickable(enabled = !isFullscreen) { onSetWindowSize(1280.dp, 720.dp) }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("1280 x 720", style = buttonTextStyle.copy(color = if(!isFullscreen) Color.White else disabledText))
                            }
                        }

                        // Botão Tela Cheia
                        val fsColor = if(isFullscreen) Color(0xFFFFD54F) else Color.White
                        val fsBorder = if(isFullscreen) Color(0xFFFFD54F) else vividButtonBorder

                        // Este botão está sempre ativo
                        Card(
                            shape = buttonShape,
                            border = BorderStroke(2.dp, fsBorder),
                            backgroundColor = vividButtonBg,
                            elevation = 6.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp)
                                .clip(buttonShape)
                                .clickable {
                                    val next = !isFullscreen
                                    isFullscreen = next
                                    onToggleFullscreen(next)
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    if (isFullscreen) "Sair de Tela Cheia" else "Tela Cheia",
                                    style = buttonTextStyle.copy(color = fsColor)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Botão Voltar
                    Card(
                        shape = buttonShape,
                        border = BorderStroke(1.dp, backButtonBorder),
                        backgroundColor = backButtonBg,
                        elevation = 0.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(buttonShape)
                            .clickable { onBack() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("Voltar", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, letterSpacing = 1.sp, color = Color.LightGray)
                        }
                    }
                }
            }
        }
    }
}