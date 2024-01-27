import android.graphics.Paint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.billsAplication.extension.toPercent
import kotlin.math.atan2
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Component for creating Pie Chart
 * Slices are painted clockwise
 * e.g. 1st input value starts from top to the right, etc
 */

private const val animationDuration = 800
private const val chartDegrees = 360f
private const val emptyIndex = -1

@Composable
internal fun PieChart(
    modifier: Modifier = Modifier,
    colors: List<Color>,
    inputValues: List<Float>,
    textColor: Color = MaterialTheme.colorScheme.primary,
    animated: Boolean = true,
    onClick: (indexCategory: Int) -> Unit
) {

    assert(inputValues.isNotEmpty() && inputValues.size == colors.size) {
        "Input values count must be equal to colors size"
    }
    // if list is empty
    val txtEmptyList = stringResource(id = com.billsAplication.R.string.no_values_analytics)
    val isEmpty = inputValues.first() == 0f && colors.first() == Color.Transparent

    // start drawing clockwise (top to right)
    var startAngle = 270f

    // calculate each input percentage
    val proportions = inputValues.toPercent()

    // calculate each input slice degrees
    val angleProgress = proportions.map { prop ->
        chartDegrees * prop / 100
    }

    // clicked slice in chart
    var clickedItemIndex by remember {
        mutableStateOf(emptyIndex)
    }

    // calculate each slice end point in degrees, for handling click position
    val progressSize = mutableListOf<Float>()

    LaunchedEffect(angleProgress) {
        progressSize.add(angleProgress.first())
        for (x in 1 until angleProgress.size) {
            progressSize.add(angleProgress[x] + progressSize[x - 1])
        }
    }

    // used for animating each slice
    val pathPortion = remember {
        Animatable(initialValue = 0f)
    }

    // animate chart slices on composition
    LaunchedEffect(inputValues) {
        pathPortion.animateTo(1f, animationSpec = tween(if (animated) animationDuration else 0))
    }

    // text style
    val density = LocalDensity.current

    val textFontSize = with(density) { 30.dp.toPx() }
    val textPaint = remember {
        Paint().apply {
            color = textColor.toArgb()
            textSize = textFontSize
            textAlign = Paint.Align.CENTER
        }
    }

    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {

        val canvasSize = min(constraints.maxWidth, constraints.maxHeight)
        val size = Size(canvasSize.toFloat(), canvasSize.toFloat())
        val canvasSizeDp = with(density) { canvasSize.toDp() }

        if (isEmpty)
            clickedItemIndex = emptyIndex

        if (inputValues.lastIndex < clickedItemIndex )
            clickedItemIndex = inputValues.lastIndex

        Canvas(
            modifier = Modifier
                .size(canvasSizeDp)
                .pointerInput(inputValues) {

                    detectTapGestures { offset ->
                        val clickedAngle = touchPointToAngle(
                            width = canvasSize.toFloat(),
                            height = canvasSize.toFloat(),
                            touchX = offset.x,
                            touchY = offset.y,
                            chartDegrees = chartDegrees
                        )
                        progressSize.forEachIndexed { index, item ->
                            if (clickedAngle <= item) {
                                clickedItemIndex = index
                                return@detectTapGestures
                            }
                        }
                    }
                }
        ) {
            //If list is empty
            if (isEmpty) {
                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawText(
                        txtEmptyList,
                        (canvasSize / 2) + textFontSize / 4,
                        (canvasSize / 2) + textFontSize / 4,
                        textPaint
                    )
                }
            }

            angleProgress.forEachIndexed { index, angle ->
                drawArc(
                    color = colors[index],
                    startAngle = startAngle,
                    sweepAngle = angle * pathPortion.value,
                    useCenter = true,
                    size = size,
                    style = Fill
                )
                startAngle += angle
            }

            if (clickedItemIndex != emptyIndex) {
                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawText(
                        "${proportions[clickedItemIndex].roundToInt()}%",
                        (canvasSize / 2) + textFontSize / 4,
                        (canvasSize / 2) + textFontSize / 4,
                        textPaint
                    )
                }
                onClick.invoke(clickedItemIndex)
            }
        }
    }

}

internal fun touchPointToAngle(
    width: Float,
    height: Float,
    touchX: Float,
    touchY: Float,
    chartDegrees: Float
): Double {
    val x = touchX - (width * 0.5f)
    val y = touchY - (height * 0.5f)
    var angle = Math.toDegrees(atan2(y.toDouble(), x.toDouble()) + Math.PI / 2)
    angle = if (angle < 0) angle + chartDegrees else angle
    return angle
}