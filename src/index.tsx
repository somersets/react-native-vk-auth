import React from 'react';
import {
  NativeModules,
  Platform,
  ImageResolvedAssetSource,
  NativeEventEmitter,
  requireNativeComponent,
  ViewProps,
  NativeModule,
} from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-vk-auth' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

interface VkAuthModule extends NativeModule {
  getUserProfile: () => Promise<VKID.UserProfile>;
  startAuth: () => void;
  closeAuth: () => void;
  logout: () => void;
  getUserSessions: () => void;
  initialize: (app: VK.App, vkid: VKID) => void;
  openURL: (url: string) => void;

  accessTokenChangedSuccess: (token: string, userId: bigint) => void;
  accessTokenChangedFailed: (error: Error) => void;
}

const VkAuth: VkAuthModule = NativeModules.VkAuth
  ? NativeModules.VkAuth
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export class VK {
  static initialize(app: VK.App, vkid: VKID) {
    VkAuth.initialize(app, vkid);
  }

  static openURL(url: string) {
    VkAuth.openURL(url);
  }
}

export namespace VK {
  export interface App {
    mode: Mode;
    credentials: Credentials;
  }

  export enum Mode {
    DEBUG = 'DEBUG',
    TEST = 'TEST',
    RELEASE = 'RELEASE',
  }

  export interface Credentials {
    clientId: string;
    clientSecret: string;
  }
}

export class VKID {
  readonly appName: string;
  readonly appVersion: string;
  readonly appIcon: ImageResolvedAssetSource;
  readonly appLinks: VKID.Links;
  private readonly silentTokenExchanger: VKID.SilentTokenExchanger;
  private readonly eventEmitter: NativeEventEmitter;

  constructor(
    appName: string,
    appVersion: string,
    appIcon: ImageResolvedAssetSource,
    appLinks: VKID.Links,
    silentTokenExchanger: VKID.SilentTokenExchanger
  ) {
    this.appName = appName;
    this.appVersion = appVersion;
    this.appIcon = appIcon;
    this.appLinks = appLinks;
    this.silentTokenExchanger = silentTokenExchanger;
    this.eventEmitter = new NativeEventEmitter(VkAuth);

    this.initSilentDataReceive();
  }

  private initSilentDataReceive() {
    this.eventEmitter.removeAllListeners('onSilentDataReceive');
    this.eventEmitter.addListener(
      'onSilentDataReceive',
      async (silentToken: VKID.SilentToken) => {
        let exchangeResult: VKID.TokenExchangeResult =
          await this.silentTokenExchanger.exchange(silentToken).catch(() => {
            return { ok: false, error: Error('Exchange failed') };
          });
        VKID.accessTokenChanged(exchangeResult);
      }
    );
  }

  startAuth() {
    VkAuth.startAuth();
  }

  closeAuth() {
    VkAuth.closeAuth();
  }

  logout() {
    VkAuth.logout();
  }

  userSessions(): Promise<Array<VKID.Session.UserSession>> {
    // @ts-ignore
    let promise: Promise<Array<UserSessionInternal.UserSession>> =
      VkAuth.getUserSessions();
    return promise.then((sessions) => {
      return sessions.map((session) => {
        console.log(session);
        return session.type === UserSessionInternal.Type.AUTHORIZED
          ? new VKID.Session.Authorized()
          : new VKID.Session.Authenticated();
      });
    });
  }

  setOnAuthChanged(onAuthChanged: VKID.AuthChangedCallback) {
    this.eventEmitter.removeAllListeners('onLogout');
    this.eventEmitter.removeAllListeners('onAuth');

    this.eventEmitter.addListener('onLogout', () => {
      onAuthChanged.onLogout();
    });
    this.eventEmitter.addListener(
      'onAuth',
      (session: UserSessionInternal.UserSession) => {
        console.log(session);
        let externalSession =
          session.type === UserSessionInternal.Type.AUTHORIZED
            ? new VKID.Session.Authorized()
            : new VKID.Session.Authenticated();
        onAuthChanged.onAuth(externalSession);
      }
    );
  }

