import SwiftUI

/// Tombol utama — full-width, tinggi tetap, sudut membulat, spinner inline saat loading.
struct AppPrimaryButton: View {
    let title: String
    let isLoading: Bool
    let systemImageAfter: String?
    let action: () -> Void

    init(
        title: String,
        isLoading: Bool = false,
        systemImageAfter: String? = "arrow.right",
        action: @escaping () -> Void
    ) {
        self.title = title
        self.isLoading = isLoading
        self.systemImageAfter = systemImageAfter
        self.action = action
    }

    var body: some View {
        Button(action: {
            if !isLoading {
                UIImpactFeedbackGenerator(style: .medium).impactOccurred()
                action()
            }
        }) {
            HStack(spacing: 8) {
                if isLoading {
                    ProgressView().progressViewStyle(CircularProgressViewStyle(tint: .white))
                } else {
                    Text(title).font(.headline.weight(.semibold))
                    if let systemImageAfter {
                        Image(systemName: systemImageAfter)
                            .font(.subheadline.weight(.semibold))
                    }
                }
            }
            .frame(maxWidth: .infinity)
            .frame(height: AppSizes.buttonHeight)
            .background(Color.accentColor)
            .foregroundColor(.white)
            .clipShape(RoundedRectangle(cornerRadius: AppCornerRadius.large, style: .continuous))
        }
        .disabled(isLoading)
        .buttonStyle(AppleScaleButtonStyle())
    }
}

/// Tombol sekunder — outlined-flavored, warna teks konfigurable (aksen / merah / dll).
struct AppSecondaryButton: View {
    let title: String
    let tint: Color
    let action: () -> Void

    init(title: String, tint: Color = .accentColor, action: @escaping () -> Void) {
        self.title = title
        self.tint = tint
        self.action = action
    }

    var body: some View {
        Button(action: {
            UIImpactFeedbackGenerator(style: .light).impactOccurred()
            action()
        }) {
            Text(title)
                .font(.headline.weight(.medium))
                .frame(maxWidth: .infinity)
                .frame(height: AppSizes.buttonHeight)
                .background(Color(uiColor: .secondarySystemGroupedBackground))
                .foregroundColor(tint)
                .clipShape(RoundedRectangle(cornerRadius: AppCornerRadius.large, style: .continuous))
        }
        .buttonStyle(AppleScaleButtonStyle())
    }
}
