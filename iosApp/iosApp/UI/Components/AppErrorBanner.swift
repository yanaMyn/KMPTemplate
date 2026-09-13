import SwiftUI

/// Banner error inline — muncul/menghilang otomatis mengikuti `message`.
/// `message == nil` → tidak render, tidak ambil ruang di layout.
struct AppErrorBanner: View {
    let message: String?

    var body: some View {
        if let message {
            HStack(spacing: 12) {
                Image(systemName: "exclamationmark.triangle.fill")
                    .font(.headline)
                    .foregroundColor(.red)

                Text(message)
                    .font(.subheadline.weight(.medium))
                    .foregroundColor(.red)

                Spacer()
            }
            .padding(14)
            .background(
                RoundedRectangle(cornerRadius: AppCornerRadius.medium, style: .continuous)
                    .fill(Color.red.opacity(0.12))
            )
            .transition(.move(edge: .top).combined(with: .opacity))
        }
    }
}
