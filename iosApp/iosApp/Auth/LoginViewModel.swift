import Foundation
import SwiftUI
import SharedLogic

@MainActor
final class LoginViewModel: ObservableObject {
    @Published var state: LoginState

    private let store: LoginStore
    private var stateTask: Task<Void, Never>?

    init(store: LoginStore = KoinHelpersKt.getLoginStore()) {
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

    func onReset() {
        store.dispatch(intent: LoginIntent.Reset())
    }
}
