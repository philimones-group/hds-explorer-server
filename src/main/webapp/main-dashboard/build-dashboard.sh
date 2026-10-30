#!/bin/bash
npm run build
cp dist/index-bundle-dashboard.js ../../../../grails-app/assets/javascripts/index-bundle-dashboard.min.js
cp dist/index-bundle-dashboard.css ../../../../grails-app/assets/stylesheets/index-bundle-dashboard.min.css
