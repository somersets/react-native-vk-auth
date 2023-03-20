# react-native-vk-auth

React Native VK ID Auth

## Installation

```sh
npm install react-native-vk-auth
```

## Настройка iOS проекта

Шаг 1. Установка Cocoapods

```sh
cd ios && pod install
```

Шаг 2. Поддержка URL схемы
Чтобы пользователь мог авторизоваться бесшовно, SDK взаимодействует с клиентом VK на устройстве пользователя. Если в клиенте есть активная сессия, пользователь увидит свои данные (имя, аватарку и телефон) в кнопках и шторке. Авторизация завершится в один клик по кнопке "Продолжить как 'username'".

Чтобы переход за авторизацией в клиент VK работал, необходимо поддержать схему URL. Для этого добавьте схему vkauthorize-silent в ключ LSApplicationQueriesSchemes в Info.plist.

#### Пример записи схемы в Info.plist

```xml
<key>LSApplicationQueriesSchemes</key>
<array>
    <string>vkauthorize-silent</string>
</array>
```

#### Universal Link
Для работы бесшовной авторизации необходимо поддержать Universal Link. При создании приложения на сайте платформы, вам нужно было указать Universal Link, по которому клиент VK будет открывать ваше приложение.

Для этого вам необходимо поддержать Universal Links в вашем проекте.

#### Deep Link
Иногда iOS некорректно обрабатывает Universal Links и они перестают работать в приложении. В этом случае нужны Deep Links, чтобы вернуть пользователя из приложения VK, так как они работают всегда. В этом случае в ваше приложение не будет передана информация о пользователе, но он вернется из клиента VK. Вам нужно поддержать Deep Link вида: vkAPP_ID://, где, APP_ID — идентификатор приложения

#### Пример записи DeepLink в Info.plist
```xml
<key>CFBundleURLTypes</key>
<array>
  <dict>
      <key>CFBundleTypeRole</key>
      <string>Editor</string>
      <key>CFBundleURLName</key>
      <string>demo_app</string>
      <key>CFBundleURLSchemes</key>
      <array>
          <string>vk123456</string>
      </array>
  </dict>
</array>
```

#### Обработка ссылки авторизации

В классе ApplicationDelegate вам необходимо добавить менеджер обработки ссылок

```objective-c
#import <React/RCTLinkingManager.h>

- (BOOL)application:(UIApplication *)application
   openURL:(NSURL *)url
   options:(NSDictionary<UIApplicationOpenURLOptionsKey,id> *)options
{
  return [RCTLinkingManager application:application openURL:url options:options];
}

- (BOOL)application:(UIApplication *)application continueUserActivity:(nonnull NSUserActivity *)userActivity
 restorationHandler:(nonnull void (^)(NSArray<id<UIUserActivityRestoring>> * _Nullable))restorationHandler
{
 return [RCTLinkingManager application:application
                  continueUserActivity:userActivity
                    restorationHandler:restorationHandler];
}
```

Затем в Typescript необходимо импортировать класс Linking при помощи которого вы сможете подписаться на события открытия ссылок и в месте обработки ссылок вызвать метод openURL у класса VK.
```javascript
React.useEffect(() => {
    Linking.getInitialURL().then((url) => {
        if (url) {
            handleOpenURL({'url': url});
        }
    }).catch(err => {
        console.warn('An error occurred', err);
    });
        Linking.addEventListener('url', handleOpenURL);
});

function handleOpenURL(event: { url: string }) {
    VK.openURL(event.url);
}
```

## Minimal setup guide for Android part:
1. Добавить ваши credentials от артифактори в build.gradle проекта

```gradle
// project build.gradle
buildscript { }
allProjects {
  repositories {
    maven {
       url("https://artifactory-external.vkpartner.ru/artifactory/superappkit-maven-public/")
    }
  }
}
```

2. Добавить VkExternalAuthRedirectScheme и VkExternalAuthRedirectHost в build.gradle application’a:
```gradle
// app build.gradle
android { }
android.defaultConfig.manifestPlaceholders = [
    'VkExternalAuthRedirectScheme' : 'vk<ClientId>',
    'VkExternalAuthRedirectHost' : 'vk.com',
]

dependencies { }
```

3. Добавить client_id, client_secret, vk_external_oauth_redirect_url и vk_account_manager_id в strings.xml:
```xml
<integer name="com_vk_sdk_AppId">your_client_id</integer>

<string name="vk_client_secret" translatable="false">your_client_secret</string>

<!-- Template: vk<ClientId>://vk.com -->
<string name="vk_external_oauth_redirect_url" translatable="false">vk<ClientId>://vk.com</string>

<!-- Template: your.package.account -->
<string name="vk_account_manager_id" translatable="false">your.package.account</string>
```

