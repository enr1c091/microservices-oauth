# Docker Compose

Want the full solution running on Docker containers linked between themselves? Not a problem.

Make sure all projects are built, go to the parent folder of the project and build all child projects with:
```sh
$ mvn clean install
```
OR
```sh
$ mvn package -B -DskipTests
```

Run your containers:
```sh
$ docker-compose up -d
```

Check all containers are up and running:
```sh
$ docker ps
```

For stopping our stack:
```sh
$ docker-compose stop
```
