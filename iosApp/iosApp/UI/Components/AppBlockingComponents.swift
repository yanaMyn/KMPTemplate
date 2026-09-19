//
//  AppBlockingComponents.swift
//
//  Created on 9/19/26.
//

import SwiftUI

/// Title text layar "Blocking" — warna & ukuran literal dari token desain Figma.
struct AppBlockingTitle: View {
    let text: String

    var body: some View {
        Text(text)
            .font(.system(size: 18, weight: .bold))
            .foregroundColor(Color(red: 0x00 / 255, green: 0x11 / 255, blue: 0x22 / 255))
            .multilineTextAlignment(.center)
            .frame(maxWidth: .infinity)
            .padding(.horizontal, 30)
    }
}

/// Body text layar "Blocking" — warna & ukuran literal dari token desain Figma.
struct AppBlockingMessage: View {
    let text: String

    var body: some View {
        Text(text)
            .font(.system(size: 12))
            .lineSpacing(6)
            .foregroundColor(Color(red: 0x29 / 255, green: 0x31 / 255, blue: 0x42 / 255))
            .multilineTextAlignment(.center)
            .frame(maxWidth: .infinity)
            .padding(.horizontal, 30)
    }
}

/// Tombol utama merah full-width layar "Blocking" (warna literal `#E3292B`, bukan
/// `AppPrimaryButton` — desain ini sengaja tidak memakai accent color aplikasi).
struct AppBlockingPrimaryButton: View {
    let title: String
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(title)
                .font(.system(size: 14, weight: .bold))
                .foregroundColor(.white)
                .frame(maxWidth: .infinity)
                .frame(height: 48)
        }
        .background(Color(red: 0xE3 / 255, green: 0x29 / 255, blue: 0x2B / 255))
        .clipShape(RoundedRectangle(cornerRadius: 24, style: .continuous))
        .padding(.horizontal, 16)
        .padding(.bottom, 16)
    }
}

/// Tombol close (X) pojok kiri atas layar "Blocking".
struct AppBlockingCloseButton: View {
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Image(systemName: "xmark")
                .font(.system(size: 16, weight: .semibold))
                .foregroundColor(Color(red: 0x00 / 255, green: 0x11 / 255, blue: 0x22 / 255))
                .frame(width: 24, height: 24)
        }
        .padding(.leading, 16)
        .padding(.top, 16)
    }
}
