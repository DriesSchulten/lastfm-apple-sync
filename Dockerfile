FROM eclipse-temurin:11
EXPOSE 8080:8080
RUN mkdir /app
COPY ./build/install/lastfm-apple-sync/ /app/
WORKDIR /app/bin
CMD ["./lastfm-apple-sync"]