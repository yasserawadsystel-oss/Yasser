package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SystelNavyDark
import com.example.ui.theme.SystelNavyPrimary
import com.example.ui.theme.SystelSuccessGreen

/**
 * Encodes signature strokes to a compact string format:
 * strokes separated by ';', points separated by ',', x and y separated by ':'
 */
object SignatureUtils {
    fun encodeStrokes(strokes: List<List<Offset>>, width: Float, height: Float): String {
        if (strokes.isEmpty() || width <= 0f || height <= 0f) return ""
        return strokes.joinToString(";") { stroke ->
            stroke.joinToString(",") { point ->
                val nx = (point.x / width).coerceIn(0f, 1f)
                val ny = (point.y / height).coerceIn(0f, 1f)
                "%.4f:%.4f".format(java.util.Locale.US, nx, ny)
            }
        }
    }

    fun decodeStrokes(encoded: String, targetW: Float, targetH: Float): List<Path> {
        if (encoded.isBlank() || targetW <= 0f || targetH <= 0f) return emptyList()
        val paths = mutableListOf<Path>()
        val strokeStrings = encoded.split(";")
        for (sStr in strokeStrings) {
            val pointStrings = sStr.split(",")
            if (pointStrings.isEmpty()) continue
            val path = Path()
            var isFirst = true
            for (pStr in pointStrings) {
                val parts = pStr.split(":")
                if (parts.size == 2) {
                    val nx = parts[0].toFloatOrNull() ?: continue
                    val ny = parts[1].toFloatOrNull() ?: continue
                    val x = nx * targetW
                    val y = ny * targetH
                    if (isFirst) {
                        path.moveTo(x, y)
                        isFirst = false
                    } else {
                        path.lineTo(x, y)
                    }
                }
            }
            if (!isFirst) {
                paths.add(path)
            }
        }
        return paths
    }
}

/**
 * Signature Pad Composable for drawing a handwritten signature.
 */
@Composable
fun SignaturePad(
    modifier: Modifier = Modifier,
    initialSignatureData: String? = null,
    savedDefaultSignature: String? = null,
    onSignatureChanged: (encodedData: String) -> Unit
) {
    val strokes = remember { mutableStateListOf<List<Offset>>() }
    var currentStroke by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var canvasSize by remember { mutableStateOf(Pair(400f, 200f)) }
    var isUsingSavedSignature by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.5.dp, SystelNavyPrimary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Create,
                    contentDescription = null,
                    tint = SystelNavyPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "التوقيع اليدوي للتيم ليدر (Signature Pad)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.5.sp,
                    color = SystelNavyDark
                )
            }

            if (!savedDefaultSignature.isNullOrBlank()) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isUsingSavedSignature) SystelSuccessGreen.copy(alpha = 0.15f) else Color.White,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isUsingSavedSignature) SystelSuccessGreen else Color(0xFFCBD5E1)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = if (isUsingSavedSignature) SystelSuccessGreen else SystelNavyPrimary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = if (isUsingSavedSignature) "توقيعك المحفوظ مفعل" else "توقيع محفوظ مسبقاً",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isUsingSavedSignature) SystelSuccessGreen else SystelNavyPrimary
                        )
                    }
                }
            }
        }

        // Drawing Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("signature_canvas")
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                isUsingSavedSignature = false
                                currentStroke = listOf(offset)
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                currentStroke = currentStroke + change.position
                            },
                            onDragEnd = {
                                if (currentStroke.isNotEmpty()) {
                                    strokes.add(currentStroke)
                                    currentStroke = emptyList()
                                    val encoded = SignatureUtils.encodeStrokes(
                                        strokes,
                                        canvasSize.first,
                                        canvasSize.second
                                    )
                                    onSignatureChanged(encoded)
                                }
                            }
                        )
                    }
            ) {
                canvasSize = Pair(size.width, size.height)

                // If using saved signature and strokes are empty, render decoded saved signature
                if (isUsingSavedSignature && !savedDefaultSignature.isNullOrBlank() && strokes.isEmpty()) {
                    val savedPaths = SignatureUtils.decodeStrokes(savedDefaultSignature, size.width, size.height)
                    for (path in savedPaths) {
                        drawPath(
                            path = path,
                            color = Color(0xFF1E3A8A),
                            style = Stroke(
                                width = 3.5.dp.toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }

                // Render current finished strokes
                for (stroke in strokes) {
                    if (stroke.size > 1) {
                        val path = Path()
                        path.moveTo(stroke.first().x, stroke.first().y)
                        for (i in 1 until stroke.size) {
                            path.lineTo(stroke[i].x, stroke[i].y)
                        }
                        drawPath(
                            path = path,
                            color = Color(0xFF1E3A8A),
                            style = Stroke(
                                width = 3.5.dp.toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }

                // Render in-progress active stroke
                if (currentStroke.size > 1) {
                    val activePath = Path()
                    activePath.moveTo(currentStroke.first().x, currentStroke.first().y)
                    for (i in 1 until currentStroke.size) {
                        activePath.lineTo(currentStroke[i].x, currentStroke[i].y)
                    }
                    drawPath(
                        path = activePath,
                        color = Color(0xFF1E3A8A),
                        style = Stroke(
                            width = 3.5.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }

            // Watermark / hint if canvas is empty
            if (strokes.isEmpty() && currentStroke.isEmpty() && (!isUsingSavedSignature || savedDefaultSignature.isNullOrBlank())) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✍️ ارسم توقيعك هنا بإصبعك",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }

        // Action Buttons: Clear & Quick Use Saved
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = {
                    strokes.clear()
                    currentStroke = emptyList()
                    isUsingSavedSignature = false
                    onSignatureChanged("")
                },
                modifier = Modifier
                    .height(34.dp)
                    .testTag("clear_signature_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("مسح التوقيع", fontSize = 11.sp)
            }

            if (!savedDefaultSignature.isNullOrBlank()) {
                Button(
                    onClick = {
                        strokes.clear()
                        currentStroke = emptyList()
                        isUsingSavedSignature = true
                        onSignatureChanged(savedDefaultSignature)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SystelSuccessGreen),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("use_saved_signature_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("استخدام توقيعي المحفوظ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Read-only view for displaying a saved signature.
 */
@Composable
fun SignatureDisplayView(
    signatureData: String,
    modifier: Modifier = Modifier,
    strokeColor: Color = Color(0xFF1E3A8A)
) {
    Canvas(modifier = modifier) {
        val paths = SignatureUtils.decodeStrokes(signatureData, size.width, size.height)
        for (path in paths) {
            drawPath(
                path = path,
                color = strokeColor,
                style = Stroke(
                    width = 2.5.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}
