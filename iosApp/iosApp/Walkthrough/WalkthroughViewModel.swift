import Foundation
import SwiftUI
import SharedLogic

@MainActor
final class WalkthroughViewModel: ObservableObject {
    @Published var state: WalkthroughState
    
    private let store: WalkthroughStore
    private var unsubscribe: (() -> Void)?
    
    init(store: WalkthroughStore = WalkthroughStore(repository: WalkthroughRepositoryImpl(dataSource: DefaultWalkthroughDataSource()))) {
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
    
    func onComplete() {
        store.dispatch(intent: WalkthroughIntent.Complete())
    }
    
    func onRestart() {
        store.dispatch(intent: WalkthroughIntent.LoadItems())
    }
}
