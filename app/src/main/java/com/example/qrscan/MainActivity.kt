package com.example.qrscan

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.qrscan.ui.theme.QrScanTheme
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning


class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val options = GmsBarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .enableAutoZoom()
                .build()
            var scannedText by remember {
                mutableStateOf("")
            }
            val clipboardManager = LocalClipboardManager.current

            val scanner = GmsBarcodeScanning.getClient(this)
            val windowInsets = WindowInsets.Companion.statusBars

            QrScanTheme {
                Scaffold(modifier = Modifier.windowInsetsPadding(windowInsets).fillMaxSize(), floatingActionButton = {
                    FloatingActionButton(onClick = {
                        scanner.startScan()
                            .addOnSuccessListener { barcode ->
                                Toast.makeText(
                                    applicationContext,
                                    "Scanned SuccessFully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                scannedText = barcode.rawValue.toString()
                            }
                            .addOnCanceledListener {
                                Toast.makeText(
                                    applicationContext,
                                    "Scan Cancelled",
                                    Toast.LENGTH_SHORT
                                ).show()
                                scannedText = ""
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    applicationContext,
                                    "Error : ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                scannedText = ""
                            }
                    }, containerColor =Color.Black) {
           Icon(painter = painterResource(R.drawable.baseline_qr_code_scanner_24),
               contentDescription = null, tint = Color.White)
                    }
                }) {

                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(Modifier.fillMaxWidth(0.9f).fillMaxHeight(0.1f)) {
                            Text(
                                "QrScan",
                                fontSize = MaterialTheme.typography.displaySmall.fontSize,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
Column(Modifier.fillMaxSize() ,verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(scannedText)
                        Button(
                            onClick = {
                                clipboardManager.setText(
                                    annotatedString = AnnotatedString(
                                        scannedText
                                    )
                                )
                            }, modifier = Modifier
                                .clip(RoundedCornerShape(15f))
                                .fillMaxWidth(0.6f),
                            colors = ButtonDefaults.buttonColors(Color.Black)
                        ) {
                            Text("Copy", color = Color.White)
                        }
                        Button(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(scannedText))
                                    startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Searching on Google",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q="+scannedText))
                                    startActivity(intent)
                                }
                            }, modifier = Modifier
                                .clip(RoundedCornerShape(15f))
                                .fillMaxWidth(0.6f),
                            colors = ButtonDefaults.buttonColors(Color.Black)
                        ) {
                            Text("Search on web", color = Color.White)
                        }
    Button(
        onClick = {
            try {
                if (scannedText ==""){
                    Toast.makeText(
                        applicationContext,
                        "Empty text cannot be shared",
                        Toast.LENGTH_SHORT
                    ).show()
                }else{

                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, scannedText)
                }
                    startActivity(intent)
                }
            } catch (e: Exception) {
                Toast.makeText(
                    applicationContext,
                    "Sharing failed",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }, modifier = Modifier
            .clip(RoundedCornerShape(15f))
            .fillMaxWidth(0.6f),
        colors = ButtonDefaults.buttonColors(Color.Black)
    ) {
        Text("Share text", color = Color.White)
    }

                    }
                    }
                }
            }
        }
    }
}

