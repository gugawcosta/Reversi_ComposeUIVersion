package reversi_ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp

@Composable
fun TranslucentButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    backgroundAlpha: Float = 0.75f,
    contentAlpha: Float = 0.6f,
    content: @Composable RowScope.() -> Unit
) {
    val translucentColors = ButtonDefaults.buttonColors(
        backgroundColor = Color(0xFF2F2F2F).copy(alpha = backgroundAlpha),
        contentColor = Color.White
    )

    Button(
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        colors = translucentColors,
        content = {
            CompositionLocalProvider(LocalContentAlpha provides contentAlpha) {
                content()
            }
        }
    )
}
@Composable
fun ResolutionScreen(
    onBack: () -> Unit,
    onSetWindowSize: (widthDp: Dp, heightDp: Dp) -> Unit,
    onToggleFullscreen: (Boolean) -> Unit // novo callback para fullscreen
) {
    // animação de gradiente horizontal (igual à StartScreen)
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
    var isFullscreen by remember { mutableStateOf(false) } // estado local do fullscreen

    // cantos dos botões proporcionais aos cantos da Box (Box/Card usa 24.dp - aqui metade = 12.dp)
    val buttonCorner = 12.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F260F)), // mesmo fundo escuro/verde
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .padding(16.dp)
                .fillMaxWidth(0.86f)
                .height(420.dp), // maior para cima/baixo igual à StartScreen
            elevation = 8.dp,
            backgroundColor = Color.Transparent
        ) {
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
                                Color(0xFF1C5F21), Color(0xFF1B5E20), Color(0xFF1B5D20), Color(0xFF1A5C1F), Color(0xFF195B1E)
                            ),
                            start = Offset(x = if (boxWidthPx == 0) 0f else animProgress * boxWidthPx, y = 0f),
                            end = Offset(x = if (boxWidthPx == 0) 0.toFloat() else (animProgress * boxWidthPx + boxWidthPx), y = 0f)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(28.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(18.dp, alignment = Alignment.CenterVertically)
                ) {
                    // título (idêntico ao que já tinha)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val label = "Escolha uma das resoluções desejadas:"
                        val outlineColor = Color.Black.copy(alpha = 0.72f)

                        val mainStyle = TextStyle(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF9BE49A),
                                    Color(0xFF49A64C),
                                    Color(0xFF2F7F33)
                                )
                            ),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif,
                            letterSpacing = 1.2.sp,
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.35f),
                                offset = Offset(2f, 2f),
                                blurRadius = 6f
                            )
                        )

                        val outlineStyle = TextStyle(
                            color = outlineColor,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif,
                            letterSpacing = 1.2.sp
                        )

                        Text(label, modifier = Modifier.offset(x = (-1).dp, y = (-1).dp), style = outlineStyle, textAlign = TextAlign.Center)
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
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    TranslucentButton(
                        onClick = { onSetWindowSize(800.dp, 600.dp) },
                        shape = RoundedCornerShape(buttonCorner)
                    ) {
                        Text("800 x 600")
                    }

                    TranslucentButton(
                        onClick = { onSetWindowSize(1024.dp, 768.dp) },
                        shape = RoundedCornerShape(buttonCorner)
                    ) {
                        Text("1024 x 768")
                    }

                    TranslucentButton(
                        onClick = { onSetWindowSize(1280.dp, 720.dp) },
                        shape = RoundedCornerShape(buttonCorner)
                    ) {
                        Text("1280 x 720")
                    }

                    TranslucentButton(
                        onClick = {
                            val next = !isFullscreen
                            isFullscreen = next
                            onToggleFullscreen(next)
                        },
                        shape = RoundedCornerShape(buttonCorner)
                    ) {
                        Text(if (isFullscreen) "Sair de Tela Cheia" else "Tela Cheia")
                    }

                    // botão Voltar com cor cinzenta-escura e cantos arredondados proporcionais
                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF2F2F2F),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(buttonCorner)
                    ) {
                        Text("Voltar")
                    }
                }
            }
        }
    }
}
