import SwiftUI

/// Header dengan ikon bulat + title + subtitle opsional. Dipakai di puncak layar
/// Login / Register / walkthrough completed.
struct AppHeaderIcon: View {
    let systemImage: String
    let title: String
    let subtitle: String?

    var iconSize: CGFloat = AppSizes.headerIcon
    var iconFontSize: CGFloat = 42

    init(
        systemImage: String,
        title: String,
        subtitle: String? = nil,
        iconSize: CGFloat = AppSizes.headerIcon,
        iconFontSize: CGFloat = 42
    ) {
        self.systemImage = systemImage
        self.title = title
        self.subtitle = subtitle
        self.iconSize = iconSize
        self.iconFontSize = iconFontSize
    }

    var body: some View {
        VStack(spacing: AppSpacing.md) {
            ZStack {
                Circle()
                    .fill(Color.accentColor.opacity(0.12))
                    .frame(width: iconSize, height: iconSize)

                Image(systemName: systemImage)
                    .font(.system(size: iconFontSize, weight: .semibold))
                    .foregroundColor(.accentColor)
            }
            .padding(.top, AppSpacing.md)

            VStack(spacing: AppSpacing.xs) {
                Text(title)
                    .font(.title2.weight(.bold))
                    .foregroundColor(.primary)

                if let subtitle {
                    Text(subtitle)
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                        .multilineTextAlignment(.center)
                }
            }
        }
    }
}
