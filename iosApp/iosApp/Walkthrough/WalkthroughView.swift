import SwiftUI
import SharedLogic

struct WalkthroughView: View {
    @StateObject private var viewModel = WalkthroughViewModel()
    var onFinished: (() -> Void)? = nil
    
    var body: some View {
        ZStack {
            Color(UIColor.systemBackground)
                .ignoresSafeArea()
            
            if viewModel.state.isCompleted {
                completedView
                    .transition(.opacity.combined(with: .scale))
            } else {
                contentView
                    .transition(.opacity)
            }
        }
        .animation(.easeInOut(duration: 0.3), value: viewModel.state.currentIndex)
        .animation(.easeInOut(duration: 0.3), value: viewModel.state.isCompleted)
    }
    
    // MARK: - Main Content View
    private var contentView: some View {
        VStack(spacing: 0) {
            // Header: Skip button
            HStack {
                Spacer()
                if !viewModel.state.isLastPage {
                    Button(action: {
                        viewModel.onSkip()
                    }) {
                        Text("Lewati")
                            .font(.subheadline.weight(.semibold))
                            .foregroundColor(.secondary)
                            .padding(.horizontal, 16)
                            .padding(.vertical, 8)
                    }
                }
            }
            .frame(height: 44)
            .padding(.horizontal)
            
            Spacer()
            
            // Slide Card
            if let currentItem = viewModel.state.currentItem {
                slideView(item: currentItem)
                    .id(currentItem.id)
                    .transition(.asymmetric(
                        insertion: .opacity.combined(with: .offset(x: 40)),
                        removal: .opacity.combined(with: .offset(x: -40))
                    ))
            }
            
            Spacer()
            
            // Footer: Indicators and Buttons
            VStack(spacing: 28) {
                // Indicators
                pageIndicators
                
                // Action Buttons
                HStack(spacing: 12) {
                    if !viewModel.state.isFirstPage {
                        Button(action: {
                            viewModel.onPrevious()
                        }) {
                            HStack {
                                Image(systemName: "chevron.left")
                                Text("Sebelumnya")
                            }
                            .font(.headline.weight(.medium))
                            .frame(maxWidth: .infinity)
                            .frame(height: 52)
                            .foregroundColor(.primary)
                            .background(Color(UIColor.secondarySystemBackground))
                            .cornerRadius(16)
                        }
                    }
                    
                    Button(action: {
                        viewModel.onNext()
                    }) {
                        HStack {
                            Text(viewModel.state.isLastPage ? "Mulai Sekarang" : "Lanjut")
                            Image(systemName: viewModel.state.isLastPage ? "checkmark.circle.fill" : "chevron.right")
                        }
                        .font(.headline.weight(.semibold))
                        .frame(maxWidth: .infinity)
                        .frame(height: 52)
                        .foregroundColor(.white)
                        .background(Color.accentColor)
                        .cornerRadius(16)
                    }
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
                    .frame(width: 140, height: 140)
                
                Image(systemName: getSfSymbol(for: item.iconName))
                    .font(.system(size: 56, weight: .medium))
                    .foregroundColor(.accentColor)
            }
            .padding(.bottom, 16)
            
            Text(item.title)
                .font(.title2.bold())
                .multilineTextAlignment(.center)
                .foregroundColor(.primary)
                .padding(.horizontal, 16)
            
            Text(item.description_)
                .font(.body)
                .multilineTextAlignment(.center)
                .foregroundColor(.secondary)
                .lineSpacing(4)
                .padding(.horizontal, 24)
        }
    }
    
    // MARK: - Page Indicators
    private var pageIndicators: some View {
        HStack(spacing: 8) {
            ForEach(0..<Int(viewModel.state.totalPages), id: \.self) { index in
                let isSelected = index == Int(viewModel.state.currentIndex)
                Capsule()
                    .fill(isSelected ? Color.accentColor : Color(UIColor.systemGray4))
                    .frame(width: isSelected ? 24 : 8, height: 8)
                    .onTapGesture {
                        viewModel.onSelectPage(index: index)
                    }
            }
        }
    }
    
    // MARK: - Completed View
    private var completedView: some View {
        VStack(spacing: 24) {
            Spacer()
            
            ZStack {
                Circle()
                    .fill(Color.accentColor.opacity(0.15))
                    .frame(width: 110, height: 110)
                
                Image(systemName: "checkmark.circle.fill")
                    .font(.system(size: 64))
                    .foregroundColor(.accentColor)
            }
            
            Text("Siap Digunakan!")
                .font(.title.bold())
                .foregroundColor(.primary)
            
            Text("Anda telah menyelesaikan panduan awal. Selamat menjelajah aplikasi!")
                .font(.body)
                .multilineTextAlignment(.center)
                .foregroundColor(.secondary)
                .padding(.horizontal, 32)
            
            Spacer()
            
            VStack(spacing: 12) {
                Button(action: {
                    onFinished?()
                }) {
                    Text("Masuk ke Beranda")
                        .font(.headline.weight(.semibold))
                        .frame(maxWidth: .infinity)
                        .frame(height: 52)
                        .foregroundColor(.white)
                        .background(Color.accentColor)
                        .cornerRadius(16)
                }
                
                Button(action: {
                    viewModel.onRestart()
                }) {
                    Text("Ulangi Panduan")
                        .font(.headline.weight(.medium))
                        .frame(maxWidth: .infinity)
                        .frame(height: 52)
                        .foregroundColor(.secondary)
                        .background(Color(UIColor.secondarySystemBackground))
                        .cornerRadius(16)
                }
            }
            .padding(.horizontal, 24)
            .padding(.bottom, 24)
        }
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
