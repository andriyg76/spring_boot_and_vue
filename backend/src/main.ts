import Vue from 'vue'
import App from './App.vue'

Vue.config.productionTip = false

import VueLogger from 'vuejs-logger/dist/vue-logger';

Vue.use(VueLogger, {
  isEnabled: true,
  logLevel : process.env.NODE_ENV === 'production' ? 'error' : 'debug',
  stringifyArguments : false,
  showLogLevel : true,
  showMethodName : true,
  separator: '|',
  showConsoleColors: true
});

/* eslint-disable no-new */
new Vue({
  render: h => h(App),
}).$mount('#app')
