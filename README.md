# Last.fm to Apple Music sync

Kotlin based web application that syncs Last.fm Top Album lists (time period of chosing) to the users Apple Music personal library.

Created from a personal need to have albums in my Apple Music library for creating easier playlists (normal and smart playlists).

Started out as a command line tool but the Apple Music API (more specifically, user functions) requires either a macOS/iOS app with MusicKit or a web based
application that uses [MusicKit JS](https://developer.apple.com/documentation/musickitjs) for obtaining a [User Token](https://developer.apple.com/documentation/applemusicapi/getting_keys_and_creating_tokens).
Therefore switched the application to a web application in Kotlin using Ktor (both client and server).

## Requirements

* Member of the Apple Developer program and setup a MusicKit identifier and key as listed in [this documentation](https://developer.apple.com/documentation/applemusicapi/getting_keys_and_creating_tokens)
* Last.fm API key/secret, can be obtained [here](https://www.last.fm/api/account/create)