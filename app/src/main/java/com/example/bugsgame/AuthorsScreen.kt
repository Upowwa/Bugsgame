package com.example.bugsgame

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.bugsgame.ui.theme.Green

@Composable
fun AuthorsScreen() {
    val borderColor = MaterialTheme.colorScheme.primary
    val textColor = Green

    val authors = listOf(
        Pair("Фролова Анастасия", R.drawable.anastasia),
        Pair("Гетинг Дарья", R.drawable.daria)
    )

    var selectedPhoto by remember {
        mutableStateOf<Int?>(null)
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            ListView(context).apply {

                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                setPadding(0, 0, 0, 0)

                adapter = object : BaseAdapter() {

                    override fun getCount(): Int {
                        return authors.size
                    }

                    override fun getItem(position: Int): Pair<String, Int> {
                        return authors[position]
                    }

                    override fun getItemId(position: Int): Long {
                        return position.toLong()
                    }

                    override fun getView(
                        position: Int,
                        convertView: View?,
                        parent: ViewGroup
                    ): View {

                        val layout = LinearLayout(context).apply {

                            layoutParams = AbsListView.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )

                            orientation = LinearLayout.VERTICAL
                            gravity = Gravity.CENTER_HORIZONTAL

                            setPadding(0, dp(40), 0, 10)

                            alpha = 0f
                            translationY = dp(30).toFloat()

                            animate()
                                .alpha(1f)
                                .translationY(0f)
                                .setDuration(600)
                                .setStartDelay((position * 200).toLong())
                                .start()
                        }

                        val imageContainer = FrameLayout(context).apply {

                            layoutParams = LinearLayout.LayoutParams(
                                dp(250),
                                dp(250)
                            ).apply {
                                gravity = Gravity.CENTER_HORIZONTAL
                            }

                            setPadding(dp(7), dp(7), dp(7), dp(7))

                            background = GradientDrawable().apply {
                                setColor(borderColor.toArgb())
                                cornerRadius = dp(28).toFloat()
                            }
                        }

                        val image = ImageView(context).apply {
                            layoutParams = FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT
                            )

                            scaleType = ImageView.ScaleType.CENTER_CROP
                            setImageResource(authors[position].second)

                            background = GradientDrawable().apply {
                                cornerRadius = dp(20).toFloat()
                            }

                            clipToOutline = true
                            contentDescription = "Фото ${authors[position].first}"
                            setOnClickListener { selectedPhoto = authors[position].second }
                        }

                        imageContainer.addView(image)

                        val name = TextView(context).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            ).apply {
                                topMargin = dp(18)
                            }

                            text = authors[position].first
                            textSize = 22f
                            setTextColor(textColor.toArgb())
                            setTypeface(null, android.graphics.Typeface.BOLD)
                            gravity = Gravity.CENTER
                            textAlignment = View.TEXT_ALIGNMENT_CENTER
                        }

                        layout.addView(imageContainer)
                        layout.addView(name)

                        return layout
                    }
                }
            }
        }
    )

    selectedPhoto?.let { photo ->
        Dialog(
            onDismissRequest = {
                selectedPhoto = null
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Color.Transparent
                    )
            ) {
                ImageView(
                    photo = photo,
                    onClose = {
                        selectedPhoto = null
                    }
                )
            }
        }
    }
}

@Composable
private fun ImageView(
    photo: Int,
    onClose: () -> Unit
) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            ImageView(context).apply {

                setImageResource(photo)
                scaleType = ImageView.ScaleType.FIT_CENTER
                setBackgroundColor(Color.TRANSPARENT)

                setOnClickListener {
                    onClose()
                }
            }
        }
    )
}

private fun dp(value: Int): Int {
    return (value * android.content.res.Resources
        .getSystem()
        .displayMetrics
        .density).toInt()
}

