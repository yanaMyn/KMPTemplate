import SwiftUI

/// Design tokens untuk sisi iOS. Selalu konsumsi dari sini — jangan hardcode angka di view.
enum AppSpacing {
    static let xs: CGFloat = 4
    static let sm: CGFloat = 8
    static let md: CGFloat = 12
    static let lg: CGFloat = 16
    static let xl: CGFloat = 24
    static let xxl: CGFloat = 32
}

enum AppCornerRadius {
    static let small: CGFloat = 10
    static let medium: CGFloat = 14
    static let large: CGFloat = 16
    static let xLarge: CGFloat = 24
}

enum AppSizes {
    static let buttonHeight: CGFloat = 52
    static let headerIcon: CGFloat = 88
    static let successIcon: CGFloat = 96
}

/// Interaksi native "press to scale" — sudah dipakai oleh semua tombol utama.
struct AppleScaleButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .scaleEffect(configuration.isPressed ? 0.97 : 1.0)
            .opacity(configuration.isPressed ? 0.88 : 1.0)
            .animation(.spring(response: 0.25, dampingFraction: 0.7), value: configuration.isPressed)
    }
}
