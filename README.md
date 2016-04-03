# spacedentist-tv-android [![Build Status](https://travis-ci.org/spacedentist-tv/spacedentist-tv-android.svg?branch=master)](https://travis-ci.org/spacedentist-tv/spacedentist-tv-android)

The official Android Chromecast remote control app for http://spacedentist.tv

<a href="https://play.google.com/store/apps/details?id=tv.spacedentist.android&utm_source=global_co&utm_medium=prtnr&utm_content=Mar2515&utm_campaign=PartBadge&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1"><img alt="Get it on Google Play" height="40px" src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png" /></a>

# Instructions

It's an Android Gradle project. Probably use Android Studio. There are two flavors, prod and stag, that point to production and staging version of the receiver app respectively.

Build with `./gradlew assembleProdDebug`.

Install with `./gradlew installProdDebug`.

Run tests with `./gradlew lintProdDebug testProdDebugUnitTest`.

# Contribute

Create a pull request!  Our Travis setup will validate it with a build and test.
