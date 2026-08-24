import Foundation
import SwiftUI
import SharedLogic

@MainActor
final class LoginViewModel: ObservableObject {
    @Published var state: LoginState
    
    private let store: LoginStore
    private var unsubscribe: (() -> Void)?
    
    init(store: LoginStore = LoginStore(repository: AuthRepositoryImpl(dataSource: DefaultAuthDataSource()))) {
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
    
    func onEmailChange(_ email: String) {
        store.dispatch(intent: LoginIntent.EmailChanged(email: email))
    }
    
    func onPasswordChange(_ password: String) {
        store.dispatch(intent: LoginIntent.PasswordChanged(password: password))
    }
    
    func onTogglePasswordVisibility() {
        store.dispatch(intent: LoginIntent.TogglePasswordVisibility())
    }
    
    func onSubmit() {
        store.dispatch(intent: LoginIntent.SubmitLogin())
    }
    
    func onClearErrors() {
        store.dispatch(intent: LoginIntent.ClearErrors())
    }
    
    func onReset() {
        store.dispatch(intent: LoginIntent.Reset())
    }
}
