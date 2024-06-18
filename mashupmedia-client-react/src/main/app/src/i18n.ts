import { initReactI18next } from "react-i18next";
import i18n from "i18next";
import commonEn from './locale/common_en.json';


// const resources = {
//   en: {
//     translation: {
//       "Welcome to React": "Welcome to React and react-i18next"
//     }
//   },
//   fr: {
//     translation: {
//       "Welcome to React": "Bienvenue à React et react-i18next"
//     }
//   }
// };


const resources = {
  en: {
    common: commonEn,
  }
};

const defaultNS = 'common';

i18n
  .use(initReactI18next) // passes i18n down to react-i18next
  .init({
    resources,
    ns: [defaultNS],
    defaultNS,
    lng: "en", // language to use, more information here: https://www.i18next.com/overview/configuration-options#languages-namespaces-resources
    // you can use the i18n.changeLanguage function to change the language manually: https://www.i18next.com/overview/api#changelanguage
    // if you're using a language detector, do not define the lng option

    interpolation: {
      escapeValue: false // react already safes from xss
    }
  });

  export default i18n;