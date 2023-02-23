import Foundation

@_implementationOnly import VKSDK

protocol RCTDomain {
    var dictionary: [String: Any?] { get }
}

extension VKID.Profile: RCTDomain {
    #warning("Use value instead of description for userID")
    var dictionary: [String : Any?] {
        [
            "userID": ["value": self.id.description],
            "firstName": self.name?.firstName,
            "lastName": self.name?.lastName,
            "phone": self.phoneMask,
            "photo200": self.image?.url?.absoluteString
        ]
    }
}

extension VKID.UserSession: RCTDomain {
    var dictionary: [String : Any?] {
        ["type": self.authorized == nil ? "authenticated" : "authorized"]
    }
}
