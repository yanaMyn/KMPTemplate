import Foundation
import SwiftUI
import SharedLogic

@MainActor
final class RegisterViewModel: ObservableObject {
    @Published var state: RegisterState

    private let store: RegisterStore
    private var stateTask: Task<Void, Never>?

    init(store: RegisterStore = RegisterStore(repository: AuthRepositoryImpl(dataSource: DefaultAuthDataSource()))) {
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
    }

    func onNameChange(_ name: String) {
        store.dispatch(intent: RegisterIntent.NameChanged(name: name))
    }

    func onEmailChange(_ email: String) {
        store.dispatch(intent: RegisterIntent.EmailChanged(email: email))
    }

    func onPasswordChange(_ password: String) {
        store.dispatch(intent: RegisterIntent.PasswordChanged(password: password))
    }

    func onConfirmPasswordChange(_ confirmPassword: String) {
        store.dispatch(intent: RegisterIntent.ConfirmPasswordChanged(confirmPassword: confirmPassword))
    }

    func onTogglePasswordVisibility() {
        store.dispatch(intent: RegisterIntent.TogglePasswordVisibility())
    }

    func onToggleConfirmPasswordVisibility() {
        store.dispatch(intent: RegisterIntent.ToggleConfirmPasswordVisibility())
    }

    func onToggleTerms() {
        store.dispatch(intent: RegisterIntent.ToggleTermsAccepted())
    }

    func onSubmit() {
        store.dispatch(intent: RegisterIntent.SubmitRegister())
    }

    func onReset() {
        store.dispatch(intent: RegisterIntent.Reset())
    }
}
