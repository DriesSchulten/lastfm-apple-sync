# Last.fm to Apple Music sync

Kotlin based web application that syncs Last.fm Top Album lists (time period of chosing) to the users Apple Music personal library.

Created from a personal need to have albums in my Apple Music library for creating easier playlists (normal and smart playlists).

Started out as a command line tool but the Apple Music API (more specifically, user functions) requires either a macOS/iOS app with MusicKit or a web based
application that uses [MusicKit JS](https://developer.apple.com/documentation/musickitjs) for obtaining
a [User Token](https://developer.apple.com/documentation/applemusicapi/getting_keys_and_creating_tokens). Therefore, switched the application to a web
application in Kotlin using Ktor (both client and server).

## Requirements

* Member of the Apple Developer program and setup a MusicKit identifier and key as listed
  in [this documentation](https://developer.apple.com/documentation/applemusicapi/getting_keys_and_creating_tokens)
* Last.fm API key/secret, can be obtained [here](https://www.last.fm/api/account/create)

See the `application.conf` for required configuration values.

## Noteworthy API routes

To set up an Apple Music user token start the application and go to `http://localhost:8080/apple-music` to authorize the application to use your Apple Music
account and obtain the tokens. When everything is set up revisiting the page allows to disconnect the application from your Apple Music account.

The sync runs through cron (with Quartz) as configured in the `application.conf`, to manually start the sync visit `http://localhost:8080/sync`, to show
current (or last, if any) sync status overview go to `http://localhost:8080/sync-status`.