#import <React/RCTBridgeModule.h>
#import <React/RCTViewManager.h>
#import <React/RCTEventEmitter.h>

@interface RCT_EXTERN_MODULE(VkAuth, NSObject)

#pragma mark - SDK

RCT_EXTERN_METHOD(initialize:(NSDictionary *)app vkid:(NSDictionary *)vkid)
RCT_EXTERN_METHOD(openURL:(NSString *)url)

#pragma mark - Auth

RCT_EXTERN_METHOD(getUserSessions:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(getUserProfile:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(startAuth)

RCT_EXTERN_METHOD(closeAuth)

RCT_EXTERN_METHOD(logout)

RCT_EXTERN_METHOD(accessTokenChangedSuccess:(NSString *)token userId:(NSNumber * _Nonnull)userId)

RCT_EXTERN_METHOD(accessTokenChangedFailed:(NSDictionary *)error)

#pragma mark - Events

RCT_EXTERN_METHOD(supportedEvents)

RCT_EXTERN_METHOD(addListener:)

RCT_EXTERN_METHOD(removeListeners:)

#pragma mark - Utility

+ (BOOL)requiresMainQueueSetup {
    return YES;
}

- (dispatch_queue_t)methodQueue {
    return dispatch_get_main_queue();
}

@end

@interface RCT_EXTERN_MODULE(RTCVkOneTapButton, RCTViewManager)

+ (BOOL)requiresMainQueueSetup {
    return YES;
}

- (dispatch_queue_t)methodQueue {
    return dispatch_get_main_queue();
}

@end
