import SwiftUI
import SharedLogic

struct RegisterView: View {
    @StateObject private var viewModel = RegisterViewModel()
    var onRegisterSuccess: ((User) -> Void)? = nil
    var onNavigateToLogin: (() -> Void)? = nil
    var onBack: (() -> Void)? = nil

    var body: some View {
        Group {
            if viewModel.state.isSuccess, let user = viewModel.state.registeredUser {
                AppVerifiedSuccessView(
                    title: "Berhasil Diverifikasi",
                    message: "Sekarang kamu bisa reset PIN dan terima info terbaru dari LinkAja melalui emailmu",
                    buttonTitle: "Tutup",
                    onClose: { onRegisterSuccess?(user) }
                )
                .transition(.asymmetric(
                    insertion: .scale(scale: 0.9).combined(with: .opacity),
                    removal: .opacity
                ))
            } else {
                NavigationView {
                    ZStack {
                        Color(uiColor: .systemGroupedBackground).ignoresSafeArea()

                        RegisterFormSwiftUIView(
                            viewModel: viewModel,
                            onNavigateToLogin: onNavigateToLogin
                        )
                    }
                    .navigationTitle("Daftar")
                    .navigationBarTitleDisplayMode(.inline)
                    .toolbar {
                        ToolbarItem(placement: .navigationBarLeading) {
                            if let onBack {
                                Button(action: {
                                    UIImpactFeedbackGenerator(style: .light).impactOccurred()
                                    onBack()
                                }) {
                                    HStack(spacing: 4) {
                                        Image(systemName: "chevron.left").fontWeight(.semibold)
                                        Text("Kembali")
                                    }
                                    .foregroundColor(.accentColor)
                                }
                            }
                        }
                    }
                }
                .navigationViewStyle(.stack)
                .transition(.opacity)
            }
        }
        .animation(.spring(response: 0.38, dampingFraction: 0.8), value: viewModel.state.isSuccess)
        .onChange(of: viewModel.state.isSuccess) { isSuccess in
            if isSuccess { UINotificationFeedbackGenerator().notificationOccurred(.success) }
        }
        .onChange(of: viewModel.state.generalError) { error in
            if error != nil { UINotificationFeedbackGenerator().notificationOccurred(.error) }
        }
    }
}

private struct RegisterFormSwiftUIView: View {
    @ObservedObject var viewModel: RegisterViewModel
    var onNavigateToLogin: (() -> Void)?

    private enum FormField: Hashable { case name, email, password, confirmPassword }
    @FocusState private var focusedField: FormField?

    var body: some View {
        ScrollView {
            VStack(spacing: AppSpacing.xl) {
                AppHeaderIcon(
                    systemImage: "person.badge.plus",
                    title: "Buat Akun Baru",
                    subtitle: "Daftar untuk mulai menggunakan KMPTemplate",
                    iconFontSize: 40
                )

                AppErrorBanner(message: viewModel.state.generalError)

                VStack(spacing: AppSpacing.lg) {
                    AppTextField(
                        title: "Nama Lengkap",
                        placeholder: "contoh: Budi Santoso",
                        text: Binding(
                            get: { viewModel.state.name },
                            set: { viewModel.onNameChange($0) }
                        ),
                        errorMessage: viewModel.state.nameError,
                        field: FormField.name,
                        focused: $focusedField,
                        textContentType: .name,
                        autocapitalization: .words,
                        submitLabel: .next,
                        onSubmit: { focusedField = .email }
                    )

                    AppTextField(
                        title: "Email",
                        placeholder: "nama@kmptemplate.org",
                        text: Binding(
                            get: { viewModel.state.email },
                            set: { viewModel.onEmailChange($0) }
                        ),
                        errorMessage: viewModel.state.emailError,
                        field: FormField.email,
                        focused: $focusedField,
                        keyboardType: .emailAddress,
                        textContentType: .username,
                        submitLabel: .next,
                        onSubmit: { focusedField = .password }
                    )

                    AppPasswordField(
                        title: "Kata Sandi",
                        placeholder: "Minimal 6 karakter",
                        text: Binding(
                            get: { viewModel.state.password },
                            set: { viewModel.onPasswordChange($0) }
                        ),
                        errorMessage: viewModel.state.passwordError,
                        isVisible: viewModel.state.isPasswordVisible,
                        onToggleVisibility: { viewModel.onTogglePasswordVisibility() },
                        field: FormField.password,
                        focused: $focusedField,
                        submitLabel: .next,
                        onSubmit: { focusedField = .confirmPassword }
                    )

                    AppPasswordField(
                        title: "Konfirmasi Kata Sandi",
                        placeholder: "Ulangi kata sandi",
                        text: Binding(
                            get: { viewModel.state.confirmPassword },
                            set: { viewModel.onConfirmPasswordChange($0) }
                        ),
                        errorMessage: viewModel.state.confirmPasswordError,
                        isVisible: viewModel.state.isConfirmPasswordVisible,
                        onToggleVisibility: { viewModel.onToggleConfirmPasswordVisibility() },
                        field: FormField.confirmPassword,
                        focused: $focusedField,
                        submitLabel: .done,
                        onSubmit: { handleFormSubmission() }
                    )

                    termsRow
                }

                AppPrimaryButton(
                    title: "Daftar",
                    isLoading: viewModel.state.isLoading,
                    action: { handleFormSubmission() }
                )
                .padding(.top, AppSpacing.xs)

                if let onNavigateToLogin {
                    Button(action: {
                        UIImpactFeedbackGenerator(style: .light).impactOccurred()
                        focusedField = nil
                        onNavigateToLogin()
                    }) {
                        Text("Sudah punya akun? ")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                        + Text("Masuk")
                            .font(.subheadline.weight(.semibold))
                            .foregroundColor(.accentColor)
                    }
                    .frame(minHeight: 44)
                }
            }
            .padding(.horizontal, 20)
            .padding(.vertical, AppSpacing.md)
        }
        .toolbar {
            ToolbarItemGroup(placement: .keyboard) {
                Spacer()
                Button("Selesai") { focusedField = nil }
                    .font(.body.weight(.semibold))
                    .foregroundColor(.accentColor)
            }
        }
    }

    private var termsRow: some View {
        VStack(alignment: .leading, spacing: 6) {
            Button(action: {
                UISelectionFeedbackGenerator().selectionChanged()
                viewModel.onToggleTerms()
            }) {
                HStack(spacing: AppSpacing.md) {
                    Image(systemName: viewModel.state.isTermsAccepted ? "checkmark.square.fill" : "square")
                        .font(.system(size: 22))
                        .foregroundColor(viewModel.state.isTermsAccepted ? .accentColor : .secondary)

                    Text("Saya menyetujui Syarat & Ketentuan")
                        .font(.subheadline)
                        .foregroundColor(.primary)
                        .multilineTextAlignment(.leading)

                    Spacer()
                }
                .frame(minHeight: 44)
                .contentShape(Rectangle())
            }
            .buttonStyle(AppleScaleButtonStyle())

            if let termsError = viewModel.state.termsError {
                Text(termsError)
                    .font(.caption)
                    .foregroundColor(.red)
                    .transition(.opacity)
            }
        }
    }

    private func handleFormSubmission() {
        focusedField = nil
        viewModel.onSubmit()
    }
}
