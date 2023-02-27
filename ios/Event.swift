import Foundation
import OSLog

@_implementationOnly import VKSDK

extension VkAuth {
    func send(event: Event) {
        self.sendEvent(withName: event.name, body: event.body)
        os_log("Sent react event", type: .info, event.description)
    }
}

protocol RCTEvent {
    var name: String { get }
    var body: Any { get }
}

struct Event {
    var name: String { self.event.name }
    var body: Any { self.event.body }

    private let event: RCTEvent

    private init(_ event: RCTEvent) {
        self.event = event
    }

    // MARK: - Accessors

    static let onLogout = Self(OnLogout())

    static func onAuth(userSession: VKID.UserSession) -> Self {
        .init(OnAuth(userSession: userSession))
    }

    static func onSilentDataReceive(silentToken: VKID.SilentToken) -> Self {
        .init(OnSilentDataReceive(silentToken: silentToken))
    }
}

extension Event: CustomStringConvertible {
    var description: String {
        "Event: name - \(name), body - \(body)"
    }
}

extension Event {
    private struct OnLogout: RCTEvent {
        let name = "onLogout"
        let body: Any = ()
    }

    private struct OnAuth: RCTEvent {
        let name = "onAuth"
        let body: Any

        init(userSession: VKID.UserSession) {
            self.body = userSession.dictionary
        }
    }

    private struct OnSilentDataReceive: RCTEvent {
        let name = "onSilentDataReceive"
        let body: Any

        init(silentToken: VKID.SilentToken) {d
            self.body = ["token": ["value": silentToken.token.value], "uuid": silentToken.uuid]
        }
    }
}