  private static accessTokenChanged(
    result: VKID.TokenExchangeResult<VKID.AccessToken, Error>
  ) {
    if (result.ok) {
      VkAuth.accessTokenChangedSuccess(
        result.accessToken.token.value,
        result.accessToken.userID.value
      );
    } else {
      VkAuth.accessTokenChangedFailed(result.error);
    }
  }
}

export namespace VKID {
  export class Token {
    value: string;

    constructor(value: string) {
      this.value = value;
    }
  }

  export class UserID {
    value: bigint;

    constructor(value: bigint) {
      this.value = value;
    }
  }

  export interface SilentToken {
    token: Token;
    uuid: string;
    firstName: string;
    lastName: string;
  }

  export interface SilentTokenExchanger {
    exchange(
      silentData: VKID.SilentToken
    ): Promise<VKID.TokenExchangeResult<VKID.AccessToken, Error>>;
  }

  export interface AccessToken {
    token: Token;
    userID: UserID;
  }

  export interface Links {
    serviceUserAgreement: string;
    servicePrivacyPolicy: string;
    serviceSupport: string | null;
  }

  export interface UserProfile {
    userID: UserID;
    firstName: string | null;
    lastName: string | null;
    phone: string | null;
    photo200: string | null;
    email: string | null;
    userHash: string | null;
  }

  export interface AuthChangedCallback {
    onAuth(userSession: VKID.Session.UserSession): void;

    onLogout(): void;
  }

  export namespace Session {
    export abstract class UserSession {}

    export class Authorized extends UserSession {
      constructor() {
        super();
      }

      get userProfile(): Promise<VKID.UserProfile> {
        return VkAuth.getUserProfile();
      }

      toString(): string {
        return 'Authorized';
      }
    }

    export class Authenticated extends UserSession {
      toString(): string {
        return 'Authenticated';
      }
    }
  }

  export type TokenExchangeResult<T = VKID.AccessToken, E = Error> =
    | { ok: true; accessToken: T }
    | { ok: false; error: E };
}

namespace UserSessionInternal {
  export class UserSession {
    private readonly _type: UserSessionInternal.Type;

    constructor(type: Type) {
      this._type = type;
    }

    get type(): Type {
      return this._type;
    }
  }

  export enum Type {
    AUTHORIZED = 'authorized',
    AUTHENTICATED = 'authenticated',
  }
}

export namespace VKOneTapButtonSpace {
  export const nativeView = requireNativeComponent('RTCVkOneTapButton');

  export enum BgColor {
    BLUE = 'BLUE',
    WHITE = 'WHITE',
    CUSTOM = 'CUSTOM',
  }

  export interface BgStyle {
    style: BgColor;
    customVkIconColor?: string;
    customBackgroundColor?: string;
    customTextColor?: string;
  }

  export enum IconGravity {
    START = 'START',
    TEXT = 'TEXT',
  }

  export enum LineFieldType {
    ACTION = 'ACTION',
    PHONE = 'PHONE',
    NONE = 'NONE',
  }

  export interface Texts {
    noUserText?: string;
    actionText?: string;
    phoneText?: string;
  }

  export type Props = {
    backgroundStyle?: BgStyle | undefined;
    iconGravity?: IconGravity | undefined;
    firstLineFieldType?: LineFieldType | undefined;
    secondLineFieldType?: LineFieldType | undefined;
    oneLineTextSize?: number | undefined;
    firstLineTextSize?: number | undefined;
    secondLineTextSize?: number | undefined;
    avatarSize?: number | undefined;
    iconSize?: number | undefined;
    progressSize?: number | undefined;
    texts?: Texts | undefined;
  } & ViewProps;
}

export class VKOneTapButton extends React.Component<VKOneTapButtonSpace.Props> {
  render() {
    // @ts-ignore
    return <VKOneTapButtonSpace.nativeView {...this.props} />;
  }
}
