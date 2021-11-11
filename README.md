# Last.fm to Apple Music sync

Kotlin based web application that syncs Last.fm Top Album lists (time period of chosing) to the users Apple Music personal library.

Created from a personal need to have albums in my Apple Music library for creating easier playlists (normal and smart playlists).

Started out as a command line tool but the Apple Music API (more specifically, user functions) requires either a macOS/iOS app with MusicKit or a web based
application that uses [MusicKit JS](https://developer.apple.com/documentation/musickitjs) for obtaining
a [User Token](https://developer.apple.com/documentation/applemusicapi/getting_keys_and_creating_tokens). Therefore, switched the application to a web
application in Kotlin using Ktor (both client and server) and a (simple) ReactJS frontend.

## Requirements

* Member of the Apple Developer program and setup a MusicKit identifier and key as listed
  in [this documentation](https://developer.apple.com/documentation/applemusicapi/getting_keys_and_creating_tokens)
* Last.fm API key/secret, can be obtained [here](https://www.last.fm/api/account/create)

See the `application.conf` for required configuration values.

## Running locally

Run the NPM build to generate the CSS (added as resource source-set to the Gradle build):

```shell
cd frontend
npm install
npm run build
```

Either adjust the `application.conf` or provide the env vars required (see Docker compose template).

Run the Gradle build:

```shell
./gradlew run
```

Open a browser on [`http://localhost:8080`](http://localhost:8080) and follow from there. The application will ask you to set up Apple Music tokens, follow this
procedure. If tokens have been setup the main page allows you to manually trigger a sync and show the status of the last sync (when, any issues/errors).

## Docker

[Docker image](https://hub.docker.com/repository/docker/driesschulten/lastfm-apple-sync) (also for RaspberryPI), a template for `docker-compose.yml`:

```yaml
version: "3"

services:
  lastfm-apple-sync:
    container_name: lastfm-apple-sync
    image: driesschulten/lastfm-apple-sync:latest
    ports:
      - "8080:8080/tcp"
    volumes:
      - '<local dir>:/app-etc' # For the storage/key
    restart: always
    environment:
      - LASTFM_API_KEY=<your key>
      - LASTFM_SHARED_SECRET=<your secret>
      - LASTFM_USER=<user name>
      - APPLE_TEAM_ID=<your Team ID>>
      - APPLE_KEY_ID=<your Key ID>
      - APPLE_KEY_FILE=app-etc/<pad to key>
      - STORAGE_DIRECTORY=app-etc/<storage dir>>
```