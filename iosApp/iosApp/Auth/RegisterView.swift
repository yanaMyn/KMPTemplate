import SwiftUI
import SharedLogic

struct RegisterView: View {
    @StateObject private var viewModel = RegisterViewModel()
    var onRegisterSuccess: ((User) -> Void)? = nil
    var onNavigateToLogin: (() -> Void)? = nil
    var onBack: (() -> Void)? = nil

    var body: some View {
        NavigationView {
            ZStack {
                Color(uiColor: .systemGroupedBackground)
                    .ignoresSafeArea()

                if viewModel.state.isSuccess, let user = viewModel.state.registeredUser {
                    RegisterSuccessSwiftUIView(
                        user: user,
                        onContinue: {
                            UIImpactFeedbackGenerator(style: .medium).impactOccurred()
                            onRegisterSuccess?(user)
                        },
                        onRegisterAgain: {
                            UIImpactFeedbackGenerator(style: .light).impactOccurred()
                            viewModel.onReset()
                        }
                    )
                    .transition(.asymmetric(
                        insertion: .scale(scale: 0.9).combined(with: .opacity),
                        removal: .opacity
                    ))
                } else {
                    RegisterFormSwiftUIView(
                        viewModel: viewModel,
                        onNavigateToLogin: onNavigateToLogin
                    )
                    .transition(.opacity)
                }
            }
            .navigationTitle("Daftar")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    if let onBack = onBack {
                        Button(action: {
                            UIImpactFeedbackGenerator(style: .light).impactOccurred()
                            onBack()
                        }) {
                            HStack(spacing: 4) {
                                Image(systemName: "chevron.left")
                                    .fontWeight(.semibold)
                                Text("Kembali")
                            }
                            .foregroundColor(.accentColor)
                        }
                    }
                }
            }
        }
        .navigationViewStyle(.stack)
        .animation(.spring(response: 0.38, dampingFraction: 0.8), value: viewModel.state.isSuccess)
        .onChange(of: viewModel.state.isSuccess) { isSuccess in
            if isSuccess {
                UINotificationFeedbackGenerator().notificationOccurred(.success)
            }
        }
        .onChange(of: viewModel.state.generalError) { error in
            if error != nil {
                UINotificationFeedbackGenerator().notificationOccurred(.error)
            }
        }
    }
}

private struct RegisterFormSwiftUIView: View {
    @ObservedObject var viewModel: RegisterViewModel
    var onNavigateToLogin: (() -> Void)?

    private enum FormField: Hashable {
        case name
        case email
        case password
        case confirmPassword
    }

    @FocusState private var focusedField: FormField?

    var body: some View {
        ScrollView {
            VStack(spacing: 24) {
                headerSection

                if let generalError = viewModel.state.generalError {
                    errorBanner(message: generalError)
                }

                VStack(spacing: 16) {
                    nameField
                    emailField
                    passwordField
                    confirmPasswordField
                    termsRow
                }

                submitButton

                loginLink
            }
            .padding(.horizontal, 20)
            .padding(.vertical, 12)
        }
        .toolbar {
            ToolbarItemGroup(placement: .keyboard) {
                Spacer()
                Button("Selesai") {
                    focusedField = nil
                }
                .font(.body.weight(.semibold))
                .foregroundColor(.accentColor)
            }
        }
    }

    // MARK: - Header

    private var headerSection: some View {
        VStack(spacing: 12) {
            ZStack {
                Circle()
                    .fill(Color.accentColor.opacity(0.12))
                    .frame(width: 88, height: 88)

                Image(systemName: "person.badge.plus")
                    .font(.system(size: 40, weight: .semibold))
                    .foregroundColor(.accentColor)
            }
            .padding(.top, 12)

            VStack(spacing: 4) {
                Text("Buat Akun Baru")
                    .font(.title2.weight(.bold))
                    .foregroundColor(.primary)

                Text("Daftar untuk mulai menggunakan KMPTemplate")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                    .multilineTextAlignment(.center)
            }
        }
    }

