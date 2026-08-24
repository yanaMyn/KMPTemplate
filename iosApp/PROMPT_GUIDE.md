# Xcode AI & Prompt Guide (iOS)

When working directly in Xcode with Apple Intelligence, Copilot for Xcode, or chat assistants, use the following context for generating SwiftUI views that consume the KMP Shared Store:

## SwiftUI State Wrapper Pattern
```swift
import SwiftUI
import sharedLogic

@MainActor
class WalkthroughObservable: ObservableObject {
    @Published var state: WalkthroughState
    private var store: WalkthroughStore
    private var unsubscribe: (() -> Void)?

    init(store: WalkthroughStore = WalkthroughStore()) {
        self.store = store
        self.state = store.state
        self.unsubscribe = store.subscribe { [weak self] newState in
            DispatchQueue.main.async {
                self?.state = newState
            }
        }
    }

    deinit {
        unsubscribe?()
    }

    func dispatch(_ intent: WalkthroughIntent) {
        store.dispatch(intent: intent)
    }
}
```

## Safety Rule
Do NOT edit project configuration files (`.pbxproj` or `.xcodeproj`) manually. Modify only Swift UI files under `iosApp/iosApp/`.
