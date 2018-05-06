#!/usr/bin/env sh

# ARG is bootRun or deploy
case $1 in
    "bootRun" )
        cp -f ./src/main/resources/application-dev.yml ./src/main/resources/application.yml
        ./gradlew bootRun
    ;;
    "deploy" )
        echo "-- deploy start to local dockerShare --"
        cp -f ./src/main/resources/application-docker.yml ./src/main/resources/application.yml
        ./gradlew build

        # clean
        rm -f ./build/libs/movie-store.jar
        rm -f ./build/libs/movie-store-1.0.0.jar.original
        rm -f ../../dockerShare/movie-store/movie-store.jar

        # deploy
        mv ./build/libs/movie-store-1.0.0.jar ./build/libs/movie-store.jar
        cp ./build/libs/movie-store.jar ../../dockerShare/movie-store/
        echo "-- deploy is done --"
    ;;
    *)
        echo " Please specified bootRun or deploy."
esac
