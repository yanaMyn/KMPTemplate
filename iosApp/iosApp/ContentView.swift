import SwiftUI
import SharedLogic

struct ContentView: View {
    @State private var showContent = false
    @State private var showWalkthrough = false
    @State private var authSheet: AuthSheet? = nil
    @State private var pendingAuthSheet: AuthSheet? = nil
    @State private var loggedInUser: User? = nil

    /// Layar autentikasi yang ditampilkan sebagai full-screen cover (bukan sheet — supaya
    /// tidak bisa di-swipe-dismiss dan benar-benar penuh sampai ke atas layar).
    /// Satu modifier `.fullScreenCover(item:)` dipakai untuk keduanya agar tidak ada
    /// dua cover yang saling bertumpuk saat berpindah Login <-> Register.
    private enum AuthSheet: String, Identifiable {
        case login
        case register

        var id: String { rawValue }
    }
    
    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 20) {
                    // Logged in User Card
                    if let user = loggedInUser {
                        HStack(spacing: 16) {
                            ZStack {
                                Circle()
                                    .fill(Color.accentColor.opacity(0.15))
                                    .frame(width: 50, height: 50)
                                Image(systemName: "person.crop.circle.fill")
                                    .font(.system(size: 28))
                                    .foregroundColor(.accentColor)
                            }
                            
                            VStack(alignment: .leading, spacing: 2) {
                                Text("Halo, \(user.name)")
                                    .font(.headline.weight(.semibold))
                                    .foregroundColor(.primary)
                                Text(user.email)
                                    .font(.subheadline)
                                    .foregroundColor(.secondary)
                            }
                            
                            Spacer()
                            
                            Button(action: {
                                UIImpactFeedbackGenerator(style: .light).impactOccurred()
                                withAnimation(.spring(response: 0.35, dampingFraction: 0.8)) {
                                    loggedInUser = nil
                                }
                            }) {
                                Text("Keluar")
                                    .font(.footnote.weight(.semibold))
                                    .foregroundColor(.red)
                                    .padding(.horizontal, 12)
                                    .padding(.vertical, 6)
                                    .background(Color.red.opacity(0.1))
                                    .clipShape(Capsule())
                            }
                        }
                        .padding(16)
                        .background(Color(uiColor: .secondarySystemGroupedBackground))
                        .clipShape(RoundedRectangle(cornerRadius: 18, style: .continuous))
                        .transition(.scale.combined(with: .opacity))
                    }
                    
                    // Main Action Cards
                    VStack(spacing: 12) {
                        Button(action: {
                            UIImpactFeedbackGenerator(style: .medium).impactOccurred()
                            authSheet = .login
                        }) {
                            HStack(spacing: 12) {
                                ZStack {
                                    Circle()
                                        .fill(Color.white.opacity(0.2))
                                        .frame(width: 36, height: 36)
                                    Image(systemName: "lock.shield.fill")
                                        .font(.system(size: 18, weight: .semibold))
                                }
                                
                                Text("Buka Halaman Login")
                                    .font(.headline.weight(.semibold))
                                
                                Spacer()
                                
                                Image(systemName: "chevron.right")
                                    .font(.subheadline.weight(.semibold))
                                    .opacity(0.8)
                            }
                            .padding(.horizontal, 18)
                            .frame(height: 56)
                            .background(Color.accentColor)
                            .foregroundColor(.white)
                            .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
                        }
                        .buttonStyle(AppleScaleButtonStyle())
                        
                        Button(action: {
                            UIImpactFeedbackGenerator(style: .light).impactOccurred()
                            authSheet = .register
                        }) {
                            HStack(spacing: 12) {
                                ZStack {
                                    Circle()
                                        .fill(Color.accentColor.opacity(0.12))
                                        .frame(width: 36, height: 36)
                                    Image(systemName: "person.badge.plus")
                                        .font(.system(size: 18, weight: .semibold))
                                        .foregroundColor(.accentColor)
                                }

                                Text("Buat Akun Baru")
                                    .font(.headline.weight(.semibold))
                                    .foregroundColor(.primary)

                                Spacer()

                                Image(systemName: "chevron.right")
                                    .font(.subheadline.weight(.semibold))
                                    .foregroundColor(.secondary)
                            }
                            .padding(.horizontal, 18)
                            .frame(height: 56)
                            .background(Color(uiColor: .secondarySystemGroupedBackground))
                            .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
                        }
                        .buttonStyle(AppleScaleButtonStyle())

                        Button(action: {
                            UIImpactFeedbackGenerator(style: .light).impactOccurred()
                            showWalkthrough = true
                        }) {
                            HStack(spacing: 12) {
                                ZStack {
                                    Circle()
                                        .fill(Color.accentColor.opacity(0.12))
                                        .frame(width: 36, height: 36)
                                    Image(systemName: "sparkles")
                                        .font(.system(size: 18, weight: .semibold))
                                        .foregroundColor(.accentColor)
                                }
                                
                                Text("Buka Fitur Walkthrough")
                                    .font(.headline.weight(.semibold))
                                    .foregroundColor(.primary)
                                
                                Spacer()
                                
                                Image(systemName: "chevron.right")
                                    .font(.subheadline.weight(.semibold))
                                    .foregroundColor(.secondary)
                            }
                            .padding(.horizontal, 18)
                            .frame(height: 56)
                            .background(Color(uiColor: .secondarySystemGroupedBackground))
                            .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
                        }
                        .buttonStyle(AppleScaleButtonStyle())
                        
                        Button(action: {
                            UIImpactFeedbackGenerator(style: .light).impactOccurred()
                            withAnimation(.spring(response: 0.35, dampingFraction: 0.8)) {
                                showContent = !showContent
                            }
                        }) {
                            HStack {
                                Image(systemName: showContent ? "eye.slash" : "hand.tap.fill")
                                Text(showContent ? "Sembunyikan Greeting" : "Tampilkan Greeting")
                            }
                            .font(.subheadline.weight(.medium))
                            .frame(maxWidth: .infinity)
                            .frame(height: 44)
                            .foregroundColor(.secondary)
                        }
                    }
                    
                    if showContent {
                        VStack(spacing: 16) {
                            Image(systemName: "swift")
                                .font(.system(size: 100))
                                .foregroundColor(.accentColor)
                                .padding(.top, 12)
                            
                            Text("SwiftUI: \(Greeting().greet())")
                                .font(.body.weight(.medium))
                                .multilineTextAlignment(.center)
                                .foregroundColor(.secondary)
                        }
                        .padding(24)
                        .frame(maxWidth: .infinity)
                        .background(Color(uiColor: .secondarySystemGroupedBackground))
                        .clipShape(RoundedRectangle(cornerRadius: 20, style: .continuous))
                        .transition(.move(edge: .top).combined(with: .opacity))
                    }
                }
                .padding(.horizontal, 20)
                .padding(.vertical, 16)
            }
            .background(Color(uiColor: .systemGroupedBackground).ignoresSafeArea())
            .navigationTitle("KMPTemplate")
        }
        .navigationViewStyle(.stack)
        .fullScreenCover(item: $authSheet, onDismiss: presentPendingAuthSheet) { sheet in
            switch sheet {
            case .login:
                LoginView(
                    onLoginSuccess: { user in
                        withAnimation(.spring(response: 0.35, dampingFraction: 0.8)) {
                            loggedInUser = user
                        }
                        authSheet = nil
                    },
                    onNavigateToRegister: {
                        switchAuthSheet(to: .register)
                    },
                    onBack: {
                        authSheet = nil
                    }
                )
            case .register:
                RegisterView(
                    onRegisterSuccess: { user in
                        withAnimation(.spring(response: 0.35, dampingFraction: 0.8)) {
                            loggedInUser = user
                        }
                        authSheet = nil
                    },
                    onNavigateToLogin: {
                        switchAuthSheet(to: .login)
                    },
                    onBack: {
                        authSheet = nil
                    }
                )
            }
        }
        .fullScreenCover(isPresented: $showWalkthrough) {
            WalkthroughView(onFinished: {
                showWalkthrough = false
            })
        }
    }

    /// Menutup sheet aktif lalu menandai sheet berikutnya untuk ditampilkan
    /// setelah animasi dismiss selesai (lihat `presentPendingAuthSheet`).
    private func switchAuthSheet(to destination: AuthSheet) {
        pendingAuthSheet = destination
        authSheet = nil
    }

    private func presentPendingAuthSheet() {
        guard let pending = pendingAuthSheet else { return }
        pendingAuthSheet = nil
        authSheet = pending
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}