    private func errorBanner(message: String) -> some View {
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
            RoundedRectangle(cornerRadius: 14, style: .continuous)
                .fill(Color.red.opacity(0.12))
        )
        .transition(.move(edge: .top).combined(with: .opacity))
    }

    // MARK: - Fields

    private var nameField: some View {
        fieldContainer(title: "Nama Lengkap", error: viewModel.state.nameError, field: .name) {
            TextField("contoh: Budi Santoso", text: Binding(
                get: { viewModel.state.name },
                set: { viewModel.onNameChange($0) }
            ))
            .textContentType(.name)
            .textInputAutocapitalization(.words)
            .focused($focusedField, equals: .name)
            .submitLabel(.next)
            .onSubmit {
                focusedField = .email
            }
        }
    }

    private var emailField: some View {
        fieldContainer(title: "Email", error: viewModel.state.emailError, field: .email) {
            TextField("nama@kmptemplate.org", text: Binding(
                get: { viewModel.state.email },
                set: { viewModel.onEmailChange($0) }
            ))
            .textContentType(.username)
            .keyboardType(.emailAddress)
            .textInputAutocapitalization(.never)
            .disableAutocorrection(true)
            .focused($focusedField, equals: .email)
            .submitLabel(.next)
            .onSubmit {
                focusedField = .password
            }
        }
    }

    private var passwordField: some View {
        fieldContainer(title: "Kata Sandi", error: viewModel.state.passwordError, field: .password) {
            HStack(spacing: 8) {
                Group {
                    if viewModel.state.isPasswordVisible {
                        TextField("Minimal 6 karakter", text: passwordBinding)
                    } else {
                        SecureField("Minimal 6 karakter", text: passwordBinding)
                    }
                }
                .textContentType(.newPassword)
                .textInputAutocapitalization(.never)
                .disableAutocorrection(true)
                .focused($focusedField, equals: .password)
                .submitLabel(.next)
                .onSubmit {
                    focusedField = .confirmPassword
                }

                visibilityToggle(
                    isVisible: viewModel.state.isPasswordVisible,
                    action: { viewModel.onTogglePasswordVisibility() }
                )
            }
        }
    }

    private var confirmPasswordField: some View {
        fieldContainer(
            title: "Konfirmasi Kata Sandi",
            error: viewModel.state.confirmPasswordError,
            field: .confirmPassword
        ) {
            HStack(spacing: 8) {
                Group {
                    if viewModel.state.isConfirmPasswordVisible {
                        TextField("Ulangi kata sandi", text: confirmPasswordBinding)
                    } else {
                        SecureField("Ulangi kata sandi", text: confirmPasswordBinding)
                    }
                }
                .textContentType(.newPassword)
                .textInputAutocapitalization(.never)
                .disableAutocorrection(true)
                .focused($focusedField, equals: .confirmPassword)
                .submitLabel(.done)
                .onSubmit {
                    handleFormSubmission()
                }

                visibilityToggle(
                    isVisible: viewModel.state.isConfirmPasswordVisible,
                    action: { viewModel.onToggleConfirmPasswordVisibility() }
                )
            }
        }
    }

    private var passwordBinding: Binding<String> {
        Binding(
            get: { viewModel.state.password },
            set: { viewModel.onPasswordChange($0) }
        )
    }

    private var confirmPasswordBinding: Binding<String> {
        Binding(
            get: { viewModel.state.confirmPassword },
            set: { viewModel.onConfirmPasswordChange($0) }
        )
    }

    private func visibilityToggle(isVisible: Bool, action: @escaping () -> Void) -> some View {
        Button(action: {
            UIImpactFeedbackGenerator(style: .light).impactOccurred()
            action()
        }) {
            Image(systemName: isVisible ? "eye.slash.fill" : "eye.fill")
                .font(.system(size: 18))
                .foregroundColor(.secondary)
                .frame(width: 44, height: 44)
                .contentShape(Rectangle())
        }
    }

    /// Wadah field dengan label, border adaptif (fokus / error), dan pesan error inline.
    private func fieldContainer<Content: View>(
        title: String,
        error: String?,
        field: FormField,
        @ViewBuilder content: () -> Content
    ) -> some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(title)
                .font(.caption.weight(.semibold))
                .foregroundColor(.secondary)

            content()
                .padding(.horizontal, 16)
                .padding(.vertical, 14)
                .background(Color(uiColor: .secondarySystemGroupedBackground))
                .cornerRadius(14)
                .overlay(
                    RoundedRectangle(cornerRadius: 14, style: .continuous)
                        .stroke(
                            error != nil ? Color.red :
                                (focusedField == field ? Color.accentColor : Color(uiColor: .separator).opacity(0.6)),
                            lineWidth: focusedField == field || error != nil ? 1.5 : 1
                        )
                )

            if let error = error {
                Text(error)
                    .font(.caption)
                    .foregroundColor(.red)
                    .transition(.opacity)
            }
        }
    }

    // MARK: - Terms & Conditions

    private var termsRow: some View {
        VStack(alignment: .leading, spacing: 6) {
            Button(action: {
                UISelectionFeedbackGenerator().selectionChanged()
                viewModel.onToggleTerms()
            }) {
                HStack(spacing: 12) {
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

    // MARK: - Actions

    private var submitButton: some View {
        Button(action: {
            handleFormSubmission()
        }) {
            HStack(spacing: 8) {
                if viewModel.state.isLoading {
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle(tint: .white))
                } else {
                    Text("Daftar")
                        .font(.headline.weight(.semibold))
                    Image(systemName: "arrow.right")
                        .font(.subheadline.weight(.semibold))
                }
            }
            .frame(maxWidth: .infinity)
            .frame(height: 52)
            .background(Color.accentColor)
            .foregroundColor(.white)
            .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
        }
        .disabled(viewModel.state.isLoading)
        .buttonStyle(AppleScaleButtonStyle())
        .padding(.top, 4)
    }

    @ViewBuilder
    private var loginLink: some View {
        if let onNavigateToLogin = onNavigateToLogin {
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

    private func handleFormSubmission() {
        focusedField = nil
        UIImpactFeedbackGenerator(style: .medium).impactOccurred()
        viewModel.onSubmit()
    }
}

private struct RegisterSuccessSwiftUIView: View {
    let user: User
    var onContinue: () -> Void
    var onRegisterAgain: () -> Void

    var body: some View {
        VStack(spacing: 24) {
            ZStack {
                Circle()
                    .fill(Color.green.opacity(0.15))
                    .frame(width: 96, height: 96)

                Image(systemName: "checkmark.circle.fill")
                    .font(.system(size: 52, weight: .medium))
                    .foregroundColor(.green)
            }
            .padding(.top, 8)

            VStack(spacing: 8) {
                Text("Pendaftaran Berhasil!")
                    .font(.title2.weight(.bold))
                    .foregroundColor(.primary)

                Text("Selamat datang, \(user.name)")
                    .font(.headline)
                    .foregroundColor(.accentColor)

                Text("Email: \(user.email)")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
            }

            VStack(spacing: 12) {
                Button(action: onContinue) {
                    Text("Lanjutkan ke Aplikasi")
                        .font(.headline.weight(.semibold))
                        .frame(maxWidth: .infinity)
                        .frame(height: 52)
                        .background(Color.accentColor)
                        .foregroundColor(.white)
                        .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
                }
                .buttonStyle(AppleScaleButtonStyle())

                Button(action: onRegisterAgain) {
                    Text("Daftar Akun Lain")
                        .font(.headline.weight(.medium))
                        .frame(maxWidth: .infinity)
                        .frame(height: 52)
                        .background(Color(uiColor: .secondarySystemGroupedBackground))
                        .foregroundColor(.accentColor)
                        .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
                }
                .buttonStyle(AppleScaleButtonStyle())
            }
            .padding(.top, 12)
        }
        .padding(28)
        .background(Color(uiColor: .systemBackground))
        .clipShape(RoundedRectangle(cornerRadius: 24, style: .continuous))
        .shadow(color: Color.black.opacity(0.06), radius: 16, x: 0, y: 6)
        .padding(.horizontal, 24)
    }
}

struct RegisterView_Previews: PreviewProvider {
    static var previews: some View {
        RegisterView()
    }
}
