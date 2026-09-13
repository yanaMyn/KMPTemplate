import SwiftUI
import SharedLogic

@main
struct iOSApp: App {
    init() {
        SharedModuleKt.startSharedKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
