import * as React from 'react';

import { StyleSheet, View, Text, Image } from 'react-native';
import DeviceInfo from 'react-native-device-info';
import { VK, VKID } from 'react-native-superappkit-pub';
import { SilentTokenExchanger } from './SilentTokenExchanger';

export default function App() {
  const [result, setResult] = React.useState<number | undefined>();

  React.useEffect(() => {
    // multiply(3, 7).then(setResult);
    //
    initialize()
  }, []);

  const initialize = () => {
    let logo = Image.resolveAssetSource(require('./sample_logo.png'));
    let vkid = new VKID(
      'VkAuth',
      DeviceInfo.getVersion(),
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
          clientId: '51561139',
          clientSecret: 'sOFnJD6DlOsUnJcR5N97',
        },
        mode: VK.Mode.DEBUG,
      },
      vkid
    );
  };

  return (
    <View style={styles.container}>
      <Text>Result: {result}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
