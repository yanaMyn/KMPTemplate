import SwiftUI
import SharedLogic

struct LoginView: View {
    @StateObject private var viewModel = LoginViewModel()
    var onLoginSuccess: ((User) -> Void)? = nil
    var onNavigateToRegister: (() -> Void)? = nil
    var onBack: (() -> Void)? = nil

    var body: some View {
        Group {
            if viewModel.state.isSuccess, let user = viewModel.state.loggedInUser {
                AppVerifiedSuccessView(
                    title: "Berhasil Diverifikasi",
                    message: "Sekarang kamu bisa reset PIN dan terima info terbaru dari LinkAja melalui emailmu",
                    buttonTitle: "Tutup",
                    onClose: { onLoginSuccess?(user) }
                )
                .transition(.asymmetric(
                    insertion: .scale(scale: 0.9).combined(with: .opacity),
                    removal: .opacity
                ))
            } else {
                NavigationView {
                    ZStack {
                        Color(uiColor: .systemGroupedBackground).ignoresSafeArea()

                        LoginFormSwiftUIView(
                            viewModel: viewModel,
                            onNavigateToRegister: onNavigateToRegister
                        )
                    }
                    .navigationTitle("Masuk")
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

private struct LoginFormSwiftUIView: View {
    @ObservedObject var viewModel: LoginViewModel
    var onNavigateToRegister: (() -> Void)?

    private enum FormField: Hashable { case email, password }
    @FocusState private var focusedField: FormField?

    var body: some View {
        ScrollView {
            VStack(spacing: AppSpacing.xl) {
                AppHeaderIcon(
                    systemImage: "lock.shield.fill",
                    title: "Selamat Datang Kembali",
                    subtitle: "Masuk ke akun KMPTemplate Anda"
                )

                AppErrorBanner(message: viewModel.state.generalError)

                VStack(spacing: AppSpacing.lg) {
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
                        submitLabel: .done,
                        onSubmit: { handleFormSubmission() }
                    )
                }

                AppPrimaryButton(
                    title: "Masuk",
                    isLoading: viewModel.state.isLoading,
                    action: { handleFormSubmission() }
                )
                .padding(.top, AppSpacing.xs)

                if let onNavigateToRegister {
                    Button(action: {
                        UIImpactFeedbackGenerator(style: .light).impactOccurred()
                        focusedField = nil
                        onNavigateToRegister()
                    }) {
                        Text("Belum punya akun? ")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                        + Text("Daftar")
                            .font(.subheadline.weight(.semibold))
                            .foregroundColor(.accentColor)
                    }
                    .frame(minHeight: 44)
                }

                DemoCredentialsPanel(viewModel: viewModel)
                    .padding(.top, AppSpacing.sm)
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

    private func handleFormSubmission() {
        focusedField = nil
        viewModel.onSubmit()
    }
}

private struct DemoCredentialsPanel: View {
    @ObservedObject var viewModel: LoginViewModel

    var body: some View {
        VStack(spacing: AppSpacing.md) {
            Text("Akun Demo Cepat (Klik untuk isi)")
                .font(.caption.weight(.medium))
                .foregroundColor(.secondary)

            HStack(spacing: AppSpacing.md) {
                demoButton(title: "Leanne Graham", icon: "person.badge.shield.checkmark.fill", email: "Sincere@april.biz")
                demoButton(title: "Ervin Howell", icon: "person.fill", email: "Shanna@melissa.tv")
            }
        }
        .padding(AppSpacing.lg)
        .background(Color(uiColor: .secondarySystemGroupedBackground))
        .clipShape(RoundedRectangle(cornerRadius: AppCornerRadius.large, style: .continuous))
    }

    private func demoButton(title: String, icon: String, email: String) -> some View {
        Button(action: {
            UIImpactFeedbackGenerator(style: .light).impactOccurred()
            viewModel.onEmailChange(email)
            viewModel.onPasswordChange("Password123!")
        }) {
            Label(title, systemImage: icon)
                .font(.caption.weight(.semibold))
                .frame(maxWidth: .infinity)
                .frame(height: 44)
                .background(Color.accentColor.opacity(0.12))
                .foregroundColor(.accentColor)
                .clipShape(RoundedRectangle(cornerRadius: AppCornerRadius.medium, style: .continuous))
        }
        .buttonStyle(AppleScaleButtonStyle())
    }
}
