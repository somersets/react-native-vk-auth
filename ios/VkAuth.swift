import UIKit
import OSLog

@_implementationOnly import VKSDK

@objc(VkAuth)
final class VkAuth: RCTEventEmitter {
    private static var _sharedSDK: VKSDK.VK.Type2<App, VKID>? // DO NOT USE THIS DIRECTLY
    fileprivate static var _sharedSuperappKit: VkAuth?

    fileprivate static var sharedSDK: VKSDK.VK.Type2<App, VKID> {
        guard let _shared = Self._sharedSDK else {
            fatalError("VKSDK is not initialized. Call initialize(_:vkid:) method before using VkAuth.")
        }

        return _shared
    }

    private var activeAuthCompletion: ((Result<VKSDK.VKID.AccessToken, Error>) -> Void)?

    @objc(initialize:vkid:)
    func initialize(_ app: NSDictionary, vkid: NSDictionary) {
        guard
            let credentials = app["credentials"] as? [String: Any],
            let clientId = credentials["clientId"] as? String,
            let clientSecret = credentials["clientSecret"] as? String
        else {
            os_log("Incorrect credentials format", type: .error)
            return
        }

        do {
            Self._sharedSDK = try VK {
                App(credentials: .init(clientId: clientId, clientSecret: clientSecret))
                VKID()
            }
            Self._sharedSuperappKit = self
        } catch {
            os_log("VKSDK initialization failed", type: .error, error.localizedDescription)
        }
    }

    @objc(openURL:)
    func openURL(_ url: String) {
        guard let url = URL(string: url) else {
            return
        }

        try? Self.sharedSDK.open(url: url)
    }

    // MARK: - Auth

    @objc func startAuth() {
        let flow = VKID.AuthFlow.exchanging(tokenExchanger: .custom(weak: self))
        let authController = VKID.AuthController(flow: flow, delegate: self)

        let viewController = try! Self.sharedSDK.vkid.ui(for: authController).uiViewController()
        UIApplication.shared.keyWindow?.rootViewController?.present(viewController, animated: true)
    }

    @objc func closeAuth() {
        UIApplication.shared.keyWindow?.rootViewController?.dismiss(animated: true)
        os_log("Authorization closed", type: .info)
    }

    @objc func logout() {
        guard let userSession = Self.sharedSDK.vkid.userSessions.first else {
            return
        }

        userSession.authorized?.logout { [weak self] _ in
            self?.send(event: .onLogout)
        }

        os_log("Logout user session", type: .info, "\(userSession)")
    }

    @objc(getUserSessions:rejecter:)
    func getUserSessions(
        resolver resolve: RCTPromiseResolveBlock,
        rejecter reject: RCTPromiseRejectBlock
    ) {
        resolve(Self.sharedSDK.vkid.userSessions.map(\.dictionary))
    }

    @objc(getUserProfile:rejecter:)
    func getUserProfile(
        resolver resolve: @escaping RCTPromiseResolveBlock,
        rejecter reject: @escaping RCTPromiseRejectBlock
    ) {
        guard let authorized = Self.sharedSDK.vkid.userSessions.first?.authorized else {
            reject(nil, nil, nil)
            return
        }

        authorized.requestProfile { result in
            do {
                let profile = try result.get()
                resolve(profile.dictionary)
            } catch {
                reject(nil, nil, nil)
            }
        }
    }
}

extension VkAuth: TokenExchanging {
    func exchange(silentToken: VKSDK.VKID.SilentToken, completion: @escaping (Result<VKSDK.VKID.AccessToken, Error>) -> Void) {
        self.activeAuthCompletion = completion
        self.send(event: .onSilentDataReceive(silentToken: silentToken))
    }

    @objc(accessTokenChangedSuccess:userId:)
    func accessTokenChangedSuccess(_ token: String, userId: NSNumber) {
        let accessToken = VKID.AccessToken(.init(token), userID: .init(userId.uint64Value))
        self.activeAuthCompletion?(.success(accessToken))
        self.activeAuthCompletion = nil
        
        os_log("Token exchange succeeded", type: .info)
    }

    @objc(accessTokenChangedFailed:)
    func accessTokenChangedFailed(_ error: NSDictionary) {
        let reactError = NSError(domain: "React Native", code: -9999, userInfo: error as! [String : Any])
        self.activeAuthCompletion?(.failure(reactError))
        self.activeAuthCompletion = nil

        os_log("Token exchange failed", type: .error, reactError.localizedDescription)
    }
}

extension VkAuth: VKIDFlowDelegate {
    func vkid(_ vkid: VKSDK.VKID.Module, didCompleteAuthWith result: Result<VKSDK.VKID.UserSession, Error>) {
        do {
            self.send(event: .onAuth(userSession: try result.get()))
        } catch {
            os_log("Authorization failed", type: .error, error.localizedDescription)
        }
    }
}

@objc(RTCVkOneTapButton)
final class OneTapButtonManager: RCTViewManager {
    override func view() -> UIView! {
        guard let sharedSuperappKit = VkAuth._sharedSuperappKit else {
            fatalError("VkAuth is not initialized. Call initialize(_:vkid:) method before using VkAuth.")
            return UIView()
        }
        var authPresenter: VKSDK.UIKitPresenter = .newUIWindow
        if let root = UIApplication.shared.keyWindow?.rootViewController {
            authPresenter = .uiViewController(root)
        }

        let flow = VKID.AuthFlow.exchanging(tokenExchanger: .custom(weak: sharedSuperappKit))
        let authController = VKID.AuthController(flow: flow, delegate: sharedSuperappKit)

        let button = VKID.OneTapButton(
            mode: .default,
            controllerConfiguration: .authController(
                authController,
                presenter: authPresenter
            )
        )

        guard let buttonView = try? VkAuth.sharedSDK.vkid.ui(for: button).uiView() else {
            fatalError("OneTapButton configuration problem")
            return UIView()
        }

        return buttonView
    }
}

extension VkAuth {
    @objc(supportedEvents)
    override func supportedEvents() -> [String] {
        ["onLogout", "onAuth", "onSilentDataReceive"]
    }
}
