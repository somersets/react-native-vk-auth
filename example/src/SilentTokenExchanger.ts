import { VKID } from 'react-native-superappkit-pub';

export class SilentTokenExchanger implements VKID.SilentTokenExchanger {
  exchange(
    silentData: VKID.SilentToken
  ): Promise<VKID.TokenExchangeResult<VKID.AccessToken, Error>> {
    return fetch('your_endpoint_for_exchange_token', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json; charset=UTF-8' },
    })
      .then((response) => response.json())
      .then((body) => {
        let accessToken = 'received access_token';
        let userId = 'received user_id';
        console.log(accessToken);
        console.log(userId);
        let result: VKID.TokenExchangeResult = {
          ok: true,
          accessToken: {
            token: new VKID.Token(body.accessToken),
            // @ts-ignore
            userID: new VKID.UserID(body.userID),
          },
        };
        return result;
      });
  }
}