## Minimal common setup guide:
1. Initialization

```javascript
import { VK, VKID } from 'react-native-superappkit-pub';

// must be initialized only once
let logo = Image.resolveAssetSource(require('./sample_logo.png'));
let vkid = new VKID(
    'Superappkit pub',
    '1.0',
    logo,
    {
        serviceUserAgreement: 'https://help.mail.ru/legal/terms/common/ua',
        servicePrivacyPolicy: 'https://help.mail.ru/legal/terms/common/privacy',
        serviceSupport: null,
    },
    new SilentTokenExchanger()
);

VK.initialize(
    {
        credentials: {
            clientId: 'your-client-id',
            clientSecret: 'your-client-secret',
        },
        mode: VK.Mode.DEBUG,
    },
    vkid
);
```

2. Auth
```javascript
// App.tsx

// 1. Silent Token Exchanger
class SilentTokenExchanger implements VKID.SilentTokenExchanger {
    exchange(silentData: VKID.SilentToken): Promise<VKID.TokenExchangeResult<VKID.AccessToken, Error>> {
        return fetch('your_endpoint_for_exchange_token', {
            method: 'POST',
            headers: {'Content-Type': 'application/json; charset=UTF-8'},
        })
        .then((response) => response.json())
        .then((body) => {
            let accessToken = 'received access_token';
            let userId = 'received user_id';
            let result: VKID.TokenExchangeResult = {
                ok: true,
                accessToken: {
                    token: new VKID.Token(accessToken),
                    userID: new VKID.UserID(userId),
                }
            };
            return result;
        });
    }
}

//2. Set observer for auth state changing
vkid.setOnAuthChanged(new class implements VKID.AuthChangedCallback {
    onAuth(userSession: VKID.Session.UserSession): void {
        if (userSession instanceof VKID.Session.Authorized) {
            // user was successfuly authorized, so show authorized flow or get user profile info
            userSession.userProfile.then((profileInfo) => {
                setProfileInfo("Profile info: " + profileInfo.userID.value + " " + profileInfo.firstName);
            })
        }
    }

    onLogout(): void {
        // logout
    }
})

// 2.1 Use One Tap Loggin button
import { VKOneTapButton } from 'react-native-superappkit-pub';
<VKOneTapButton />

// 2.2 OR use start auth manually
<Button title={'Auth by VK'} onPress={() => auth()} />
function auth() {
    vkid.startAuth();
}

// You may use force close
function forceCloseAuth() {
    vkid.closeAuth();
}

// Logout
function logout() {
    vkid.logout();
}

// Check is logged in
async function isLoggedIn() {
    let sessions = await vkid.getUserSessions();
    let isLoggedIn = sessions.some((session) => session instanceof VKID.UserSession.Authorized)
}
```

### One Tap Button customization
```javascript
import { VKOneTapButton, VKOneTapButtonSpace } from 'react-native-superappkit-pub';
<VKOneTapButton
    style={styles.vkView}
    backgroundStyle={{
        style: VKOneTapButtonSpace.BgColor.CUSTOM, /* or 'BLUE' (default) or 'WHITE' */
        customVkIconColor: '#your-color-if-using-custom-style',
        customBackgroundColor: '#your-color-if-using-custom-style',
        customTextColor: '#your-color-if-using-custom-style'
    }}
    iconGravity={
        // 'START' (by default) or 'TEXT'
        VKOneTapButtonSpace.IconGravity.START
    }
    firstLineFieldType={
        // 'NONE' (by default) or 'ACTION' or 'PHONE'
        VKOneTapButtonSpace.LineFieldType.ACTION
    }
    secondLineFieldType={
        // 'NONE' (by default) or 'ACTION' or 'PHONE'
        VKOneTapButtonSpace.LineFieldType.PHONE
    }
    texts={{
        noUserText: 'Login as VK',
        actionText: 'Login as {firstName} {lastName}', /* {firstName} and {lastName} are templates that will be replaced by real user names */
        phoneText: 'With phone {phone}', /* {phone} is a template that will be replaced by user phone */
    }}
    oneLineTextSize={ 16 }
    firstLineTextSize={ 16 }
    secondLineTextSize={ 14 }
    avatarSize={ 64 }
    iconSize={ 64 }
    progressSize={ 56 }
/>
```

## Usage

under development
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
