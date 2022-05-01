# httpviewer2

## build
```
sbt stage
```

## dev

### backend
first once `sbt stage` to build js assets

```
bloop run appJVM -- -J-Dassets=jvm/target/universal/stage/assets
```
or
```
set -a
. local.env
bloop run appJVM
```

### js
```
fastOptJS / webpack
xdg-open js/src/main/resources/index-dev.html
~fastOptJS
```
