<#--
npx npm-check-updates -u;
-->
{
  "$schema": "http://json.schemastore.org/package",
  "author": "wcarmon",
  "dependencies": {
    "@angular-material-components/color-picker": "~6.0.0",
    "@angular/animations": "~12.2.5",
    "@angular/cdk": "~12.2.5",
    "@angular/common": "~12.2.5",
    "@angular/compiler": "~12.2.5",
    "@angular/core": "~12.2.5",
    "@angular/forms": "~12.2.5",
    "@angular/material": "~12.2.5",
    "@angular/platform-browser": "~12.2.5",
    "@angular/platform-browser-dynamic": "~12.2.5",
    "@angular/router": "~12.2.5",
    "auto-bind": "~4.0.0",
    "axios": "~0.21.4",
    "idb": "~6.1.3",
    "lodash-es": "~4.17.21",
    "normalize.css": "~8.0.1",
    "rxjs": "~7.3.0",
    "rxjs-es": "~5.0.0-beta.12",
    "tslib": "~2.3.1",
    "ub-common": "1.0.0",
    "ub-net": "1.0.0",
    "ub-persistence": "1.0.0",
    "uuid": "~8.3.2",
    "winston": "~3.3.3",
    "zone.js": "~0.11.4"
  },
  "description": "codegen-node-sandbox",
  "devDependencies": {
    "@angular-devkit/build-angular": "~12.2.5",
    "@angular/cli": "~12.2.5",
    "@angular/compiler-cli": "~12.2.5",
    "@types/chai": "~4.2.21",
    "@types/jasmine": "~3.9.0",
    "@types/lodash": "~4.14.172",
    "@types/lodash-es": "~4.17.4",
    "@types/mocha": "~9.0.0",
    "@types/node": "~16.9.1",
    "@types/uuid": "~8.3.1",
    "@typescript-eslint/eslint-plugin": "~4.31.0",
    "@typescript-eslint/parser": "~4.31.0",
    "barrelsby": "~2.2.0",
    "chai": "~4.3.4",
    "codelyzer": "~6.0.2",
    "eslint": "~7.32.0",
    "eslint-plugin-import": "~2.24.2",
    "eslint-plugin-sonarjs": "~0.10.0",
    "gulp": "~4.0.2",
    "jasmine": "~3.9.0",
    "jasmine-core": "~3.9.0",
    "jasmine-spec-reporter": "~7.0.0",
    "karma": "~6.3.4",
    "karma-chrome-launcher": "~3.1.0",
    "karma-coverage": "~2.0.3",
    "karma-jasmine": "~4.0.1",
    "karma-jasmine-html-reporter": "~1.7.0",
    "mocha": "~9.1.1",
    "prettier": "~2.4.0",
    "protractor": "~7.0.0",
    "ts-node": "~10.2.1",
    "typescript": "~4.4.2"
  },
  "directories": {
    "test": "test"
  },
  "license": "ISC",
  "main": "dist/index.js",
  "name": "codegen-node-sandbox",
  "private": true,
  "scripts": {
    "build": "ng build",
    "e2e": "ng e2e",
    "lint": "ng lint",
    "ng": "ng",
    "pretty": "prettier --write ./src/**/*.{ts,tsx,js,jsx,json}",
    "start": "ng serve",
    "test": "ng test"
  },
  "types": "dist/index.d.ts",
  "version": "1.0.0"
}
