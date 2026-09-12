import Foundation
import SwiftUI
import SharedLogic

@MainActor
final class WalkthroughViewModel: ObservableObject {
    @Published var state: WalkthroughState

    private let store: WalkthroughStore
    private var stateTask: Task<Void, Never>?

    init(store: WalkthroughStore = WalkthroughMVIKt.createWalkthroughStore()) {
        self.store = store
        self.state = store.state.value

        self.stateTask = Task { [weak self] in
            guard let self else { return }
            for await newState in self.store.state {
                self.state = newState
            }
        }
    }

    deinit {
        stateTask?.cancel()
        store.close()
    }

    func onNext() {
        store.dispatch(intent: WalkthroughIntent.NextPage())
    }

    func onPrevious() {
        store.dispatch(intent: WalkthroughIntent.PreviousPage())
    }

    func onSelectPage(index: Int) {
        store.dispatch(intent: WalkthroughIntent.SelectPage(index: Int32(index)))
    }

    func onSkip() {
        store.dispatch(intent: WalkthroughIntent.Skip())
    }

    func onRestart() {
        store.dispatch(intent: WalkthroughIntent.LoadItems())
    }
}
