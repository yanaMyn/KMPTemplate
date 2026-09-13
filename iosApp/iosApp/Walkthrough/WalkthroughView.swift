import SwiftUI
import SharedLogic

struct WalkthroughView: View {
    @StateObject private var viewModel = WalkthroughViewModel()
    var onFinished: (() -> Void)? = nil
    
    var body: some View {
        NavigationView {
            ZStack {
                Color(uiColor: .systemGroupedBackground)
                    .ignoresSafeArea()
                
                if viewModel.state.isCompleted {
                    completedView
                        .transition(.asymmetric(
                            insertion: .scale(scale: 0.92).combined(with: .opacity),
                            removal: .opacity
                        ))
                } else {
                    contentView
                        .transition(.opacity)
                }
            }
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    if !viewModel.state.isCompleted && !viewModel.state.isLastPage {
                        Button(action: {
                            UIImpactFeedbackGenerator(style: .light).impactOccurred()
                            withAnimation(.spring(response: 0.38, dampingFraction: 0.8)) {
                                viewModel.onSkip()
                            }
                        }) {
                            Text("Lewati")
                                .font(.subheadline.weight(.semibold))
                                .foregroundColor(.secondary)
                        }
                    }
                }
            }
        }
        .navigationViewStyle(.stack)
        .animation(.spring(response: 0.38, dampingFraction: 0.8), value: viewModel.state.currentIndex)
        .animation(.spring(response: 0.38, dampingFraction: 0.8), value: viewModel.state.isCompleted)
        .onChange(of: viewModel.state.isCompleted) { completed in
            if completed {
                UINotificationFeedbackGenerator().notificationOccurred(.success)
            }
        }
    }
    
    // MARK: - Main Content View
    private var contentView: some View {
        VStack(spacing: 0) {
            Spacer()
            
            // Slide Card
            if let currentItem = viewModel.state.currentItem {
                slideView(item: currentItem)
                    .id(currentItem.id)
                    .transition(.asymmetric(
                        insertion: .opacity.combined(with: .offset(x: 35)),
                        removal: .opacity.combined(with: .offset(x: -35))
                    ))
            }
            
            Spacer()
            
            // Footer: Indicators and Buttons
            VStack(spacing: 28) {
                // Page Indicators with 44pt accessible touch target
                pageIndicators
                
                // Action Buttons
                HStack(spacing: 12) {
                    if !viewModel.state.isFirstPage {
                        Button(action: {
                            UIImpactFeedbackGenerator(style: .light).impactOccurred()
                            withAnimation(.spring(response: 0.35, dampingFraction: 0.8)) {
                                viewModel.onPrevious()
                            }
                        }) {
                            HStack(spacing: 6) {
                                Image(systemName: "chevron.left")
                                    .font(.subheadline.weight(.semibold))
                                Text("Sebelumnya")
                            }
                            .font(.headline.weight(.medium))
                            .frame(maxWidth: .infinity)
                            .frame(height: 52)
                            .foregroundColor(.primary)
                            .background(Color(uiColor: .secondarySystemGroupedBackground))
                            .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
                        }
                        .buttonStyle(AppleScaleButtonStyle())
                    }
                    
                    Button(action: {
                        UIImpactFeedbackGenerator(style: .medium).impactOccurred()
                        withAnimation(.spring(response: 0.35, dampingFraction: 0.8)) {
                            viewModel.onNext()
                        }
                    }) {
                        HStack(spacing: 6) {
                            Text(viewModel.state.isLastPage ? "Mulai Sekarang" : "Lanjut")
                            Image(systemName: viewModel.state.isLastPage ? "checkmark.circle.fill" : "chevron.right")
                                .font(.subheadline.weight(.semibold))
                        }
                        .font(.headline.weight(.semibold))
                        .frame(maxWidth: .infinity)
                        .frame(height: 52)
                        .foregroundColor(.white)
                        .background(Color.accentColor)
                        .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
                    }
                    .buttonStyle(AppleScaleButtonStyle())
                }
            }
            .padding(.horizontal, 24)
            .padding(.bottom, 24)
        }
    }
    
    // MARK: - Slide View
    private func slideView(item: WalkthroughItem) -> some View {
        VStack(spacing: 24) {
            ZStack {
                RoundedRectangle(cornerRadius: 32, style: .continuous)
                    .fill(Color.accentColor.opacity(0.12))
                    .frame(width: 130, height: 130)
                
                Image(systemName: getSfSymbol(for: item.iconName))
                    .font(.system(size: 54, weight: .medium))
                    .foregroundColor(.accentColor)
            }
            .padding(.bottom, 12)
            
            Text(item.title)
                .font(.title2.weight(.bold))
                .multilineTextAlignment(.center)
                .foregroundColor(.primary)
                .padding(.horizontal, 20)
            
            Text(item.description_)
                .font(.body)
                .multilineTextAlignment(.center)
                .foregroundColor(.secondary)
                .lineSpacing(4)
                .padding(.horizontal, 28)
        }
    }
    
    // MARK: - Page Indicators
    private var pageIndicators: some View {
        HStack(spacing: 8) {
            ForEach(0..<Int(viewModel.state.totalPages), id: \.self) { index in
                let isSelected = index == Int(viewModel.state.currentIndex)
                Capsule()
                    .fill(isSelected ? Color.accentColor : Color(uiColor: .systemGray4))
                    .frame(width: isSelected ? 26 : 8, height: 8)
                    .animation(.spring(response: 0.3, dampingFraction: 0.7), value: isSelected)
                    .frame(minWidth: 44, minHeight: 44) // 44pt tap target
                    .contentShape(Rectangle())
                    .onTapGesture {
                        UIImpactFeedbackGenerator(style: .light).impactOccurred()
                        withAnimation(.spring(response: 0.35, dampingFraction: 0.8)) {
                            viewModel.onSelectPage(index: index)
                        }
                    }
            }
        }
    }
    
    // MARK: - Completed View
    private var completedView: some View {
        AppSuccessCard(
            title: "Siap Digunakan!",
            subtitle: "Selamat!",
            caption: "Anda telah menyelesaikan panduan awal. Selamat menjelajah aplikasi!",
            primaryTitle: "Masuk ke Beranda",
            onPrimary: { onFinished?() },
            secondaryTitle: "Ulangi Panduan",
            secondaryTint: .accentColor,
            onSecondary: {
                withAnimation(.spring(response: 0.38, dampingFraction: 0.8)) {
                    viewModel.onRestart()
                }
            }
        )
    }
    
    private func getSfSymbol(for iconName: String) -> String {
        switch iconName {
        case "sparkles":
            return "sparkles"
        case "layers":
            return "square.3.layers.3d"
        case "devices":
            return "laptopcomputer.and.iphone"
        default:
            return "star.fill"
        }
    }
}

struct WalkthroughView_Previews: PreviewProvider {
    static var previews: some View {
        WalkthroughView()
    }
}
