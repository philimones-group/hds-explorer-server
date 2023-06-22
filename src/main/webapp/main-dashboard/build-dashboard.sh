#!/bin/bash
npm run build
cp dist/index-bundle-dashboard.js ../../../../grails-app/assets/javascripts/
cp dist/index-bundle-dashboard.css ../../../../grails-app/assets/stylesheets/
