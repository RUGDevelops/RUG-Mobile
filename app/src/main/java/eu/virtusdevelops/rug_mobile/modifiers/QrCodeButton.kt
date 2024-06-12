package eu.virtusdevelops.rug_mobile.modifiers

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TabRowDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.virtusdevelops.rug_mobile.R
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanQRCode
import kotlin.reflect.KProperty

@Composable
fun QrCodeButton() {
    var packageHolderId by remember { mutableIntStateOf(0) }
    val scanQRCodeLauncher = rememberLauncherForActivityResult(ScanQRCode()) { result ->
        result.let {
            var qrData = result.toString()
            if(result is QRResult.QRUserCanceled) return@let

            Log.d("RESULT_DATA", qrData)
            packageHolderId = parseQrResult(qrData) ?: 0
        }
    }

    val modifier = Modifier
        .padding(2.dp)
        .fillMaxWidth()

    Box(
        modifier = Modifier
            //.weight(0.2f)
            //.align(Alignment.CenterVertically)
    ) {
        IconButton(
            onClick = {
                scanQRCodeLauncher.launch(null)
            },
            modifier = modifier
                // .align(Alignment.CenterVertically)
                .fillMaxHeight()
                //.weight(0.2f)
                .background(
                    TextFieldDefaults.colors().unfocusedContainerColor,
                    RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp)
                ),
            content = {
                Icon(
                    modifier = Modifier.padding(10.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.qrcode_solid),
                    contentDescription = "qr code scanner"
                )
            },
        )
        TabRowDefaults.Divider(
            color = Color.Black,
            thickness = 1.dp,
            modifier = modifier
                .align(Alignment.BottomCenter)
        )
    }
}

fun parseQrResult(qrData: String) : Int? {
    if(qrData.isEmpty()) return null
    val rawValueStartIndex = qrData.indexOf("rawValue=") + "rawValue=".length
    val rawValueEndIndex = qrData.indexOf(")", startIndex = rawValueStartIndex)
    val rawValue = qrData.substring(rawValueStartIndex, rawValueEndIndex)

    val components = rawValue.split(",")

    if (components.size == 3) {
        val url = components[0]
        val urlComponents = url.split("/")

        if (urlComponents.size >= 11) {
            val packageHolderId = urlComponents[4].toIntOrNull() ?: 0
            return packageHolderId
        } else {
            Log.d("QR_ERROR", "Failed to parse QR code data: $qrData")
        }
    } else {
        Log.d("QR_ERROR", "Failed to parse QR code data: $qrData")
    }
    return null
}

@Preview
@Composable
fun QrCodeButtonPreview() {
    QrCodeButton()
}

