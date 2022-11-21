import { Injectable } from '@angular/core';
import { Plugin, Cordova, CordovaProperty, CordovaInstance, InstanceProperty, AwesomeCordovaNativePlugin } from '@awesome-cordova-plugins/core';

/**
 * @name VnPaySdk
 * @description
 * This plugin connect to Vn Pay Sdk
 *
 * @usage
 * ```typescript
 * import { VnPaySdk } from '@awesome-cordova-plugins/vn-pay-sdk';
 *
 *
 * constructor(private vnPaySdk: VnPaySdk) { }
 *
 * ...
 *
 *
 * this.vnPaySdk.openSdk('https://sandbox.vnpayment.vn/testsdk/', 'FAHASA03', 'resultactivity', 123)
 *   .then((res: any) => console.log(res))
 *   .catch((error: any) => console.error(error));
 *
 * ```
 */
@Plugin({
  pluginName: 'VnPaySdk',
  plugin: 'com.iii.vnpaysdk',
  pluginRef: 'cordova.plugins.VnPaySdk',
  repo: '',
  install: '',
  installVariables: [],
  platforms: ['android']
})
@Injectable()
export class VnPaySdk extends AwesomeCordovaNativePlugin {

  /**
   * This function call cordova vnpay sdk
   * @param url {string} url generated from merchant server
   * @param tmnCode {string} tmnCode granted by vnpay
   * @param scheme {string} scheme to return to your app when payment finish
   * @param isSandbox {boolean} is production or stage
   * @return {Promise<any>} Returns a promise that resolves when payment finish
   */
  @Cordova()
  openSdk(url: string, tmnCode: string, scheme: string, isSandbox: boolean): Promise<any> {
    return; // We add return; here to avoid any IDE / Compiler errors
  }

}
