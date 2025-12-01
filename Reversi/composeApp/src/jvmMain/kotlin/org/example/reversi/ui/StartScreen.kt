package org.example.reversi.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import reversi.composeapp.generated.resources.Res
import reversi.composeapp.generated.resources.reversi

@Composable
fun StartScreen(
    onEnterGame: (String) -> Unit,
    onCreateGame: () -> Unit,
    onResolution: () -> Unit
) {
    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value) {
        EnterGameDialog(
            onConfirm = { name ->
                showDialog.value = false
                onEnterGame(name)
            },
            onDismiss = { showDialog.value = false }
        )
    }

    // animação de gradiente horizontal
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F260F)), // fundo geral escuro/verde
        contentAlignment = Alignment.Center
    ) {
        // Card maior verticalmente, largura controlada
        Card(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .padding(16.dp)
                .fillMaxWidth(0.86f)
                .height(420.dp), // maior para cima/baixo
            elevation = 8.dp,
            backgroundColor = Color.Transparent
        ) {
            // recolhe largura para calcular o degradê animado
            Box(
                modifier = Modifier
                    .onSizeChanged { boxWidthPx = it.width }
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF49A64C), Color(0xFF48A54B), Color(0xFF47A34A), Color(0xFF46A249), Color(0xFF45A049),
                                Color(0xFF449F48), Color(0xFF439E47), Color(0xFF429C46), Color(0xFF419B45), Color(0xFF409A44),
                                Color(0xFF3F9843), Color(0xFF3E9742), Color(0xFF3D9641), Color(0xFF3C9440), Color(0xFF3C9340),
                                Color(0xFF3B923F), Color(0xFF3A913E), Color(0xFF3A903E), Color(0xFF398F3D), Color(0xFF388E3C),
                                Color(0xFF388D3C), Color(0xFF378C3B), Color(0xFF368B3A), Color(0xFF368A3A), Color(0xFF358939),
                                Color(0xFF358839), Color(0xFF348738), Color(0xFF338537), Color(0xFF338437), Color(0xFF328336),
                                Color(0xFF318235), Color(0xFF318135), Color(0xFF308034), Color(0xFF2F7F33), Color(0xFF2F7E33),
                                Color(0xFF2E7D32), Color(0xFF2E7C32), Color(0xFF2D7B31), Color(0xFF2C7A30), Color(0xFF2C7930),
                                Color(0xFF2B782F), Color(0xFF2A772E), Color(0xFF2A762E), Color(0xFF29752D), Color(0xFF28742D),
                                Color(0xFF28732C), Color(0xFF27722B), Color(0xFF27712B), Color(0xFF26702A), Color(0xFF256F2A),
                                Color(0xFF256E29), Color(0xFF246D29), Color(0xFF236C28), Color(0xFF236B28), Color(0xFF226A27),
                                Color(0xFF216926), Color(0xFF216826), Color(0xFF206725), Color(0xFF206625), Color(0xFF1F6524),
                                Color(0xFF1F6424), Color(0xFF1E6323), Color(0xFF1D6222), Color(0xFF1D6122), Color(0xFF1C6021),
                                Color(0xFF1C5F21), Color(0xFF1B5E20), Color(0xFF1B5D20), Color(0xFF1A5C1F), Color(0xFF195B1E),
                            ),
                            start = Offset(x = if (boxWidthPx == 0) 0f else animProgress * boxWidthPx, y = 0f),
                            end = Offset(x = if (boxWidthPx == 0) boxWidthPx.toFloat() else (animProgress * boxWidthPx + boxWidthPx), y = 0f)
                        ),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .padding(28.dp)
            ) {
                Image(
                    painter = painterResource(Res.drawable.reversi),
                    contentDescription = "Mini Tabuleiro Reversi",
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.TopStart)
                        .offset(x = (-16).dp, y = (-16).dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(18.dp, alignment = Alignment.CenterVertically)
                ) {
                    // título ornamentado "Reversi" (estilo igual ao de ResolutionScreen)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val label = "Reversi"
                        val outlineColor = Color.Black.copy(alpha = 0.72f)

                        val mainStyle = TextStyle(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF9BE49A),
                                    Color(0xFF49A64C),
                                    Color(0xFF2F7F33)
                                )
                            ),
                            fontSize = 50.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.SansSerif,
                            letterSpacing = 1.2.sp,
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.35f),
                                offset = Offset(3f, 3f),
                                blurRadius = 8f
                            )
                        )

                        val outlineStyle = TextStyle(
                            color = outlineColor,
                            fontSize = 50.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.SansSerif,
                            letterSpacing = 1.2.sp
                        )

                        // múltiplas camadas para criar contorno
                        Text(label, modifier = Modifier.padding(bottom = 0.dp).then(Modifier.offset(x = (-1).dp, y = (-1).dp)), style = outlineStyle, textAlign = TextAlign.Center)
                        Text(label, modifier = Modifier.offset(x = (-1).dp, y = 0.dp), style = outlineStyle, textAlign = TextAlign.Center)
                        Text(label, modifier = Modifier.offset(x = (-1).dp, y = 1.dp), style = outlineStyle, textAlign = TextAlign.Center)
                        Text(label, modifier = Modifier.offset(x = 0.dp, y = (-1).dp), style = outlineStyle, textAlign = TextAlign.Center)
                        Text(label, modifier = Modifier.offset(x = 0.dp, y = 1.dp), style = outlineStyle, textAlign = TextAlign.Center)
                        Text(label, modifier = Modifier.offset(x = 1.dp, y = (-1).dp), style = outlineStyle, textAlign = TextAlign.Center)
                        Text(label, modifier = Modifier.offset(x = 1.dp, y = 0.dp), style = outlineStyle, textAlign = TextAlign.Center)
                        Text(label, modifier = Modifier.offset(x = 1.dp, y = 1.dp), style = outlineStyle, textAlign = TextAlign.Center)

                        Text(
                            label,
                            style = mainStyle,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    AccentButton(
                        onClick = { showDialog.value = true },
                        text = "Entrar num jogo",
                        modifier = Modifier.fillMaxWidth(0.3f)
                    )

                    AccentButton(
                        onClick = onCreateGame,
                        text = "Criar jogo",
                        modifier = Modifier.fillMaxWidth(0.3f)
                    )

                    AccentButton(
                        onClick = onResolution,
                        text = "Resolução",
                        modifier = Modifier.fillMaxWidth(0.2f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AccentButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    background: Color = Color.White.copy(alpha = 0.06f),
    borderColor: Color = Color.Black.copy(alpha = 0.75f),
    shape: Shape = RoundedCornerShape(32.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
) {
    var hovered by remember { mutableStateOf(false) }

    val bg = if (hovered) background.copy(alpha = 0.12f) else background
    val elevation = if (hovered) 6.dp else 0.dp

    Box(
        modifier = modifier
            .pointerMoveFilter(
                onEnter = {
                    hovered = true
                    true
                },
                onExit = {
                    hovered = false
                    true
                }
            )
            .shadow(elevation, shape = shape)
            .clip(shape)
            .background(bg, shape)
            .border(BorderStroke(2.dp, borderColor), shape)
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        val outlineColor = Color.Black.copy(alpha = 0.72f)

        val mainStyle = TextStyle(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFF9BE49A),
                    Color(0xFF49A64C),
                    Color(0xFF2F7F33)
                )
            ),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.SansSerif,
            letterSpacing = 0.4.sp,
            shadow = Shadow(
                color = Color.Black.copy(alpha = 0.25f),
                offset = Offset(1f, 1f),
                blurRadius = 6f
            )
        )

        val outlineStyle = TextStyle(
            color = outlineColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.SansSerif,
            letterSpacing = 0.4.sp
        )

        // camadas de contorno
        Text(text, modifier = Modifier.offset(x = (-1).dp, y = (-1).dp), style = outlineStyle)
        Text(text, modifier = Modifier.offset(x = (-1).dp, y = 0.dp), style = outlineStyle)
        Text(text, modifier = Modifier.offset(x = (-1).dp, y = 1.dp), style = outlineStyle)
        Text(text, modifier = Modifier.offset(x = 0.dp, y = (-1).dp), style = outlineStyle)
        Text(text, modifier = Modifier.offset(x = 0.dp, y = 1.dp), style = outlineStyle)
        Text(text, modifier = Modifier.offset(x = 1.dp, y = (-1).dp), style = outlineStyle)
        Text(text, modifier = Modifier.offset(x = 1.dp, y = 0.dp), style = outlineStyle)
        Text(text, modifier = Modifier.offset(x = 1.dp, y = 1.dp), style = outlineStyle)

        // texto principal com degrade
        Text(
            text,
            style = mainStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}