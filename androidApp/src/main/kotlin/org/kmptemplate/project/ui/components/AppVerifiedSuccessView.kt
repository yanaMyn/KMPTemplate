package org.kmptemplate.project.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.kmptemplate.project.R

/**
 * Layar "Blocking" (verifikasi/hasil) sesuai desain Figma — ilustrasi 360x360, tombol
 * back di App Bar, title + body, satu tombol merah penuh di bawah. Dipakai untuk hasil
 * login/register.
 *
 * Warna & tipe diambil literal dari token desain Figma (bukan `MaterialTheme`) supaya
 * screen ini cocok pixel-untuk-pixel dengan referensinya.
 */
@Composable
fun AppVerifiedSuccessView(
    title: String,
    body: String,
    buttonText: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(R.drawable.img_verified_success),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(21.dp))
            Text(
                text = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                color = Color(0xFF001122),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = body,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                color = Color(0xFF293142),
                fontSize = 12.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.weight(1f))
            Button(
                onClick = onClose,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE3292B))
            ) {
                Text(
                    text = buttonText,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        IconButton(
            onClick = onClose,
            modifier = Modifier
                .safeContentPadding()
                .padding(top = 16.dp, start = 16.dp)
                .align(Alignment.TopStart)
                .size(24.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = "Tutup",
                tint = Color.Unspecified
            )
        }
    }
}
