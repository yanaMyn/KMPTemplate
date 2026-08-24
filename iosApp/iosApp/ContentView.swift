import SwiftUI
import SharedLogic

struct ContentView: View {
    @State private var showContent = false
    @State private var showWalkthrough = false
    
    var body: some View {
        if showWalkthrough {
            WalkthroughView(onFinished: {
                showWalkthrough = false
            })
        } else {
            VStack(spacing: 16) {
                Button("Click me!") {
                    withAnimation {
                        showContent = !showContent
                    }
                }
                
                Button(action: {
                    showWalkthrough = true
                }) {
                    Label("Buka Fitur Walkthrough", systemImage: "sparkles")
                        .font(.headline)
                        .padding(.horizontal, 16)
                        .padding(.vertical, 10)
                        .background(Color.accentColor.opacity(0.12))
                        .cornerRadius(12)
                }

                if showContent {
                    VStack(spacing: 16) {
                        Image(systemName: "swift")
                            .font(.system(size: 200))
                            .foregroundColor(.accentColor)
                        Text("SwiftUI: \(Greeting().greet())")
                    }
                    .transition(.move(edge: .top).combined(with: .opacity))
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
            .padding()
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}