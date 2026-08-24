import SwiftUI
import SharedLogic

struct LoginView: View {
    @StateObject private var viewModel = LoginViewModel()
    var onLoginSuccess: ((User) -> Void)? = nil
    var onNavigateToRegister: (() -> Void)? = nil
    var onBack: (() -> Void)? = nil
    
    var body: some View {
        NavigationView {
            ZStack {
                Color(uiColor: .systemGroupedBackground)
                    .ignoresSafeArea()
                
                if viewModel.state.isSuccess, let user = viewModel.state.loggedInUser {
                    LoginSuccessSwiftUIView(
                        user: user,
                        onContinue: {
                            UIImpactFeedbackGenerator(style: .medium).impactOccurred()
                            onLoginSuccess?(user)
                        },
                        onLogout: {
                            UIImpactFeedbackGenerator(style: .light).impactOccurred()
                            viewModel.onReset()
                        }
                    )
                    .transition(.asymmetric(
                        insertion: .scale(scale: 0.9).combined(with: .opacity),
                        removal: .opacity
                    ))
                } else {
                    LoginFormSwiftUIView(
                        viewModel: viewModel,
                        onNavigateToRegister: onNavigateToRegister,
                        onBack: onBack
                    )
                    .transition(.opacity)
                }
            }
            .navigationTitle("Masuk")
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

private struct LoginFormSwiftUIView: View {
    @ObservedObject var viewModel: LoginViewModel
    var onNavigateToRegister: (() -> Void)?
    var onBack: (() -> Void)?
    
    private enum FormField: Hashable {
        case email
        case password
    }
    
    @FocusState private var focusedField: FormField?
    
    var body: some View {
        ScrollView {
            VStack(spacing: 24) {
                // Header Icon & Title
                VStack(spacing: 12) {
                    ZStack {
                        Circle()
                            .fill(Color.accentColor.opacity(0.12))
                            .frame(width: 88, height: 88)
                        
                        Image(systemName: "lock.shield.fill")
                            .font(.system(size: 42, weight: .semibold))
                            .foregroundColor(.accentColor)
                    }
                    .padding(.top, 12)
                    
                    VStack(spacing: 4) {
                        Text("Selamat Datang Kembali")
                            .font(.title2.weight(.bold))
                            .foregroundColor(.primary)
                        
                        Text("Masuk ke akun KMPTemplate Anda")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                    }
                }
                
                // General Error Banner (Animated)
                if let generalError = viewModel.state.generalError {
                    HStack(spacing: 12) {
                        Image(systemName: "exclamationmark.triangle.fill")
                            .font(.headline)
                            .foregroundColor(.red)
                        
                        Text(generalError)
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
                
                // Input Fields Inset Card
                VStack(spacing: 16) {
                    // Email Field
                    VStack(alignment: .leading, spacing: 6) {
                        Text("Email")
                            .font(.caption.weight(.semibold))
                            .foregroundColor(.secondary)
                        
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
                        .padding(.horizontal, 16)
                        .padding(.vertical, 14)
                        .background(Color(uiColor: .secondarySystemGroupedBackground))
                        .cornerRadius(14)
                        .overlay(
                            RoundedRectangle(cornerRadius: 14, style: .continuous)
                                .stroke(
                                    viewModel.state.emailError != nil ? Color.red :
                                        (focusedField == .email ? Color.accentColor : Color(uiColor: .separator).opacity(0.6)),
                                    lineWidth: focusedField == .email || viewModel.state.emailError != nil ? 1.5 : 1
                                )
                        )
                        
                        if let emailError = viewModel.state.emailError {
                            Text(emailError)
                                .font(.caption)
                                .foregroundColor(.red)
                                .transition(.opacity)
                        }
                    }
                    
                    // Password Field
                    VStack(alignment: .leading, spacing: 6) {
                        Text("Kata Sandi")
                            .font(.caption.weight(.semibold))
                            .foregroundColor(.secondary)
                        
                        HStack(spacing: 8) {
                            if viewModel.state.isPasswordVisible {
                                TextField("Minimal 6 karakter", text: Binding(
                                    get: { viewModel.state.password },
                                    set: { viewModel.onPasswordChange($0) }
                                ))
                                .textContentType(.password)
                                .textInputAutocapitalization(.never)
                                .disableAutocorrection(true)
                                .focused($focusedField, equals: .password)
                                .submitLabel(.done)
                                .onSubmit {
                                    handleFormSubmission()
                                }
                            } else {
                                SecureField("Minimal 6 karakter", text: Binding(
                                    get: { viewModel.state.password },
                                    set: { viewModel.onPasswordChange($0) }
                                ))
                                .textContentType(.password)
                                .textInputAutocapitalization(.never)
                                .disableAutocorrection(true)
                                .focused($focusedField, equals: .password)
                                .submitLabel(.done)
                                .onSubmit {
                                    handleFormSubmission()
                                }
                            }
                            
                            Button(action: {
                                UIImpactFeedbackGenerator(style: .light).impactOccurred()
                                viewModel.onTogglePasswordVisibility()
                            }) {
                                Image(systemName: viewModel.state.isPasswordVisible ? "eye.slash.fill" : "eye.fill")
                                    .font(.system(size: 18))
                                    .foregroundColor(.secondary)
                                    .frame(width: 36, height: 36)
                                    .contentShape(Rectangle())
                            }
                        }
                        .padding(.horizontal, 16)
                        .padding(.vertical, 14)
                        .background(Color(uiColor: .secondarySystemGroupedBackground))
                        .cornerRadius(14)
                        .overlay(
                            RoundedRectangle(cornerRadius: 14, style: .continuous)
                                .stroke(
                                    viewModel.state.passwordError != nil ? Color.red :
                                        (focusedField == .password ? Color.accentColor : Color(uiColor: .separator).opacity(0.6)),
                                    lineWidth: focusedField == .password || viewModel.state.passwordError != nil ? 1.5 : 1
                                )
                        )
                        
                        if let passwordError = viewModel.state.passwordError {
                            Text(passwordError)
                                .font(.caption)
                                .foregroundColor(.red)
                                .transition(.opacity)
                        }
                    }
                }
                
                // Submit Button
                Button(action: {
                    handleFormSubmission()
                }) {
                    HStack(spacing: 8) {
                        if viewModel.state.isLoading {
                            ProgressView()
                                .progressViewStyle(CircularProgressViewStyle(tint: .white))
                        } else {
                            Text("Masuk")
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

                // Cross-link to Register
                if let onNavigateToRegister = onNavigateToRegister {
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

                // Demo Credentials Quick Actions
                VStack(spacing: 12) {
                    Text("Akun Demo Cepat (Klik untuk isi)")
                        .font(.caption.weight(.medium))
                        .foregroundColor(.secondary)
                    
                    HStack(spacing: 12) {
                        Button(action: {
                            UIImpactFeedbackGenerator(style: .light).impactOccurred()
                            viewModel.onEmailChange("admin@kmptemplate.org")
                            viewModel.onPasswordChange("Password123!")
                        }) {
                            Label("Admin Demo", systemImage: "person.badge.shield.checkmark.fill")
                                .font(.caption.weight(.semibold))
                                .frame(maxWidth: .infinity)
                                .frame(height: 44)
                                .background(Color.accentColor.opacity(0.12))
                                .foregroundColor(.accentColor)
                                .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
                        }
                        .buttonStyle(AppleScaleButtonStyle())
                        
                        Button(action: {
                            UIImpactFeedbackGenerator(style: .light).impactOccurred()
                            viewModel.onEmailChange("user@kmptemplate.org")
                            viewModel.onPasswordChange("Password123!")
                        }) {
                            Label("User Demo", systemImage: "person.fill")
                                .font(.caption.weight(.semibold))
                                .frame(maxWidth: .infinity)
                                .frame(height: 44)
                                .background(Color.accentColor.opacity(0.12))
                                .foregroundColor(.accentColor)
                                .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
                        }
                        .buttonStyle(AppleScaleButtonStyle())
                    }
                }
                .padding(16)
                .background(Color(uiColor: .secondarySystemGroupedBackground))
                .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
                .padding(.top, 8)
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
    
    private func handleFormSubmission() {
        focusedField = nil
        UIImpactFeedbackGenerator(style: .medium).impactOccurred()
        viewModel.onSubmit()
    }
}

private struct LoginSuccessSwiftUIView: View {
    let user: User
    var onContinue: () -> Void
    var onLogout: () -> Void
    
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
                Text("Login Berhasil!")
                    .font(.title2.weight(.bold))
                    .foregroundColor(.primary)
                
                Text("Selamat datang kembali, \(user.name)")
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
                
                Button(action: onLogout) {
                    Text("Keluar (Logout)")
                        .font(.headline.weight(.medium))
                        .frame(maxWidth: .infinity)
                        .frame(height: 52)
                        .background(Color(uiColor: .secondarySystemGroupedBackground))
                        .foregroundColor(.red)
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

// MARK: - Native Apple Button Interaction Style
struct AppleScaleButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .scaleEffect(configuration.isPressed ? 0.97 : 1.0)
            .opacity(configuration.isPressed ? 0.88 : 1.0)
            .animation(.spring(response: 0.25, dampingFraction: 0.7), value: configuration.isPressed)
    }
}

struct LoginView_Previews: PreviewProvider {
    static var previews: some View {
        LoginView()
    }
}
