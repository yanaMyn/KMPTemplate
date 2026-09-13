import SwiftUI

/// Password field dengan toggle eye/eye-slash. `isVisible` di-hoist ke caller (bukan @State
/// internal) supaya state dimiliki store dan bertahan config change / navigation.
struct AppPasswordField<Field: Hashable>: View {
    let title: String
    let placeholder: String
    @Binding var text: String
    let errorMessage: String?
    let isVisible: Bool
    let onToggleVisibility: () -> Void
    let field: Field
    let focused: FocusState<Field?>.Binding

    var submitLabel: SubmitLabel = .done
    var onSubmit: () -> Void = {}

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(title)
                .font(.caption.weight(.semibold))
                .foregroundColor(.secondary)

            HStack(spacing: 8) {
                Group {
                    if isVisible {
                        TextField(placeholder, text: $text)
                    } else {
                        SecureField(placeholder, text: $text)
                    }
                }
                .textContentType(.password)
                .textInputAutocapitalization(.never)
                .disableAutocorrection(true)
                .focused(focused, equals: field)
                .submitLabel(submitLabel)
                .onSubmit(onSubmit)

                Button(action: {
                    UIImpactFeedbackGenerator(style: .light).impactOccurred()
                    onToggleVisibility()
                }) {
                    Image(systemName: isVisible ? "eye.slash.fill" : "eye.fill")
                        .font(.system(size: 18))
                        .foregroundColor(.secondary)
                        .frame(width: 44, height: 44)
                        .contentShape(Rectangle())
                }
            }
            .padding(.horizontal, AppSpacing.lg)
            .padding(.vertical, 14)
            .background(Color(uiColor: .secondarySystemGroupedBackground))
            .cornerRadius(AppCornerRadius.medium)
            .overlay(
                RoundedRectangle(cornerRadius: AppCornerRadius.medium, style: .continuous)
                    .stroke(
                        errorMessage != nil ? Color.red :
                            (focused.wrappedValue == field ? Color.accentColor : Color(uiColor: .separator).opacity(0.6)),
                        lineWidth: focused.wrappedValue == field || errorMessage != nil ? 1.5 : 1
                    )
            )

            if let error = errorMessage {
                Text(error)
                    .font(.caption)
                    .foregroundColor(.red)
                    .transition(.opacity)
            }
        }
    }
}
