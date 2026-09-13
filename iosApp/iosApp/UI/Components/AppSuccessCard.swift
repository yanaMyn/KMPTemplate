import SwiftUI

/// Kartu sukses — ikon check bulat + title/subtitle/caption + pasangan tombol primer + sekunder.
/// Digunakan di layar hasil (post-login, post-register, walkthrough completed).
struct AppSuccessCard: View {
    let systemImage: String
    let title: String
    let subtitle: String
    let caption: String
    let primaryTitle: String
    let onPrimary: () -> Void
    let secondaryTitle: String
    let secondaryTint: Color
    let onSecondary: () -> Void

    init(
        systemImage: String = "checkmark.circle.fill",
        title: String,
        subtitle: String,
        caption: String,
        primaryTitle: String,
        onPrimary: @escaping () -> Void,
        secondaryTitle: String,
        secondaryTint: Color = .red,
        onSecondary: @escaping () -> Void
    ) {
        self.systemImage = systemImage
        self.title = title
        self.subtitle = subtitle
        self.caption = caption
        self.primaryTitle = primaryTitle
        self.onPrimary = onPrimary
        self.secondaryTitle = secondaryTitle
        self.secondaryTint = secondaryTint
        self.onSecondary = onSecondary
    }

    var body: some View {
        VStack(spacing: AppSpacing.xl) {
            ZStack {
                Circle()
                    .fill(Color.green.opacity(0.15))
                    .frame(width: AppSizes.successIcon, height: AppSizes.successIcon)

                Image(systemName: systemImage)
                    .font(.system(size: 52, weight: .medium))
                    .foregroundColor(.green)
            }
            .padding(.top, AppSpacing.sm)

            VStack(spacing: AppSpacing.sm) {
                Text(title)
                    .font(.title2.weight(.bold))
                    .foregroundColor(.primary)

                Text(subtitle)
                    .font(.headline)
                    .foregroundColor(.accentColor)

                Text(caption)
                    .font(.subheadline)
                    .foregroundColor(.secondary)
            }

            VStack(spacing: AppSpacing.md) {
                AppPrimaryButton(title: primaryTitle, systemImageAfter: nil, action: onPrimary)
                AppSecondaryButton(title: secondaryTitle, tint: secondaryTint, action: onSecondary)
            }
            .padding(.top, AppSpacing.md)
        }
        .padding(AppSpacing.xl + AppSpacing.xs)
        .background(Color(uiColor: .systemBackground))
        .clipShape(RoundedRectangle(cornerRadius: AppCornerRadius.xLarge, style: .continuous))
        .shadow(color: Color.black.opacity(0.06), radius: 16, x: 0, y: 6)
        .padding(.horizontal, AppSpacing.xl)
    }
}
