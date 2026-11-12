package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChatMessage
import com.example.ui.theme.DarkText
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.FreshLime
import com.example.ui.theme.FreshLimeLight
import com.example.ui.theme.SoftCream
import com.example.util.AppLanguage
import com.example.util.Localization

@Composable
fun VoiceAssistantScreen(
    language: AppLanguage,
    messages: List<ChatMessage>,
    streamingText: String,
    isResponding: Boolean,
    onSendMessage: (String) -> Unit,
    onSpeakText: (String) -> Unit,
    onStopSpeak: () -> Unit,
    onOpenLangLocation: () -> Unit = {}
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Android Speech to Text Launcher
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                inputText = spokenText
                onSendMessage(spokenText)
            }
        }
    }

    // Scroll to latest message
    LaunchedEffect(messages.size, streamingText) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val presetPrompts = when (language) {
        AppLanguage.GUJARATI -> listOf(
            "કપાસમાં ગુલાબી ઈયળથી કેવી રીતે બચવું?",
            "મગફળીમાં ટિક્કા રોગના દેશી ઉપાયો",
            "ઘઉંમાં ગેરુ રોગનું નિયંત્રણ",
            "લીંબોળીનું અર્ક કેવી રીતે બનાવવું?"
        )
        AppLanguage.HINDI -> listOf(
            "कपास में गुलाबी सुंडी का नियंत्रण कैसे करें?",
            "मूंगफली में टिक्का रोग के जैविक उपचार",
            "गेहूं में रतुआ रोग से बचाव",
            "नीम का काढ़ा कैसे तैयार करें?"
        )
        AppLanguage.MARATHI -> listOf(
            "कपाशीवरील बोंडअळीचे नियंत्रण कसे करावे?",
            "सोयाबीनवरील कीड व्यवस्थापन उपाय",
            "सेंद्रिय कीटकनाशक कसे तयार करावे?"
        )
        else -> listOf(
            "How to protect cotton from pink bollworm?",
            "Organic control for crop diseases?",
            "Preventive measures for leaf rust?",
            "How to prepare neem botanical extract?"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftCream)
            .padding(16.dp)
    ) {
        // Header
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = ForestGreen),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(FreshLime),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Mic, contentDescription = null, tint = ForestGreen)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = Localization.get("voice_title", language),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = Localization.get("voice_subtitle", language),
                                style = MaterialTheme.typography.bodySmall.copy(color = FreshLimeLight)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = FreshLime,
                        modifier = Modifier.clickable { onOpenLangLocation() }
                    ) {
                        Text(
                            text = "${language.nativeName} ✎",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = ForestGreen
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                // Voice Readiness Indicator
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (language.hasTts && language.hasStt) ForestGreen.copy(alpha = 0.6f) else Color(0xFF5D4037)
                ) {
                    Text(
                        text = if (language.hasTts && language.hasStt)
                            "✓ ${language.nativeName}: ${Localization.get("voice_available", language)}"
                        else
                            "ℹ ${language.nativeName}: ${Localization.get("voice_limited", language)}",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontSize = 10.sp),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Preset Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(presetPrompts) { prompt ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onSendMessage(prompt) }
                ) {
                    Text(
                        text = prompt,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = ForestGreen,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Chat Message List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg ->
                ChatBubble(
                    message = msg,
                    onSpeak = { onSpeakText(msg.text) }
                )
            }

            if (isResponding && streamingText.isNotEmpty()) {
                item {
                    ChatBubble(
                        message = ChatMessage("stream", isUser = false, text = streamingText),
                        onSpeak = {}
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Input Controls Row
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mic Button for Android Speech Recognition
                IconButton(
                    onClick = {
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE, if (language == AppLanguage.GUJARATI) "gu-IN" else if (language == AppLanguage.HINDI) "hi-IN" else "en-IN")
                            putExtra(RecognizerIntent.EXTRA_PROMPT, Localization.get("speech_listening", language))
                        }
                        try {
                            speechLauncher.launch(intent)
                        } catch (e: Exception) {
                            // Speech recognizer not installed on emulator, fallback gracefully
                        }
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .background(FreshLimeLight, CircleShape)
                        .testTag("voice_mic_button")
                ) {
                    Icon(imageVector = Icons.Default.Mic, contentDescription = "Speak", tint = ForestGreen)
                }

                Spacer(modifier = Modifier.width(6.dp))

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text(Localization.get("voice_type_hint", language), fontSize = 12.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("assistant_query_input"),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    maxLines = 2
                )

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            onSendMessage(inputText)
                            inputText = ""
                        }
                    },
                    enabled = inputText.isNotBlank() && !isResponding,
                    modifier = Modifier
                        .size(42.dp)
                        .background(if (inputText.isNotBlank()) ForestGreen else Color.LightGray, CircleShape)
                        .testTag("send_query_button")
                ) {
                    if (isResponding) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(
    message: ChatMessage,
    onSpeak: () -> Unit
) {
    val isUser = message.isUser
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = if (isUser) ForestGreen else Color.White,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (isUser) Color.White else DarkText,
                        lineHeight = 22.sp
                    )
                )

                if (!isUser) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onSpeak,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Listen Audio",
                                tint = ForestGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
