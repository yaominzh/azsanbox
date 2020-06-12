docker run --name mean-mongo  \
      -e MONGO_INITDB_ROOT_USERNAME=admin \
      -e MONGO_INITDB_ROOT_PASSWORD=password \
-p 27017:27017 -v /Volumes/AZT1000/dockerdata/mean-mongo:/data/db -d mongo