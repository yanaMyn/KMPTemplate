import SwiftUI

/// Text field standar aplikasi:
/// - label kecil di atas, input rounded card, border adaptif (fokus / error), error inline di bawah.
/// - `Field` adalah tipe generik agar caller memakai enum FocusState-nya sendiri.
struct AppTextField<Field: Hashable>: View {
    let title: String
    let placeholder: String
    @Binding var text: String
    let errorMessage: String?
    let field: Field
    let focused: FocusState<Field?>.Binding

    var keyboardType: UIKeyboardType = .default
    var textContentType: UITextContentType? = nil
    var autocapitalization: TextInputAutocapitalization = .never
    var submitLabel: SubmitLabel = .next
    var onSubmit: () -> Void = {}

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(title)
                .font(.caption.weight(.semibold))
                .foregroundColor(.secondary)

            TextField(placeholder, text: $text)
                .keyboardType(keyboardType)
                .textContentType(textContentType)
                .textInputAutocapitalization(autocapitalization)
                .disableAutocorrection(true)
                .focused(focused, equals: field)
                .submitLabel(submitLabel)
                .onSubmit(onSubmit)
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
