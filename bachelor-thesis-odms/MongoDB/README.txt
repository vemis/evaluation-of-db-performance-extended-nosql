docker run -d \
  --name mongodb \
  -p 27017:27017 \
  -v "$HOME/mongo-data":/data/db \
  mongo:latest